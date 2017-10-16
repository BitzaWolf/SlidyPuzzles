package bitzawolf.dialog;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
**	Only allows java-accepted image files to be picked. Borrowed from the
**		excellent <a href="http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html">Java
**		tutorial</a> on creating a custom JFileChooser.
**/
public class ImageFilter extends FileFilter
{
	@Override
	public boolean accept(File f)
	{
		if (f.isDirectory())
			return true;
		
		String extension = getExtension(f);
		if (extension != null)
		{
			if (extension.equals("tiff") ||
				extension.equals("tif") ||
				extension.equals("gif") ||
				extension.equals("jpeg") ||
				extension.equals("jpg") ||
				extension.equals("png"))
			{
				return true;
			}
			else
				return false;
		}
		
		return false;
	}
	
	@Override
	public String getDescription()
	{
		return "Images";
	}
	
	public static String getExtension(File f)
	{
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1)
			ext = s.substring(i+1).toLowerCase();
		return ext;
	}
}