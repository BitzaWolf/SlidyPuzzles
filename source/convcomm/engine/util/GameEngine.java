package convcomm.engine.util;

import convcomm.engine.graphics.*;
import convcomm.engine.input.InputManager;
import convcomm.engine.audio.SoundManager;

import java.awt.geom.*;
import java.awt.*;
import javax.swing.ImageIcon;

/**
**	The class that organizes all parts of the game into one location. To create a game using this engine, extend this
**		class and modify the methods as needed, such as Init to run code as the game starts up, update, and most importantly
**		draw. You do not need to worry about a game loop or when to refresh the screen. Just override the draw method and draw!
**		Override the update method and update your game state!
**/
public class GameEngine extends EmptyGameLoop
{
	private boolean isRunning;
	private boolean paused;
	private GameLoop currentLoop;
	
	/**Keeps track of the frames per second in the game. Draw to the screen if you want, or just forget about it.**/
	protected FPSCounter fpsCounter;
	
	/**Use to reference the screen size, change size, make full-screen, etc.**/
	protected ScreenManager screen;
	
	/**Use to bind Virtual Keys to input devices like the keyboard and mouse.**/
	protected InputManager inputManager;
	
	/**Use to play sounds and music.**/
	protected SoundManager soundManager;
	
	/**
	**	Starts and runs the game in a new window of the specified width, height, and if it is full-screen.
	**/
	public void run(int width, int height, boolean fullScreen)
	{
		try
		{
			screen = new ScreenManager(width, height, fullScreen);
			init();
			gameLoop();
		}
		finally
		{
			screen.end();
			if (soundManager != null)
				soundManager.close();
		}
	}
	
	/**
	**	Stops the game and closes the window.
	**/
	public void stop()
	{
		isRunning = false;
	}
	
	/**
	**	Sets some default settings for the game engine.
	**/
	public void init()
	{
		inputManager = new InputManager(screen.getGameWindow());
		if (SoundManager.hasAudioMixer())
			soundManager = new SoundManager();
		else
			soundManager = null;
		fpsCounter = new FPSCounter();
		
		screen.getGameWindow().setBackground(Color.BLACK);
		screen.getGameWindow().setForeground(Color.WHITE);
		
		isRunning = true;
		paused = false;
		currentLoop = null;
	}
	
	/**
	** Loads an Image based on a relative file name from the base location.
	**/
	protected static Image loadImage(String fileName)
	{
		return new ImageIcon(fileName).getImage();
	}
	
	/**
	** The game loop. Updates, draws, and that's about it.
	**/
	public void gameLoop()
	{
		long startTime = System.currentTimeMillis();
		long currTime = startTime;
		while (isRunning)
		{
			long elapsedTime = System.currentTimeMillis() - currTime;
			currTime += elapsedTime;
			
			if (currentLoop != null)
			{
				if (currentLoop.continueLoop())
					runLoop(currentLoop, elapsedTime);
				else
					currentLoop = currentLoop.nextLoop();
			}
			else
			{
				if (continueLoop())
					runLoop(this, elapsedTime);
				else
				{
					currentLoop = nextLoop();
					if (currentLoop == null) // Even this engine loop has no next loop, and wants to stop running: quit
						stop();
				}
			}
			if (fpsCounter.getLastFPS() > 120)
			{
				try
				{
					Thread.sleep(5);
				}
				catch (InterruptedException ie) {}
			}
		}
	}
	
	private void runLoop(GameLoop loop, long elapsedTime)
	{
		fpsCounter.update(elapsedTime);
		
		loop.globalUpdate(elapsedTime);
		if (isPaused())
			loop.pausedUpdate(elapsedTime);
		else
			loop.unpausedUpdate(elapsedTime);
		
		Graphics2D g = screen.getGraphics();
		wipeScreen(g);
		loop.globalDraw(g);
		if (isPaused())
			loop.pausedDraw(g);
		else
			loop.unpausedDraw(g);
		g.dispose();
		screen.update();
	}
	
	private void wipeScreen(Graphics2D g)
	{
		Color temp = g.getColor();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		g.setColor(temp);
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public void setPaused(boolean p)
	{
		if (isPaused() != p)
		{
			paused = p;
			inputManager.resetAllVirtualKeys();
		}
	}
	
	public void togglePaused()
	{
		setPaused(! isPaused());
	}
	
	public InputManager getInputManager()
	{
		return inputManager;
	}
	
	public ScreenManager getScreenManager()
	{
		return screen;
	}
	
	public SoundManager getSoundManager()
	{
		return soundManager;
	}
	
	public FPSCounter getFPSCounter()
	{
		return fpsCounter;
	}
	
	protected void setCurrentLoop(GameLoop loop)
	{
		if (loop != null && currentLoop == null)
			currentLoop = loop;
	}
	
	public boolean continueLoop()
	{
		return true;
	}
}