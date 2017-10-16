package bitzawolf;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.net.URL;

/**
**	Represents the play-area of tiles that can be shifted around. This class also facilitates the procedure
**		of cutting up and scaling images as requested. There is also a static function that returns the suggested
**		tile counts for a certain images based on its aspect ratio and overall size.
**/
public class TileGrid
{
	private static final int MIN_TILE_SIZE = 100; // in either width or height
	private int tileWidth, tileHeight, columns, rows;
	private Tile[] tiles;
	private Image originalImageScaled;
	private ArrayList<Point> reverseMoves;
	
	public TileGrid(int columns, int rows, ImageIcon originalImage, int screenWidth, int screenHeight)
	{
		this.columns = columns;
		this.rows = rows;
		ImageIcon scaled = scaleImage(originalImage, screenWidth, screenHeight);
		originalImageScaled = scaled.getImage();
		int numTiles = columns * rows;
		tileWidth = scaled.getIconWidth() / columns;
		tileHeight = scaled.getIconHeight() / rows;
		tiles = new Tile[numTiles];
		for (int row = 0; row < rows; ++row)
		{
			for (int column = 0; column < columns; ++column)
			{
				BufferedImage buff = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics g = buff.getGraphics();
				g.drawImage(scaled.getImage(), 0, 0, tileWidth - 1, tileHeight - 1, column * tileWidth, row * tileHeight, (column + 1) * tileWidth, (row + 1) * tileHeight, null);
				tiles[column + row * columns] = new Tile(buff, column, row, column * tileWidth, row * tileHeight);
			}
		}
		tiles[0] = null;
		
		shuffle();
		
		/*System.out.println("\ttotalTiles: " + numTiles + "\n\ttileWidth: " + tileWidth + "\n\ttileHeight: " + tileHeight);
		int i = 0;
		for (Tile t : tiles)
		{
			if (t == null)
				System.out.println("\ttiles[" + i + "] = null;");
			else
				System.out.println("\ttiles[" + i + "] = " + t);
			++i;
		}*/
	}
	
	/**
	**	Moves the tile indicated at the specified slot position to the empty tile space if possible.
	**/
	public void moveTileViaSlot(int slotPosX, int slotPosY)
	{
		if (slotPosX >= tileWidth * columns)
			return;
		if (slotPosY >= tileHeight * rows)
			return;
		Tile selectedTile = tiles[slotPosX + slotPosY * columns];
		if (selectedTile == null)
			return;
		Point selectedTilePos = selectedTile.getCurrentSlot();
		Point emptyTilePos = getEmptyPosition();
		boolean inSameRow = (selectedTilePos.y == emptyTilePos.y);
		boolean inSameColumn = (selectedTilePos.x == emptyTilePos.x);
		if (inSameRow)
		{
			int dx = (emptyTilePos.x > selectedTilePos.x) ? -1 : 1;
			int xSlotPos = emptyTilePos.x + dx;
			while (xSlotPos != selectedTilePos.x)
			{
				moveTileToEmpty(xSlotPos, selectedTilePos.y);
				xSlotPos += dx;
			}
			moveTileToEmpty(selectedTilePos.x, selectedTilePos.y);
		}
		else if (inSameColumn)
		{
			int dy = (emptyTilePos.y > selectedTilePos.y) ? -1 : 1;
			int ySlotPos = emptyTilePos.y + dy;
			while (ySlotPos != selectedTilePos.y)
			{
				moveTileToEmpty(selectedTilePos.x, ySlotPos);
				ySlotPos += dy;
			}
			moveTileToEmpty(selectedTilePos.x, selectedTilePos.y);
		}
		
		/*System.out.println("Move Tile Called.");
		System.out.println("\tCalled at position (" + tileSpacePosX + ", " + tileSpacePosY + ")");
		//System.out.println("\tTeanslated into index: " + (column + row * columns));
		System.out.println("\tTranslated into Tile: " + selectedTile);
		System.out.println("\tEmptyTilePos: " + emptyTilePos);
		System.out.println("\tsameRow: " + inSameRow + "\tsameColumn: " + inSameColumn);
		System.out.println("\tNew Tiles array:");
		int i = 0;
		for (Tile t : tiles)
		{
			if (t == null)
				System.out.println("\ttiles[" + i + "] = null;");
			else
				System.out.println("\ttiles[" + i + "] = " + t);
			++i;
		}*/
	}
	
	/**
	**	Moves the tile that contains the coordinates to the empty tile space, if possible.<br>
	**		If not possible, nothing happens.<br>
	**		If an adjacent tile that's in the way can move, that one gets moved too. For example
	**		in a 3 x 3 grid with an empty at position 0 and 2 is told to move, 1 will move left also.
	**	@param tileSpacePosX The x coordinate in pixels relative to the grid's top-left origin.
	**	@param tileSpacePosY The y coordinate in pixels relative to the grid's top-left origin.
	**/
	public void moveTile(int tileSpacePosX, int tileSpacePosY)
	{
		if (tileSpacePosX >= tileWidth * columns)
			return;
		if (tileSpacePosY >= tileHeight * rows)
			return;
		int column = (tileSpacePosX / tileWidth);
		int row = (tileSpacePosY / tileHeight);
		moveTileViaSlot(column, row);
		
		
		/*Tile selectedTile = tiles[column + row * columns];
		if (selectedTile == null)
			return;
		Point selectedTilePos = selectedTile.getCurrentSlot();
		Point emptyTilePos = getEmptyPosition();
		boolean inSameRow = (selectedTilePos.y == emptyTilePos.y);
		boolean inSameColumn = (selectedTilePos.x == emptyTilePos.x);
		if (inSameRow)
		{
			int dx = (emptyTilePos.x > selectedTilePos.x) ? -1 : 1;
			int xSlotPos = emptyTilePos.x + dx;
			while (xSlotPos != selectedTilePos.x)
			{
				moveTileToEmpty(xSlotPos, selectedTilePos.y);
				xSlotPos += dx;
			}
			moveTileToEmpty(selectedTilePos.x, selectedTilePos.y);
		}
		else if (inSameColumn)
		{
			int dy = (emptyTilePos.y > selectedTilePos.y) ? -1 : 1;
			int ySlotPos = emptyTilePos.y + dy;
			while (ySlotPos != selectedTilePos.y)
			{
				moveTileToEmpty(selectedTilePos.x, ySlotPos);
				ySlotPos += dy;
			}
			moveTileToEmpty(selectedTilePos.x, selectedTilePos.y);
		}
		else
		{
			// neither
		}*/
	}
	
	/**
	**	Orders the tile at the selected slot position to move to the empty tile position.
	**/
	private void moveTileToEmpty(int slotPosX, int slotPosY)
	{
		Point emptyTilePos = getEmptyPosition();
		Tile selectedTile = getTileFromSlotPosition(slotPosX, slotPosY);
		float posX = emptyTilePos.x * tileWidth;
		float posY = emptyTilePos.y * tileHeight;
		selectedTile.moveTo(posX, posY, emptyTilePos);
		for (int i = 0; i < tiles.length; ++i)
		{
			if (tiles[i] == null)
				tiles[i] = selectedTile;
			else if (tiles[i] == selectedTile)
				tiles[i] = null;
		}
	}
	
	/**
	** Orders the tile at the selected slot position to move <b>without animating</b> to the empty tile position.
	**/
	private void moveTileInstantlyToEmpty(int slotPosX, int slotPosY)
	{
		Point emptyTilePos = getEmptyPosition();
		Tile selectedTile = getTileFromSlotPosition(slotPosX, slotPosY);
		float posX = emptyTilePos.x * tileWidth;
		float posY = emptyTilePos.y * tileHeight;
		if (posX >= tileWidth * columns || posY >= tileHeight * rows)
			System.out.println("Warning! Moving a tile outside game boundaries (" + slotPosX + ", " + slotPosY + ") to (" + posX + ", " + posY + ")");
		selectedTile.moveInstantlyTo(posX, posY, emptyTilePos);
		for (int i = 0; i < tiles.length; ++i)
		{
			if (tiles[i] == null)
				tiles[i] = selectedTile;
			else if (tiles[i] == selectedTile)
				tiles[i] = null;
		}
	}
	
	public Tile getTileFromSlotPosition(int slotPosX, int slotPosY)
	{
		int index = slotPosX + slotPosY * columns;
		if (index >= tiles.length)
			return null;
		return tiles[index];
	}
	
	/**
	**	Returns the position of the empty tile, or null if it's not found for some very strange and impossible reason.
	**/
	public Point getEmptyPosition()
	{
		for (int x = 0; x < columns; ++x)
		{
			for (int y = 0; y < rows; ++y)
			{
				int i = x + y * columns;
				if (tiles[i] == null)
					return new Point(x, y);
			}
		}
		return null;
	}
	
	public boolean isComplete()
	{
		for (Tile t : tiles)
		{
			if (t != null && ! t.isInOriginalSlot())
					return false;
		}
		return true;
	}
	
	public boolean isNotMoving()
	{
		for (Tile t : tiles)
		{
			if (t != null && t.isMoving())
				return false;
		}
		return true;
	}
	
	public Image getOriginalImageScaled()
	{
		return originalImageScaled;
	}
	
	public int getScaledImageWidth()
	{
		return originalImageScaled.getWidth(null);
	}
	
	public int getScaledImageHeight()
	{
		return originalImageScaled.getHeight(null);
	}
	
	public ArrayList<Point> getReverseMoves()
	{
		ArrayList<Point> reverseMovesCopy = new ArrayList<Point>();
		for (int i = 0; i < reverseMoves.size(); ++i)
			reverseMovesCopy.add(i, reverseMoves.get(i));
		return reverseMovesCopy;
	}
	
	/**
	**	Scales the provided image to fit the screen size. This function preserves aspect ratio
	**		and aims to scale by leaving the image as large as possible. A new image is created, so the
	**		original remains unchanged.
	**	<p>If the image is smaller than the screen, the image returned is the same as the one passed.</p>
	**/
	private static ImageIcon scaleImage(ImageIcon img, int screenWidth, int screenHeight)
	{
		ImageIcon img2 = img;
		if (img.getIconWidth() > screenWidth)
		{
			if (img.getIconHeight() > screenHeight)
			{
				// so, the image is bigger than the screen's width and height.
				// Try scaling by width first.
				img = new ImageIcon(img.getImage().getScaledInstance(screenWidth, -1, Image.SCALE_DEFAULT));
				if (img.getIconHeight() > screenHeight) // is it still too tall?
					img = new ImageIcon(img.getImage().getScaledInstance(-1, screenHeight, Image.SCALE_DEFAULT)); // scale by height instead.
			}
			else
				img = new ImageIcon(img.getImage().getScaledInstance(screenWidth, -1, Image.SCALE_DEFAULT));
		}
		else if (img.getIconHeight() > screenHeight)
			img = new ImageIcon(img.getImage().getScaledInstance(-1, screenHeight, Image.SCALE_DEFAULT));
		
		return img;
	}
	
	public static ArrayList<Point> createSuggestions(File f, int screenWidth, int screenHeight)
	{
		return createSuggestions(new ImageIcon(f.getPath()), screenWidth, screenHeight);
	}
	
	public static ArrayList<Point> createSuggestions(URL path, int screenWidth, int screenHeight)
	{
		return createSuggestions(new ImageIcon(path), screenWidth, screenHeight);
	}
	
	/**
	**	Creates a list of suggested grid sizes based on the image size when it will be scaled to fit the screen.
	**/
	public static ArrayList<Point> createSuggestions(ImageIcon ico, int screenWidth, int screenHeight)
	{
		ImageIcon temp = scaleImage(ico, screenWidth, screenHeight);
		int width = temp.getIconWidth();
		int height = temp.getIconHeight();
		
		ArrayList<Integer> possibleColumns = new ArrayList<Integer>();
		ArrayList<Integer> possibleRows = new ArrayList<Integer>();
		
		
		for (int columnCount = 3; columnCount <= 7; ++columnCount)
		{
			int tileWidth = width / columnCount;
			if (tileWidth >= MIN_TILE_SIZE)
				possibleColumns.add(new Integer(columnCount));
		}
		
		for (int rowCount = 3; rowCount <= 7; ++rowCount)
		{
			int tileHeight = height / rowCount;
			if (tileHeight >= MIN_TILE_SIZE)
				possibleRows.add(new Integer(rowCount));
		}
		
		ArrayList<Point> combinations = new ArrayList<Point>();
		for (Integer columnCount : possibleColumns)
		{
			for (Integer rowCount : possibleRows)
				combinations.add(new Point(columnCount.intValue(), rowCount.intValue()));
		}
		
		if (combinations.size() == 0)
			return null;
		
		return combinations;
	}
	
	public void update(long time)
	{
		for (Tile t : tiles)
			if (t != null)
				t.update(time);
	}
	
	public void draw(Graphics2D g, int paddingLeft, int paddingTop)
	{
		for (Tile t : tiles)
			if (t != null)
				t.draw(g, paddingLeft, paddingTop);
	}
	
	/**
	**	Shuffles the tiles, preparing them for a new game. This is performed in such a way so to make sure it can be solved. This is
	**		done moving tiles as though a player were clicking on them to move, which gaurentees that the puzzle can be solved.
	**/
	private void shuffle()
	{
		Random r = new Random();
		final int SHUFFLE_MOVES = 100;
		Point lastEmptyPosition = getEmptyPosition(), currentEmptyPosition = getEmptyPosition();
		ArrayList<Point> possibleMoves = new ArrayList<Point>();
		boolean lastMovementVertical = true;
		reverseMoves = new ArrayList<Point>();
		reverseMoves.add(new Point(0, 0));
		for (int i = 0 ; i < SHUFFLE_MOVES; ++i)
		{
			if (lastMovementVertical)
			{
				for (int x = 0; x < columns; ++x)
				{
					Point addMe = new Point(x, currentEmptyPosition.y);
					if (! addMe.equals(currentEmptyPosition) && ! addMe.equals(lastEmptyPosition))
						possibleMoves.add(addMe);
				}
				lastMovementVertical = false;
			}
			else
			{
				for (int y = 0; y < rows; ++y)
				{
					Point addMe = new Point(currentEmptyPosition.x, y);
					if (! addMe.equals(currentEmptyPosition) && ! addMe.equals(lastEmptyPosition))
						possibleMoves.add(addMe);
				}
				lastMovementVertical = true;
			}
			
			Point useMe = possibleMoves.get(r.nextInt(possibleMoves.size()));
			reverseMoves.add(0, useMe);
			
			if (useMe.x != currentEmptyPosition.x)
			{
				int dx = (useMe.x > currentEmptyPosition.x) ? 1 : -1;
				for (int x = currentEmptyPosition.x + dx; x != useMe.x + dx; x += dx)
					moveTileInstantlyToEmpty(x, useMe.y);
			}
			else
			{
				int dy = (useMe.y > currentEmptyPosition.y) ? 1 : -1;
				for (int y = currentEmptyPosition.y + dy; y != useMe.y + dy; y += dy)
					moveTileInstantlyToEmpty(useMe.x, y);
			}
			
			possibleMoves.clear();
			lastEmptyPosition = currentEmptyPosition;
			currentEmptyPosition = useMe;
		}
	}
	
	/**
	**	Shuffles the tiles, preparing them for a new game. This is performed in such a way so to make sure it can be solved. This is
	**		done moving tiles as though a player were clicking on them to move, which gaurentees that the puzzle can be solved.
	**	
	**	This method is being removed for a more efficient version. This method thinks in a 1-step-at-a-time fashion by examing
	**		where the next movement can come from, picking one of those at random, then picking randomly how far in that direction
	**		to move. If the spot the empty was just at is chosen, this gets repeated.
	**	A more efficient method would be to populate a list of all possible slotPositions that can be moved next and pick one of those
	**		randomly. While constructing the list we can take out the last position it was just in, so we don't have to repeat the randomized
	**		picking. This is covered in shuffle(). This method will be held as an example of increasing readability and efficiency.
	**/
	private void shuffleOld()
	{
		Random r = new Random();
		final int SUFFLE_MOVES = 1;//50;
		final int UP = 10, RIGHT = 11, DOWN = 12, LEFT = 13;
		Point lastEmptyPosition = getEmptyPosition(), currentEmptyPosition = getEmptyPosition();
		for (int i = 0; i < SUFFLE_MOVES; ++i)
		{
			Point finalEmptyPosition = null;
			int dx = 0, dy = 0;
			do
			{
				boolean canGoUp = currentEmptyPosition.y != 0;
				boolean canGoRight = currentEmptyPosition.x != (columns - 1);
				boolean canGoDown = currentEmptyPosition.y != (rows - 1);
				boolean canGoLeft = currentEmptyPosition.x != 0;
				int numOfDirections = 0;
				int[] directions = new int[4];
				if (canGoUp)
				{
					directions[numOfDirections] = UP;
					++numOfDirections;
				}
				if (canGoRight)
				{
					directions[numOfDirections] = RIGHT;
					++numOfDirections;
				}
				if (canGoDown)
				{
					directions[numOfDirections] = DOWN;
					++numOfDirections;
				}
				if (canGoLeft)
				{
					directions[numOfDirections] = LEFT;
					++numOfDirections;
				}
				int direction = directions[r.nextInt(numOfDirections)];
				
				int maxSpaces = 1;
				switch (direction)
				{
					case UP: maxSpaces = currentEmptyPosition.y; break;
					case RIGHT: maxSpaces = columns - currentEmptyPosition.x - 1; break;
					case DOWN: maxSpaces = rows - currentEmptyPosition.y - 1; break;
					case LEFT: maxSpaces = currentEmptyPosition.x; break;
					default: System.out.println("Unknown direction attempted in shuffle: " + direction);
				}
				int distance = r.nextInt(maxSpaces) + 1;
				dx = 0;
				dy = 0;
				switch (direction)
				{
					case UP: dy = -1; break;
					case RIGHT: dx = 1; break;
					case DOWN: dy = 1; break;
					case LEFT: dx = -1; break;
				}
				finalEmptyPosition = new Point(currentEmptyPosition.x + (dx * distance), currentEmptyPosition.y + (dy * distance));
			}
			while (finalEmptyPosition.equals(lastEmptyPosition));
			
			if (dx != 0) // moving left or right
			{
				int xSlotPos = currentEmptyPosition.x + dx;
				while (xSlotPos != finalEmptyPosition.x)
				{
					moveTileInstantlyToEmpty(xSlotPos, finalEmptyPosition.y);
					xSlotPos += dx;
				}
				moveTileToEmpty(finalEmptyPosition.x, finalEmptyPosition.y);
			}
			else
			{
				int ySlotPos = currentEmptyPosition.y + dy;
				while (ySlotPos != finalEmptyPosition.y)
				{
					moveTileToEmpty(finalEmptyPosition.x, ySlotPos);
					ySlotPos += dy;
				}
				moveTileToEmpty(finalEmptyPosition.x, finalEmptyPosition.y);
			}
			lastEmptyPosition = currentEmptyPosition;
			currentEmptyPosition = finalEmptyPosition;
		}
	}
}