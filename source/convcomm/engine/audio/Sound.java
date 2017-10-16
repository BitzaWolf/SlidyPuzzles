package convcomm.engine.audio;

public class Sound
{
	private byte[] samples;
	private boolean pauseable;
	
	/**
	**	Creates a new Sound object, setting the sound to say it doesn't allow pausing.
	**/
	public Sound(byte[] samples)
	{
		this(samples, false);
	}
	
	/**
	**	Create a new Sound object with the specified byte array.
	**	The array is not copied.
	**/
	public Sound(byte[] samples, boolean pauseable)
	{
		this.samples = samples;
		this.pauseable = pauseable;
	}
	
	/**
	**	Returns this sound's objects samples as a byte array
	**/
	public byte[] getSamples()
	{
		return samples;
	}
	
	public boolean isPauseable()
	{
		return pauseable;
	}
}