package convcomm.engine.util;

import java.awt.*;

/**
**	Graphics Utility is a collection of very useful functions to support drawing graphics. This is
**		a static class and shouldn't be created.
**	
**	@author Bitza
**/
public class GraphicsUtility
{
	private static boolean debug = true;
	
	public static void drawStringCentered(Graphics2D g, String str, int x, int y, int width, int height)
	{
		FontMetrics metrics = g.getFontMetrics();
		int stringWidth = metrics.stringWidth(str);
		int stringHeight = metrics.getMaxAscent();
		int finalX = ((width - stringWidth) / 2) + x;
		int finalY = ((height + stringHeight) / 2) + y;
		g.drawString(str, finalX, finalY);
	}
}