package convcomm.engine.graphics;

import java.awt.Window;
import javax.swing.RepaintManager;
import javax.swing.JComponent;

/**
**	Null Repaint Manager is a Repaint Manager that doesn't do any repainting. Useful when all the rendering is
**		done manually instead of by AWT or Swing.
**/
public class NullRepaintManager extends RepaintManager
{
	public static void install()
	{
		RepaintManager repaintManager = new NullRepaintManager();
		repaintManager.setDoubleBufferingEnabled(false);
		RepaintManager.setCurrentManager(repaintManager);
	}
	
	public void addInvalidComponent(JComponent c) {}
	public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {}
	public void addDirtyRegion(Window window, int x, int y, int w, int h) {}
	public void markCompletelyDirty(JComponent c) {}
	public void paintDirtyRegions() {}
}