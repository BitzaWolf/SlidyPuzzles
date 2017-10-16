package convcomm.engine.audio;

/**
**	Designed to filter sound samples. Since SoundFilters may use internal buffering of samples, a new SoundFilter
**		object should be created for every sound played. However, SoundFilters can be reused after they are finished
**		by calling reset(). Assumes all samples are 16-bit stereo signed little-endian.
**/
public abstract class SoundFilter
{
	public void reset()
	{
		
	}
	
	/**
	**	Returns the remaining size, in bytes, that this filter plays after the sound is finished. An example would
	**		be an echo that plays longer than its original sound. Returns 0 by default.
	**/
	public int getRemainingSize()
	{
		return 0;
	}
	
	/**
	**	Filters an array of samples. Samples should be 16-bit signed little-endian stereo
	**/
	public void filter(byte[] samples)
	{
		filter(samples, 0, samples.length);
	}
	
	/**
	**	Filters an array of sampels. This methd should be implemented by subclasses. Note that the offset and length
	**		are number of bytes, not samples.
	**/
	public abstract void filter(byte[] samples, int offset, int length);
	
	/**
	**	Convience method for getting a sample from a byte array.
	**/
	public static short getLeftSample(byte[] buffer, int position)
	{
		return (short) (((buffer[position + 1] & 0xFF) << 8) | (buffer[position] & 0xFF));
	}
	
	/**
	**	Convience method for getting a Right sample from a byte array.
	**/
	public static short getRightSample(byte[] buffer, int position)
	{
		return (short) (((buffer[position + 3] & 0xFF) << 8) | (buffer[position + 2] & 0xFF));
	}
	
	/**
	**	Convience method for setting a 16-bit stereo left sample in a byte array.
	**/
	public static void setLeftSample(byte[] buffer, int position, short sample)
	{
		buffer[position] = (byte) (sample & 0xFF); // & 0xFF makes the byte keep its absolute number, so when it becomes an int it doesn't become negative (all 1's on the left)
		buffer[position+1] = (byte) ((sample >> 8) & 0xFF); // >> 8 makes the "left" byte shift into the right position, so when the short becomes a byte the left bytes are chopped off.
	}
	
	/**
	**	Convience method for setting a 16-bit stereo right sample in a byte array.
	**/
	public static void setRightSample(byte[] buffer, int position, short sample)
	{
		buffer[position + 2] = (byte) (sample & 0xFF);
		buffer[position + 3] = (byte) ((sample >> 8) & 0xFF);
	}
	
	/**
	**	Convience method for setting both 16-bit stereo samples in a byte array.
	**/
	public static void setSample(byte[] buffer, int position, short leftSample, short rightSample)
	{
		setLeftSample(buffer, position, leftSample);
		setRightSample(buffer, position, rightSample);
	}
}