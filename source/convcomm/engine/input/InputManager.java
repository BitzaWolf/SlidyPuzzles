package convcomm.engine.input;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
**	InputManager manages input of key and mouse events from standard Java AWT events. These events
**		can be mapped to Virtual Keys.
**	@author David Brackeen from Developing Games in Java
**/
public class InputManager implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	/**An invisible cursor, used for making the cursor disappear.**/
	public static final Cursor INVISIBLE_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(0, 0), "invisible");
	
	public enum MouseButton
	{
		MOVE_LEFT("Mouse Left"),
		MOVE_RIGHT("Mouse Right"),
		MOVE_UP("Mouse Up"),
		MOVE_DOWN("Mouse Down"),
		WHEEL_UP("Mouse Wheel Up"),
		WHEEL_DOWN("Mouse Wheel Down"),
		BUTTON_1("Mouse Button 1"),
		BUTTON_2("Mouse Button 2"),
		BUTTON_3("Mouse Button 3");
		
		private String name;
		
		MouseButton(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
		
		public static MouseButton getMouseButton(MouseEvent e)
		{
			switch (e.getButton())
			{
				case MouseEvent.BUTTON1:	return BUTTON_1;
				case MouseEvent.BUTTON2:	return BUTTON_2;
				case MouseEvent.BUTTON3:	return BUTTON_3;
			}
			return null;
		}
	}
	
	private HashMap<Integer, VirtualKey> keyboardMap;
	private HashMap<MouseButton, VirtualKey> mouseMap;
	
	private Point mouseLocation;
	private Point centerLocation;
	private Component comp;
	private Robot robot;
	private boolean isRecentering;
	
	/**
	**	Creates a new InputManager that listens to the specified component. Just pass the instance from <code>ScreenManager.getGameWindow()</code>.
	**/
	public InputManager(Component comp)
	{
		this.comp = comp;
		keyboardMap = new HashMap<Integer, VirtualKey>();
		mouseMap = new HashMap<MouseButton, VirtualKey>();
		mouseLocation = new Point();
		centerLocation = new Point();
		robot = null;
		
		comp.addKeyListener(this);
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
		comp.addMouseWheelListener(this);
		
		comp.setFocusTraversalKeysEnabled(false);
	}
	
	/**
	**	Changes the look of the cursor for this manager's component. To create a Cursor, the book did:
	**		<code>Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage("imgName?", new Point(0, 0), "invisible");</code>
	**/
	public void setCursor(Cursor cursor)
	{
		comp.setCursor(cursor);
	}
	
	/**
	**	Sets the mouse's movements to be in "FPS" mode or not. By FPS-mode, I mean the movements don't actually
	**		move the mouse and instead the movements are detected but the mouse gets reset to the center of the screen.
	**/
	public void setRelativeMouseMode(boolean activate)
	{
		if (activate == isRelativeMouseMode())
			return;
		
		if (activate)
		{
			try
			{
				robot = new Robot();
				recenterMouse();
			}
			catch (AWTException awte)
			{
				System.out.println("CC Engine error: AWTException: couldn't make Robot. (InputManager setRelativeMouseMode(boolean activate))");
				robot = null;
			}
		}
		else
			robot = null;
	}
	
	public boolean isRelativeMouseMode()
	{
		return robot != null;
	}
	
	/**
	**	Maps a VirtualKey to a keyboard key as defined in java.awt.KeyEvent. If the key is already mapped, it'll get replaced
	**		with this new key.
	**	<p><b>Improvement idea:</b> notify the game/caller via uncaught exception that a key was overwritten so the player can react.</p>
	**/
	public void mapToKeyboard(VirtualKey action, int keyCode)
	{
		keyboardMap.put(new Integer(keyCode), action);
	}
	
	/**
	**	Maps a VirtualKey to a mouse action as defined in MouseButton enumeration. If the key is already mapped, it'll get replaced
	**		with this new key.
	**	<p><b>Improvement idea:</b> notify the game/caller via uncaught exception that a key was overwritten so the player can react.</p>
	**/
	public void mapToMouse(VirtualKey action, MouseButton button)
	{
		mouseMap.put(button, action);
	}
	
	/**
	**	Clears all mapped keys and mouse actions that are using this Virtual Key.
	**/
	public void clearMap(VirtualKey action)
	{
		for (Integer key : keyboardMap.keySet())
		{
			if (keyboardMap.get(key) == action)
				keyboardMap.remove(key);
		}
		for (MouseButton key : mouseMap.keySet())
		{
			if (mouseMap.get(key) == action)
				mouseMap.remove(key);
		}
	}
	
	/**
	**	Returns an array of the keys and mouse-buttons mapped to this Virtual Key.
	**/
	public ArrayList<String> getMappings(VirtualKey action)
	{
		ArrayList<String> list = new ArrayList<String>();
		for (Integer key : keyboardMap.keySet())
		{
			if (keyboardMap.get(key) == action)
				list.add(getKeyboardName(key));
		}
		for (MouseButton key : mouseMap.keySet())
		{
			if (mouseMap.get(key) == action)
				list.add(key.getName());
		}
		return list;
	}
	
	public void resetAllVirtualKeys()
	{
		for (VirtualKey vk : keyboardMap.values())
		{
			if (vk != null)
				vk.reset();
		}
		for (VirtualKey vk : mouseMap.values())
		{
			if (vk != null)
				vk.reset();
		}
	}
	
	public static String getKeyboardName(int keyCode)
	{
		return KeyEvent.getKeyText(keyCode);
	}
	
	public int getMouseX()
	{
		return mouseLocation.x;
	}
	
	public int getMouseY()
	{
		return mouseLocation.y;
	}
	
	private synchronized void recenterMouse()
	{
		if (robot != null && comp.isShowing())
		{
			centerLocation.x = comp.getWidth() / 2;
			centerLocation.y = comp.getHeight() / 2;
			SwingUtilities.convertPointToScreen(centerLocation, comp);
			isRecentering = true;
			robot.mouseMove(centerLocation.x, centerLocation.y);
		}
	}
	
	public void keyPressed(KeyEvent e)
	{
		VirtualKey vk = keyboardMap.get(e.getKeyCode());
		if (vk != null)
			vk.press();
		e.consume();
	}
	
	public void keyReleased(KeyEvent e)
	{
		VirtualKey vk = keyboardMap.get(e.getKeyCode());
		if (vk != null)
			vk.release();
		e.consume();
	}
	
	public void keyTyped(KeyEvent e)
	{
		e.consume();
	}
	
	public void mousePressed(MouseEvent e)
	{
		VirtualKey vk = mouseMap.get(MouseButton.getMouseButton(e));
		if (vk != null)
			vk.press();
	}
	
	public void mouseReleased(MouseEvent e)
	{
		VirtualKey vk = mouseMap.get(MouseButton.getMouseButton(e));
		if (vk != null)
			vk.release();
	}
	
	public void mouseClicked(MouseEvent e)
	{
		
	}
	
	public void mouseEntered(MouseEvent e)
	{
		mouseMoved(e);
	}
	
	public void mouseExited(MouseEvent e)
	{
		mouseMoved(e);
	}
	
	public void mouseDragged(MouseEvent e)
	{
		mouseMoved(e);
	}
	
	public synchronized void mouseMoved(MouseEvent e)
	{
		if (isRecentering)
			isRecentering = false;
		else
		{
			int dx = e.getX() - mouseLocation.x;
			int dy = e.getY() - mouseLocation.y;
			mouseHelper(MouseButton.MOVE_LEFT, MouseButton.MOVE_RIGHT, dx);
			mouseHelper(MouseButton.MOVE_UP, MouseButton.MOVE_DOWN, dy);
			
			if (isRelativeMouseMode())
				recenterMouse();
		}
		
		mouseLocation.x = e.getX();
		mouseLocation.y = e.getY();
	}
	
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		mouseHelper(MouseButton.WHEEL_UP, MouseButton.WHEEL_DOWN, e.getWheelRotation());
	}
	
	private void mouseHelper(MouseButton negative, MouseButton positive, int amount)
	{
		VirtualKey key = null;
		if (amount < 0)
			key = mouseMap.get(negative);
		else if (amount > 0)
			key = mouseMap.get(positive);
		if (key != null)
		{
			key.press(Math.abs(amount));
			key.release();
		}
	}
}