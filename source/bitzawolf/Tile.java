package bitzawolf;

import java.awt.geom.Point2D;
import java.awt.*;

/**
**	Tile represents a single part of the entire puzzle. It is a portion of the image and can be slid around.
**/
public class Tile
{
	private Image img;
	private Point originalSlotNumber, currentSlotNumber;
	private Point2D.Float position, moveLocation, velocity;
	
	private static final float ANIM_SPEED = 500.0f / 1000.0f; //x pixels per second convered into milliseconds.
	
	public Tile(Image image, int slotNumberX, int slotNumberY, float posX, float posY)
	{
		img = image;
		originalSlotNumber = new Point(slotNumberX, slotNumberY);
		currentSlotNumber = new Point(slotNumberX, slotNumberY);
		position = new Point2D.Float(posX, posY);
		moveLocation = new Point2D.Float(posX, posY);
		velocity = new Point2D.Float(0, 0);
	}
	
	/**
	**	If the tile is in the space it was constructed in. Should be used to determine if the puzzle is complete.
	**/
	public boolean isInOriginalSlot()
	{
		return (currentSlotNumber.equals(originalSlotNumber));
	}
	
	public boolean isMoving()
	{
		return (velocity.x != 0 || velocity.y != 0);
	}
	
	/**
	**	Moves this tile to the destination position and gives the tile a new slot number.
	**		The tile automatically animates itself with the update call. 
	**/
	public void moveTo(float x, float y, Point newSlot)
	{
		currentSlotNumber.x = newSlot.x;
		currentSlotNumber.y = newSlot.y;
		moveLocation.x = x;
		moveLocation.y = y;
		
		int signX = 0, signY = 0;
		if (moveLocation.x < position.x)
			signX = -1;
		else if (moveLocation.x > position.x)
			signX = 1;
		if (moveLocation.y < position.y)
			signY = -1;
		else if (moveLocation.y > position.y)
			signY = 1;
		
		velocity.x = ANIM_SPEED * signX;
		velocity.y = ANIM_SPEED * signY;
	}
	
	/**
	**	Moves this tile to the destination position without animating and gives the tile a new slot number.
	**/
	public void moveInstantlyTo(float x, float y, Point newSlot)
	{
		currentSlotNumber.x = newSlot.x;
		currentSlotNumber.y = newSlot.y;
		moveLocation.x = x;
		moveLocation.y = y;
		position.x = x;
		position.y = y;
	}
	
	/**
	**	Animates this tile. If the tile has not been told to move, then this method does nothing.
	**/
	public void update(long time)
	{
		if (velocity.x != 0 || velocity.y != 0)
		{
			position.x += velocity.x * time;
			position.y += velocity.y * time;
			
			if (velocity.x < 0 && position.x <= moveLocation.x)
			{
				position.x = moveLocation.x;
				velocity.x = 0;
			}
			else if (velocity.x > 0 && position.x >= moveLocation.x)
			{
				position.x = moveLocation.x;
				velocity.x = 0;
			}
			
			if (velocity.y < 0 && position.y <= moveLocation.y)
			{
				position.y = moveLocation.y;
				velocity.y = 0;
			}
			else if (velocity.y > 0 && position.y >= moveLocation.y)
			{
				position.y = moveLocation.y;
				velocity.y = 0;
			}
		}
	}
	
	public Point getCurrentSlot()
	{
		return new Point(currentSlotNumber.x, currentSlotNumber.y);
	}
	
	public Point getOriginalSlot()
	{
		return new Point(originalSlotNumber.x, originalSlotNumber.y);
	}
	
	public void draw(Graphics2D g, int paddingLeft, int paddingTop)
	{
		g.drawImage(img, ((int) position.x) + paddingLeft, ((int) position.y) + paddingTop, null);
	}
	
	public String toString()
	{
		return "TILE. At location (" + position.x + ", " + position.y + ") and slot (" + currentSlotNumber.x + ", " + currentSlotNumber.y + ")";
	}
}