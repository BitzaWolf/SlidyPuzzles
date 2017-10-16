package convcomm.engine.graphics;

import java.awt.Image;

public class Sprite
{
	private Animation anim;
	
	/**X position of the sprite's top-left corner.**/
	protected float posX;
	/**Y position of the sprite's top-left corner.**/
	protected float posY;
	/**Change in position per millisecond along X.**/
	protected float dx;
	/**Change in position per millisecond along Y.**/
	protected float dy; //velocity
	
	public Sprite(Animation anim)
	{
		this.anim = anim;
		posX = 0;
		posY = 0;
		dx = 0;
		dy = 0;
	}
	
	/**
	**	Updates the position based on the velocity.
	**/
	public void update(long time)
	{
		posX += dx * time;
		posY += dy * time;
		anim.update(time);
	}
	
	public float getPositionX()
	{
		return posX;
	}
	
	public float getPositionY()
	{
		return posY;
	}
	
	public void setX(float x)
	{
		posX = x;
	}
	
	public void setY(float y)
	{
		posY = y;
	}
	
	/**
	**	Returns the width of the sprite based on the size of the image.
	**/
	public int getWidth()
	{
		return anim.getImage().getWidth(null);
	}
	
	/**
	**	Returns the height of the sprite based on the size of the image.
	**/
	public int getHeight()
	{
		return anim.getImage().getHeight(null);
	}
	
	public float getVelocityX()
	{
		return dx;
	}
	
	public float getVelocityY()
	{
		return dy;
	}
	
	public void setVelocityX(float dx)
	{
		this.dx = dx;
	}
	
	public void setVelocityY(float dy)
	{
		this.dy = dy;
	}
	
	public Image getImage()
	{
		return anim.getImage();
	}
}