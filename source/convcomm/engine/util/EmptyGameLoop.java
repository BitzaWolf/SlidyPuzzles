package convcomm.engine.util;

import java.awt.Graphics2D;

/**
**	A completely empty implementation of GameLoop. Use this class as a base class and just
**		override whatever you need when you need a Game Loop.
**/
public class EmptyGameLoop implements GameLoop
{
	public void unpausedDraw(Graphics2D g) {}
	public void pausedDraw(Graphics2D g) {}
	public void globalDraw(Graphics2D g) {}
	public void unpausedUpdate(long time) {}
	public void pausedUpdate(long time) {}
	public void globalUpdate(long time) {}
	/**Returns false.**/
	public boolean continueLoop() {return false;}
	/**Returns null.**/
	public GameLoop nextLoop() {return null;}
}