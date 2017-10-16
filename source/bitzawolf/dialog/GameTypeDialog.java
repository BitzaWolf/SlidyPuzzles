package bitzawolf.dialog;

import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

/**
**	Displays a dialog to select one of the game-modes to play: Use one of the provided images, or a custom image
**		on the computer.
**/
public class GameTypeDialog implements ActionListener
{
	public static final int PLAY_PRESET = 0;
	public static final int PLAY_CUSTOM = 1;
	public static final int CANCEL = 2;
	
	private static final GameTypeDialog listener = new GameTypeDialog();
	private static final String[] IMAGE_NAMES = {"Cry of a Nether", "Forest Evening", "Morning Mist", "Secret of the Squares"};
	private static final int WIDTH = 450;
	private static final int HEIGHT = 210;
	
	private static JDialog dialog;
	private static JPanel panel;
	private static JRadioButton rButtPresetImage, rButtCustomImage;
	private static JComboBox<String> comboBoxPresetImages;
	private static JButton buttonPlay, buttonCancel;
	private static int returnValue;
	private static int selectedPresetIndex;
	private static ImageIcon imagePresetPreview;
	private static JLabel jLabelPresetPreview;
	
	/**
	**	This "method" does all of the initialization for the static class. Pretty fancy!
	**/
	static
	{
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		
		panel.add(new JLabel("Play using a..."), gbc);
		
		rButtPresetImage = new JRadioButton("Provided preset image.");
		rButtPresetImage.setSelected(true);
		rButtPresetImage.addActionListener(listener);
		gbc.gridy = 1;
		panel.add(rButtPresetImage, gbc);
		
		rButtCustomImage = new JRadioButton("Custom image.");
		rButtCustomImage.addActionListener(listener);
		gbc.gridy = 2;
		panel.add(rButtCustomImage, gbc);
		
		comboBoxPresetImages = new JComboBox<String>(IMAGE_NAMES);
		comboBoxPresetImages.addActionListener(listener);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		panel.add(comboBoxPresetImages, gbc);
		
		imagePresetPreview = new ImageIcon(listener.getClass().getResource("StandardImage0.png"));
		imagePresetPreview = new ImageIcon(imagePresetPreview.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT));
		jLabelPresetPreview = new JLabel(imagePresetPreview);
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		panel.add(jLabelPresetPreview, gbc);
		
		buttonPlay = new JButton("Play");
		buttonPlay.addActionListener(listener);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridheight = 1;
		panel.add(buttonPlay, gbc);
		
		buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(listener);
		gbc.gridx = 2;
		panel.add(buttonCancel, gbc);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rButtCustomImage);
		group.add(rButtPresetImage);
	}
	
	/**
	** Shows this dialog and returns a selected Width and Height. This method forces the calling thread to
	**	wait until the user selects "Okay" or "Cancel".
	**/
	public static int show(Frame parent)
	{
		returnValue = CANCEL;
		
		dialog = new JDialog(parent, "Select Image", true);
		dialog.setSize(WIDTH, HEIGHT);
		dialog.setResizable(false);
		dialog.add(panel);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		
		return returnValue;
	}
	
	/**
	**	If the player chose to play with a preset image, this function returns which image was used
	**		based on the names of the images. Each of the preset images are named 0 through 4, which
	**		mimic the index location within the JComboBox.
	**/
	public static int getSelectedPresetIndex()
	{
		return selectedPresetIndex;
	}
	
	private static void changeImageIcon()
	{
		String imageName = (String) comboBoxPresetImages.getSelectedItem();
		for (int i = 0; i < IMAGE_NAMES.length; ++i)
		{
			if (IMAGE_NAMES[i].equals(imageName))
			{
				selectedPresetIndex = i;
				break;
			}
		}
		imagePresetPreview = new ImageIcon(listener.getClass().getResource("StandardImage" + selectedPresetIndex + ".png"));
		imagePresetPreview = new ImageIcon(imagePresetPreview.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT));
		jLabelPresetPreview.setIcon(imagePresetPreview);
	}
	
	/**
	**	React to the player pressing the Play button.
	**/
	private static void performPlay()
	{
		if (rButtCustomImage.isSelected())
			returnValue = PLAY_CUSTOM;
		else if (rButtPresetImage.isSelected())
		{
			returnValue = PLAY_PRESET;
			String imageName = (String) comboBoxPresetImages.getSelectedItem();
			for (int i = 0; i < IMAGE_NAMES.length; ++i)
			{
				if (IMAGE_NAMES[i].equals(imageName))
				{
					selectedPresetIndex = i;
					break;
				}
			}
		}
		dialog.setVisible(false);
	}
	
	/**
	**	React to the player pressing the Cancel button.
	**/
	private static void performCancel()
	{
		returnValue = CANCEL;
		dialog.setVisible(false);
	}
	
	/** Empty constructor. This is a static class, so except for an ActionListener, this should never be called. **/
	private GameTypeDialog() { }
	
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == buttonPlay)
			performPlay();
		else if (ae.getSource() == buttonCancel)
			performCancel();
		else if (ae.getSource() == rButtCustomImage)
			comboBoxPresetImages.setEnabled(false);
		else if (ae.getSource() == rButtPresetImage)
			comboBoxPresetImages.setEnabled(true);
		else if (ae.getSource() == comboBoxPresetImages)
			changeImageIcon();
	}
}