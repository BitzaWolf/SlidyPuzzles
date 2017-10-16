package convcomm.engine.graphics;

import java.awt.Image;
import java.util.ArrayList;

/**
**	Animation manages a series of images (frames) and the amount of time to display each frame.
**	@author David Brackeen
**/
public class Animation
{
	private ArrayList<AnimFrame> frames;
	private int currFrameIndex;
	private long animTime;
	private long totalDuration;
	
	/**
	**	Creates a new Animation with 0 frames.
	**/
	public Animation()
	{
		frames = new ArrayList<AnimFrame>();
		totalDuration = 0;
		start();
	}
	
	/**
	**	Restarts the animation completely. Sets the internal timer to 0 seconds elapsed and points to frame 0.
	**/
	public synchronized void start()
	{
		animTime = 0;
		currFrameIndex = 0;
	}
	
	/**
	**	Adds an image to the end of the animation to be display for the specificed duration (millis).
	**	@param image Frame to display.
	**	@param duration Length in milliseconds to display this frame.
	**/
	public synchronized void addFrame(Image image, long duration)
	{
		totalDuration += duration;
		frames.add(new AnimFrame(image, totalDuration));
	}
	
	/**
	**	Updates the internal timer and changes image (frame) if necessary.
	**	@param elapsedTime Milliseconds passed since the last call to this method.
	**/
	public synchronized void update(long elapsedTime)
	{
		if (frames.size() > 1)
		{
			animTime += elapsedTime;
			if (animTime >= totalDuration)
			{
				animTime %= totalDuration;
				currFrameIndex = 0;
			}
			while (animTime > frames.get(currFrameIndex).endTime)
				++currFrameIndex;
		}
	}
	
	/**
	**	Gets the animation's current image or frame. Returns null if there are no frames/images.
	**	@return The current image, or null if the animation has no frames.
	**/
	public synchronized Image getImage()
	{
		if (frames.size() == 0)
			return null;
		else
			return frames.get(currFrameIndex).image;
	}
	
	private class AnimFrame
	{
		Image image;
		long endTime;
		
		public AnimFrame(Image image, long endTime)
		{
			this.image = image;
			this.endTime = endTime;
		}
	}
}