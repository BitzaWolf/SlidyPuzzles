package convcomm.engine.util;

import java.awt.Graphics2D;
import java.awt.Color;

public class FPSCounter
{
	private int lastFPS;
	private int frames;
	private long timer;
	
	public FPSCounter()
	{
		lastFPS = 0;
		frames = 0;
		timer = 1000;
	}
	
	public void reset()
	{
		lastFPS = 0;
		frames = 0;
		timer = 1000;
	}
	
	public void update(long time)
	{
		++frames;
		timer -= time;
		if (timer <= 0)
		{
			lastFPS = frames;
			frames = 0;
			timer += 1000;
		}
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(10, 10, 60, 20);
		g.setColor(Color.WHITE);
		g.drawString("" + lastFPS + " FPS", 11, 25);
	}
	
	public int getLastFPS()
	{
		return lastFPS;
	}
}