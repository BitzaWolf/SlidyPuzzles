package convcomm.engine.graphics;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.RepaintManager;

/**
**	Manages a screen to draw to as well as sizing, what size options are available for full screen, and going full-screen.
**		This class does not manage an animation loop or game loop of any kind, simply this class just facilitates working
**		with the window to draw on. Namely, call <code>getGraphics()</code> to get the Graphics object to draw to this
**		window with. Once you're done drawing, call <code>update()</code>. Finally, call <code>getGameWindow()</code>
**		to the the window this class is using to display to. Those are the most important facts you need to know about
**		this class, everything else supplements setting the game window's size and making it full-screen.
**	
**	<p>Based on work by David Brackeen in Developing Games in Java.</p>
**/
public class ScreenManager
{
	private boolean AWTEnabled;
	private GraphicsDevice device;
	private JFrame gameWindow;
	
	/**
	**	Creates a new game screen using the specified width and height.
	**/
	public ScreenManager(int width, int height, boolean fullScreen)
	{
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		JFrame frame = new JFrame();
		
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		gameWindow = frame;
		
		enableAWTPaint(false);
		
		if (fullScreen)
			setFullScreen(true);
		
		frame.createBufferStrategy(2);
	}
	
	public boolean isAWTEnabled()
	{
		return AWTEnabled;
	}
	
	public void enableAWTPaint(boolean b)
	{
		if (AWTEnabled && !b)
		{
			gameWindow.setIgnoreRepaint(true);
			NullRepaintManager.install();
			gameWindow.getContentPane().setIgnoreRepaint(true);
			gameWindow.getLayeredPane().setIgnoreRepaint(true);
			AWTEnabled = false;
		}
		else if (! AWTEnabled && b)
		{
			gameWindow.setIgnoreRepaint(false);
			RepaintManager repaintManager = new RepaintManager();
			RepaintManager.setCurrentManager(repaintManager);
			gameWindow.getContentPane().setIgnoreRepaint(false);
			gameWindow.getLayeredPane().setIgnoreRepaint(false);
			AWTEnabled = true;
		}
	}
	
	/**
	**	Returns a list of all display modes available.
	**/
	public DisplayMode[] getCompatibleDisplayModes()
	{
		return device.getDisplayModes();
	}
	
	/**
	**	Returns the current display mode being used.
	**/
	public DisplayMode getCurrentDisplayMode()
	{
		return device.getDisplayMode();
	}
	
	/**
	**	Changes the size of the game window. If the window is an innappropriate size to go full screen with, full screen
	**		will be disabled. To get the appropriate sizes, call <code>getCompatibleDisplayModes()</code>. If you're not
	**		using full screen, then the game window will change to any size, always centering in the middle of the screen.
	**/
	public void setSize(int width, int height)
	{
		gameWindow.setSize(width, height);
		if (device.getFullScreenWindow() == null)
			gameWindow.setLocationRelativeTo(null);
	}
	
	/**
	** Changes the full-screen status of the game window. True will set the game window to full screen using the window's current size.
	**		False will make the game window windowed, keeping the same size.
	**/
	public void setFullScreen(boolean setFull)
	{
		if (setFull && device.getFullScreenWindow() == null)
		{
			DisplayMode mode = new DisplayMode(gameWindow.getWidth(), gameWindow.getHeight(), 32, DisplayMode.REFRESH_RATE_UNKNOWN);
			device.setFullScreenWindow(gameWindow);
			if (mode != null && device.isDisplayChangeSupported())
			{
				try
				{
					device.setDisplayMode(mode);
				}
				catch (IllegalArgumentException ex) {}
			}
		}
		else if (!setFull && device.getFullScreenWindow() != null)
		{
			device.setFullScreenWindow(null);
		}
	}
	
	/**
	**	Obtains the Graphics2D to draw to the screen with.
	**/
	public Graphics2D getGraphics()
	{
		return (Graphics2D) (gameWindow.getBufferStrategy().getDrawGraphics());
	}
	
	public JFrame getGameWindow()
	{
		return gameWindow;
	}
	
	/**
	**	Displays the drawings that have occured since <code>update()</code> was last called. This method must be called after drawing
	**		everything inside the game-loop.
	**/
	public void update()
	{
		if (gameWindow != null)
		{
			BufferStrategy strat = gameWindow.getBufferStrategy();
			if (! strat.contentsLost())
				strat.show();
		}
		Toolkit.getDefaultToolkit().sync();
	}
	
	public int getWidth()
	{
		return gameWindow.getWidth();
	}
	
	public int getHeight()
	{
		return gameWindow.getHeight();
	}
	
	public BufferedImage createCompatibleImage(int w, int h, int transparency)
	{
		return gameWindow.getGraphicsConfiguration().createCompatibleImage(w, h, transparency);
	}
	
	/**
	**	Closes and disposes of this game window. Calls to getGraphics and update will probably crash.
	**/
	public void end()
	{
		if (device.getFullScreenWindow() != null)
			device.setFullScreenWindow(null);
		gameWindow.dispose();
	}
}