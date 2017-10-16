package convcomm.engine.input;

/**
**	VirtualKey is a representation of the perfect game-pad for your game. Imagine if you could create a seperate
**		key for "jump" or "buy from the shop", that's what VirtualKey is meant to represent. You later tie this
**		virtual-key or "Game Action" to an actual physical key like on the keyboard or mouse so the virtual key
**		actually receives presses.
**	
**	@author David Brackeen from Developing Games in Java.
**/
public class VirtualKey
{
	/**
	**	Defines how the button handles receiving input from the raw source, like the Keyboard.
	**/
	public enum Behavior
	{
		/**Key reports pressed as long as its held down.**/
		RAPID_FIRE,
		/**Key reports pressed <b>once</b> until the key is released and re-pressed.**/
		INITIAL_PRESS_ONLY;
	}
	
	private enum State
	{
		RELEASED,
		PRESSED,
		WAITING_FOR_RELEASE;
	}
	
	private String name;
	private Behavior behavior;
	private int amount;
	private State state;
	
	/**
	**	Creates a new VirtualKey with the RAPID_FIRE behavior.
	**/
	public VirtualKey(String name)
	{
		this(name, Behavior.RAPID_FIRE);
	}
	
	/**
	**	Creates a new VirtualKey with the stated name and behavior.
	**/
	public VirtualKey(String name, Behavior behavior)
	{
		this.name = name;
		this.behavior = behavior;
		reset();
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	**	Resets the key so that it appears like it hasn't been pressed.
	**/
	public void reset()
	{
		state = State.RELEASED;
		amount = 0;
	}
	
	/**
	**	Calls <code>press(); release();</code>.
	**/
	public synchronized void tap()
	{
		press();
		release();
	}
	
	/**
	**	Signals that the key was pressed. Calls <code>press(1);</code>.
	**/
	public synchronized void press()
	{
		press(1);
	}
	
	/**
	**	Signals that the key was presed a specific number of times.
	**/
	public synchronized void press(int amount)
	{
		if (state != State.WAITING_FOR_RELEASE)
		{
			this.amount += amount;
			state = State.PRESSED;
		}
	}
	
	/**
	**	Signals that the key was released
	**/
	public synchronized void release()
	{
		state = State.RELEASED;
	}
	
	/**
	** Returns if the key has been presed since last checked. Calls <code>return getAmount() != 0;</code>.
	**/
	public synchronized boolean isPressed()
	{
		return getAmount() != 0;
	}
	
	/**
	** Returns the number of times this key has been pressed since last checked.
	**/
	public synchronized int getAmount()
	{
		int retVal = amount;
		if (amount != 0)
		{
			if (state == State.RELEASED)
				amount = 0;
			else if (behavior == Behavior.INITIAL_PRESS_ONLY)
			{
				state = State.WAITING_FOR_RELEASE;
				amount = 0;
			}
		}
		return retVal;
	}
}