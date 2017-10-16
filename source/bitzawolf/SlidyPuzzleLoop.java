package bitzawolf;

import convcomm.engine.util.EmptyGameLoop;
import convcomm.engine.input.*;
import convcomm.engine.graphics.ScreenManager;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;

/**
**	This is the game-loop for running a puzzle. A lot of the display work and actual game play is covered in
**		the other classes like Tile and TileGrid; this class manages the update loop, draw loop, the pressing
**		of buttons, displaying a pause menu, quitting, etc.
**/
public class SlidyPuzzleLoop extends EmptyGameLoop implements MouseListener
{
	private static final Color[] GRADIENT_COLORS = {Color.YELLOW, Color.BLACK};
	private static final float[] DISTANCE = {0.0f, 1.0f};
	private static final double FREQUENCY = Math.PI / 500.0; // complete half of a cycle in x milliseconds.
	
	private boolean quit, waitForFinalTile, drawOriginalImage;
	private boolean checkCompletion;
	private TileGrid grid;
	private int paddingLeft, paddingTop;
	private Component compy;
	private int screenCenterX, screenCenterY, screenMax, screenWidth, screenHeight;
	private double currentAngle;
	private VirtualKey exit;
	private ScreenManager screenManager;
	
	public SlidyPuzzleLoop(URL imagePath, int numColumns, int numRows, int screenWidth, int screenHeight, Component compy, InputManager inputManager, ScreenManager sm)
	{
		this(new ImageIcon(imagePath), numColumns, numRows, screenWidth, screenHeight, compy, inputManager, sm);
	}
	
	public SlidyPuzzleLoop(File imageFile, int numColumns, int numRows, int screenWidth, int screenHeight, Component compy, InputManager inputManager, ScreenManager sm)
	{
		this(new ImageIcon(imageFile.getPath()), numColumns, numRows, screenWidth, screenHeight, compy, inputManager, sm);
	}
	
	public SlidyPuzzleLoop(ImageIcon imageIco, int numColumns, int numRows, int screenWidth, int screenHeight, Component compy, InputManager inputManager, ScreenManager sm)
	{
		quit = false;
		waitForFinalTile = false;
		drawOriginalImage = false;
		checkCompletion = false;
		this.compy = compy;
		compy.addMouseListener(this);
		grid = new TileGrid(numColumns, numRows, imageIco, screenWidth, screenHeight);
		paddingLeft = 0;
		paddingTop = 0;
		if (grid.getScaledImageWidth() < screenWidth)
			paddingLeft = (screenWidth - grid.getScaledImageWidth()) / 2;
		if (grid.getScaledImageHeight() < screenHeight)
			paddingTop = (screenHeight - grid.getScaledImageHeight()) / 2;
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		screenMax = (screenWidth > screenHeight) ? screenWidth : screenHeight;
		screenCenterX = screenWidth / 2;
		screenCenterY = screenHeight / 2;
		currentAngle = 0;
		
		exit = new VirtualKey("Exit", VirtualKey.Behavior.INITIAL_PRESS_ONLY);
		inputManager.mapToKeyboard(exit, KeyEvent.VK_ESCAPE);
		
		screenManager = sm;
	}
	
	@Override
	public void globalDraw(Graphics2D g)
	{
		if (drawOriginalImage)
		{
			Point2D start = new Point2D.Float(screenCenterX, screenCenterY);
			Point2D end = new Point2D.Float((float) Math.cos(currentAngle) * screenMax, (float) Math.sin(currentAngle) * screenMax);
			LinearGradientPaint gradient = new LinearGradientPaint(start, end, DISTANCE, GRADIENT_COLORS, MultipleGradientPaint.CycleMethod.REFLECT);
			g.setPaint(gradient);
			g.fillRect(0, 0, screenWidth, screenHeight);
			Image img = grid.getOriginalImageScaled();
			g.drawImage(img, paddingLeft, paddingTop, null);
		}
		else
			grid.draw(g, paddingLeft, paddingTop);
	}
	
	@Override
	public void globalUpdate(long time)
	{
		synchronized (this)
		{
			grid.update(time);
			if (checkCompletion)
			{
				checkCompletion = false;
				if (grid.isComplete())
					waitForFinalTile = true;
			}
			else if (waitForFinalTile)
			{
				if (grid.isNotMoving())
				{
					drawOriginalImage = true;
					waitForFinalTile = false;
				}
			}
			else if (exit.isPressed())
			{
				screenManager.enableAWTPaint(true);
				int retVal = JOptionPane.showConfirmDialog(null, "Stop playing this puzzle?", "Quit Puzzle", JOptionPane.YES_NO_OPTION);
				if (retVal == JOptionPane.YES_OPTION)
					quit = true;
				screenManager.enableAWTPaint(false);
			}
		}
		if (drawOriginalImage)
		{
			currentAngle += FREQUENCY * time;
			if (currentAngle >= Math.PI * 2)
				currentAngle -= (Math.PI * 2);
		}
	}
	
	@Override
	public boolean continueLoop()
	{
		return ! quit;
	}
	
	public void mousePressed(MouseEvent me)
	{
		synchronized (this)
		{
			if (drawOriginalImage)
			{
				quit = true;
				return;
			}
			if (waitForFinalTile || drawOriginalImage)
				return;
			
			grid.moveTile(me.getX() - paddingLeft, me.getY() - paddingTop);
			checkCompletion = true;
		}
	}
	public void mouseClicked(MouseEvent me) { }
	public void mouseEntered(MouseEvent me) { }
	public void mouseExited(MouseEvent me) { }
	public void mouseReleased(MouseEvent me) { }
}