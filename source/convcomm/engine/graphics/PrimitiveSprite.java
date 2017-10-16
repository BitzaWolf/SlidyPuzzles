package convcomm.engine.graphics;

import java.awt.geom.*;
import java.awt.Shape;

public class PrimitiveSprite
{
	private RectangularShape shape;
	/**Change in position per millisecond along X.**/
	protected float dx;
	/**Change in position per millisecond along Y.**/
	protected float dy; //velocity
	
	public PrimitiveSprite(RectangularShape shape)
	{
		if (shape == null)
			shape = new Rectangle2D.Float();
		this.shape = shape;
	}
	
	/**
	**	Updates the position based on the velocity.
	**/
	public void update(long time)
	{
		shape.setFrame(shape.getX() + dx * time, shape.getY() + dy * time, shape.getWidth(), shape.getHeight());
	}
	
	public float getPositionX()
	{
		return (float) shape.getX();
	}
	
	public float getPositionY()
	{
		return (float) shape.getY();
	}
	
	public void setPositionX(float x)
	{
		shape.setFrame(x, shape.getY(), shape.getWidth(), shape.getHeight());
	}
	
	public void setPositionY(float y)
	{
		shape.setFrame(shape.getX(), y, shape.getWidth(), shape.getHeight());
	}
	
	public void setWidth(float width)
	{
		shape.setFrame(shape.getX(), shape.getY(), width, shape.getHeight());
	}
	
	public void setHeight(float height)
	{
		shape.setFrame(shape.getX(), shape.getY(), shape.getWidth(), height);
	}
	
	public float getWidth()
	{
		return (float) shape.getWidth();
	}
	
	public float getHeight()
	{
		return (float) shape.getHeight();
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
	
	public Shape getShape()
	{
		return shape;
	}
}