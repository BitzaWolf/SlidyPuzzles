package convcomm.engine.audio;

import convcomm.engine.util.ThreadPool;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;

public class SoundManager extends ThreadPool
{
	private static final AudioFormat FORMAT = new AudioFormat(44100, 16, 2, true, false);
	private static final int FRAME_SIZE = 4;
	private static final int MONO_FRAME_SIZE = 2;
	private static final int SAMPLE_RATE = 44100;
	private static final int MAX_LINES = 32;
	
	private ThreadLocal<SourceDataLine> localLine;
	private ThreadLocal<byte[]> localBuffer;
	private Object pauseLock, stopLock;
	private boolean paused, stopped;
	
	public static boolean hasAudioMixer()
	{
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		return (mixers.length > 0);
	}
	
	public SoundManager()
	{
		this(getMaxSimultaneousSounds(FORMAT));
	}
	
	public SoundManager(int maxSimultaneousSounds)
	{
		super(Math.min(maxSimultaneousSounds, getMaxSimultaneousSounds(FORMAT)));
		
		if (maxSimultaneousSounds == -1)
			return;
		
		localLine = new ThreadLocal<SourceDataLine>();
		localBuffer = new ThreadLocal<byte[]>();
		pauseLock = new Object();
		stopLock = new Object();
		synchronized(this)
		{
			notifyAll();
		}
	}
	
	public Sound loadSound(URL path)
	{
		return loadSound(path, true);
	}
	
	public Sound loadSound(String filePath)
	{
		return loadSound(this.getClass().getResource(filePath), true);
	}
	
	public Sound loadSound(URL file, boolean pauseable)
	{
		AudioInputStream stream = null;
		try
		{
			stream = AudioSystem.getAudioInputStream(file);
		}
		catch (UnsupportedAudioFileException uafe)
		{
			uafe.printStackTrace();
			return null;
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			return null;
		}
		
		if (stream == null)
			return null;
		
		int length = (int) (stream.getFrameLength() * FRAME_SIZE);
		int monoLength = (int) (stream.getFrameLength() * MONO_FRAME_SIZE);
		
		byte[] samples = new byte[length];
		byte[] monoSamples = new byte[monoLength];
		DataInputStream dataStream = new DataInputStream(stream);
		try
		{
			dataStream.readFully(monoSamples);
			dataStream.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		convertToStereo(monoSamples, samples);
		return new Sound(samples, pauseable);
	}
	
	public static int getMaxSimultaneousSounds(AudioFormat format)
	{
		DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format);
		int maxLines = AudioSystem.getMixer(null).getMaxLines(lineInfo);
		if (maxLines == AudioSystem.NOT_SPECIFIED)
			return MAX_LINES;
		return maxLines;
	}
	
	public void close()
	{
		cleanUp();
		super.close();
	}
	
	public void join()
	{
		cleanUp();
		super.join();
	}
	
	public void reset()
	{
		synchronized (stopLock)
		{
			stopped = true;
		}
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException ie)
		{
			
		}
		synchronized (stopLock)
		{
			stopped = false;
		}
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public void setPaused(boolean paused)
	{
		if (this.paused != paused)
		{
			synchronized (pauseLock)
			{
				this.paused = paused;
				if (!paused);
					pauseLock.notifyAll();
			}
		}
	}
	
	public void play(Sound sound)
	{
		play(sound, null, false);
	}
	
	public void play(Sound sound, SoundFilter filter, boolean looping)
	{
		InputStream stream;
		if (sound == null)
			return;
		
		if (looping)
			stream = new LoopingByteInputStream(sound.getSamples());
		else
			stream = new ByteArrayInputStream(sound.getSamples());
		
		if (filter != null)
			stream = new FilteredSoundStream(stream, filter);
		
		super.runTask(new PlaySoundTask(stream, sound.isPauseable()));
	}
	
	public void playMusic(String filePath)
	{
		URL file = this.getClass().getResource(filePath);
		AudioInputStream stream = null;
		try
		{
			stream = AudioSystem.getAudioInputStream(file);
		}
		catch (UnsupportedAudioFileException uafe)
		{
			uafe.printStackTrace();
			return;
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			return;
		}
		stream = AudioSystem.getAudioInputStream(FORMAT, stream);
		
		super.runTask(new PlaySoundTask(stream, false));
	}
	
	@Override
	protected void threadStarted()
	{
		/*synchronized (this)
		{
			try
			{
				this.wait();
			}
			catch (InterruptedException ie)
			{
				
			}
		}*/
		
		// Create a buffer to house 1/10 of a second of playback
		int bufferSize = FRAME_SIZE * Math.round(SAMPLE_RATE / 10);
		
		SourceDataLine line;
		try
		{
			line = AudioSystem.getSourceDataLine(FORMAT);
			line.open(FORMAT, bufferSize);
		}
		catch (LineUnavailableException ex)
		{
			Thread.currentThread().interrupt();
			return;
		}
		
		line.start();
		
		byte[] buffer = new byte[bufferSize];
		
		localLine.set(line);
		localBuffer.set(buffer);
	}
	
	protected void threadStopped()
	{
		SourceDataLine line = (SourceDataLine) localLine.get();
		if (line != null)
		{
			line.drain();
			line.close();
		}
	}
	
	protected void cleanUp()
	{
		setPaused(false);
		
		Mixer mixer = AudioSystem.getMixer(null);
		if (mixer.isOpen())
			mixer.close();
	}
	
	protected class PlaySoundTask implements Runnable
	{
		private InputStream stream;
		private final boolean pauseable;
		
		public PlaySoundTask(InputStream source, boolean pauseable)
		{
			this.stream = source;
			this.pauseable = pauseable;
		}
		
		public void run()
		{
			threadStarted();
			SourceDataLine line = localLine.get();
			byte[] buffer = localBuffer.get();
			if (line == null || buffer == null)
			{
				System.out.println("line = " + line + "\nbuffer = " + buffer);
				return;
			}
			try
			{
				int bytesRead = 0;
				while (bytesRead != -1)
				{
					if (pauseable)
					{
						synchronized (pauseLock)
						{
							if (paused)
							{
								try
								{
									pauseLock.wait();
								}
								catch (InterruptedException ie)
								{
									return;
								}
							}
						}
					}
					synchronized (stopLock)
					{
						if (stopped)
							return;
					}
					bytesRead = stream.read(buffer, 0, buffer.length);
					if (bytesRead != -1)
						line.write(buffer, 0, bytesRead);
				}
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
	
	private void convertToStereo(byte[] monoSamples, byte[] stereoSamples)
	{
		int j = 0;
		for(int i = 0; i < monoSamples.length; i += 2)
		{
			stereoSamples[j] = monoSamples[i];
			stereoSamples[j + 2] = monoSamples[i];
			stereoSamples[j + 1] = monoSamples[i + 1];
			stereoSamples[j + 3] = monoSamples[i + 1];
			j += 4;
		}
	}
}