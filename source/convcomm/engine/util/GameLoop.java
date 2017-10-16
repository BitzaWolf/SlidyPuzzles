package convcomm.engine.util;

import java.awt.Graphics2D;

/**
**	Game Loop is a self-contained and operating portion of the game, meant to
**		represent a separate, yet long-lasting part of a game, like display the main menu,
**		or running a dungeon.
**	
**	<p>The main engine: <code>GameEngine</code> runs like this too, but may be passed a GameLoop to
**		run a seperate game loop until told to return back to the main loop. The idea is to help
**		organize game code by having the main loop just organize and run separate loops that keep
**		all of their code contained. For example, a loop for the Main Menu probably doesn't need to
**		know about the field and methods of the loop of the actual game.</p>
**	
**	<p>This class is totally optional, but recommended to organize game code to relative places.</p>
**/
public interface GameLoop
{
	/**Draws to the screen only when the game is unpaused.**/
	public void unpausedDraw(Graphics2D g);
	/**Draws to the screen only when the game is paused.**/
	public void pausedDraw(Graphics2D g);
	/**Draws to the screen regardless of the game being paused or not.**/
	public void globalDraw(Graphics2D g);
	
	/**Updates game state only when unpaused.**/
	public void unpausedUpdate(long time);
	/**Updates game state only when paused.**/
	public void pausedUpdate(long time);
	/**Updates game state regardless of the game being paused or not.**/
	public void globalUpdate(long time);
	
	/**Returns if the loop should keep running or not.**/
	public boolean continueLoop();
	/**Returns the next GameLoop to execute after this loop has finished. Return <code>null</code> to indicate no next loop.**/
	public GameLoop nextLoop();
}