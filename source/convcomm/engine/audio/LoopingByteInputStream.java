package convcomm.engine.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
**	Loops a ByteArrayInputStream indefinitely. Stops when close() is called
**/
public class LoopingByteInputStream extends ByteArrayInputStream
{
	private boolean closed;
	
	/**
	**	Creates a new LoopingByteInputStream with the specified byte array. The array is not copied.
	**/
	public LoopingByteInputStream(byte[] buffer)
	{
		super(buffer);
		closed = false;
	}
	
	/**
	**	Reads <code>length</code> bytes from the array. If the end of the array is reach the reading starts over from the beginning. Returns -1 if it has been closed
	**/
	public int read(byte[] buffer, int offset, int length)
	{
		if (closed)
			return -1;
		int totalBytesRead = 0;
		while (totalBytesRead < length)
		{
			int numBytesRead = super.read(buffer, offset + totalBytesRead, length - totalBytesRead);
			
			if (numBytesRead > 0)
				totalBytesRead += numBytesRead;
			else
				reset();
		}
		return totalBytesRead;
	}
	
	public void close() throws IOException
	{
		super.close();
		closed = true;
	}
}