package bitzawolf.dialog;

import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

/**
**	Displays a dialog to allow the user to sellect a new window size. This class doesn't actually change the game window size, it
**		just selects a width and a height from either a list of common screen sizes or a custom defined screen size.
**	<p>This class is a static class, so to use it just call <code>WindowSizeDialog.show(Frame parent, int currentWidth, int currentHeight)</code>.</p>
**/
public class WindowSizeDialog implements ActionListener, DocumentListener
{
	private static final WindowSizeDialog listener = new WindowSizeDialog();
	private static final String[] SCREEN_SIZES = {"640 x 480", "800 x 600", "1024 x 768", "1280 x 1024", "1440 x 900", "1600 x 1000" };
	private static final int WIDTH = 400;
	private static final int HEIGHT = 210;
	
	private static JDialog dialog;
	private static JPanel panel;
	private static JRadioButton suggestedSizeRButt, customSizeRButt;
	private static JComboBox<String> suggestedSizeComboBox;
	private static JTextField customWidthField, customHeightField;
	private static JLabel badChoiceLabel;
	private static JButton okayButton, cancelButton;
	private static int screenWidth, screenHeight;
	private static int[] returnValues;
	
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
		
		suggestedSizeRButt = new JRadioButton("Suggested Sizes");
		suggestedSizeRButt.addActionListener(listener);
		panel.add(suggestedSizeRButt, gbc);
		
		suggestedSizeComboBox = new JComboBox<String>(SCREEN_SIZES);
		gbc.gridx = 1;
		gbc.gridwidth = 3;
		panel.add(suggestedSizeComboBox, gbc);
		
		customSizeRButt = new JRadioButton("Custom Size");
		customSizeRButt.addActionListener(listener);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		panel.add(customSizeRButt, gbc);
		
		JPanel tempPanel = new JPanel();
		customWidthField = new JTextField(5);
		customWidthField.getDocument().addDocumentListener(listener);
		tempPanel.add(customWidthField);
		tempPanel.add(new JLabel("x"));
		customHeightField = new JTextField(5);
		customHeightField.getDocument().addDocumentListener(listener);
		tempPanel.add(customHeightField);
		gbc.gridx = 1;
		gbc.gridwidth = 3;
		panel.add(tempPanel, gbc);
		
		
		badChoiceLabel = new JLabel("Bad Size default text, change me as needed!");
		badChoiceLabel.setForeground(Color.RED);
		badChoiceLabel.setVisible(false);
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.gridy = 2;
		panel.add(badChoiceLabel, gbc);
		
		gbc.gridx = 0;
		gbc.gridwidth = 4;
		gbc.gridy = 3;
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode mode = device.getDisplayMode();
		screenWidth = mode.getWidth();
		screenHeight = mode.getHeight();
		if (mode != null)
			panel.add(new JLabel("Your monitor's size is " + mode.getWidth() + " x " + mode.getHeight()), gbc);
		else
			panel.add(new JLabel("Your monitor's size is unknown."), gbc);
		
		okayButton = new JButton("Okay");
		okayButton.addActionListener(listener);
		gbc.gridx = 1;
		gbc.gridwidth = 1;
		gbc.gridy = 4;
		panel.add(okayButton, gbc);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(listener);
		gbc.gridx = 2;
		panel.add(cancelButton, gbc);
		
		ButtonGroup group = new ButtonGroup();
		group.add(suggestedSizeRButt);
		group.add(customSizeRButt);
	}
	
	/**
	** Shows this dialog and returns a selected Width and Height. This method forces the calling thread to
	**	wait until the user selects "Okay" or "Cancel".
	**/
	public static int[] show(Frame parent, int currentWidth, int currentHeight)
	{
		returnValues = new int[2];
		returnValues[0] = currentWidth;
		returnValues[1] = currentHeight;
		
		customWidthField.setText("" + currentWidth);
		customHeightField.setText("" + currentHeight);
		
		boolean customSizeUsed = true;
		for (String size : SCREEN_SIZES)
		{
			Scanner sizeIn = new Scanner(size);
			int width = sizeIn.nextInt();
			sizeIn.next();
			int height = sizeIn.nextInt();
			if (width == currentWidth && height == currentHeight)
			{
				activateSuggestedSizes();
				suggestedSizeRButt.setSelected(true);
				suggestedSizeComboBox.setSelectedItem(size);
				customSizeUsed = false;
				break;
			}
		}
		if (customSizeUsed)
		{
			activateCustomSizes();
			customSizeRButt.setSelected(true);
		}
		
		dialog = new JDialog(parent, "Change Window Size", true);
		dialog.setSize(WIDTH, HEIGHT);
		dialog.setResizable(false);
		dialog.add(panel);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		
		return returnValues;
	}
	
	/**
	**	React to the player pressing the Okay button.
	**/
	private static void performOkay()
	{
		if (customSizeRButt.isSelected())
		{
			if (badChoiceLabel.isVisible()) // if the values in the textFields are a bad choice.
			{
				dialog.setVisible(false);
				return;
			}
			Scanner fieldIn = new Scanner(customWidthField.getText());
			int width = fieldIn.nextInt();
			fieldIn = new Scanner(customHeightField.getText());
			int height = fieldIn.nextInt();
			returnValues[0] = width;
			returnValues[1] = height;
		}
		else if (suggestedSizeRButt.isSelected())
		{
			Scanner comboIn = new Scanner((String) suggestedSizeComboBox.getSelectedItem());
			returnValues[0] = comboIn.nextInt();
			comboIn.next();
			returnValues[1] = comboIn.nextInt();
		}
		dialog.setVisible(false);
	}
	
	/**
	**	React to the player pressing the Cancel button.
	**/
	private static void performCancel()
	{
		dialog.setVisible(false);
	}
	
	/**
	**	Enables the comboBox for suggested screen sizes and disables the custom size textFields.
	**/
	private static void activateSuggestedSizes()
	{
		suggestedSizeComboBox.setEnabled(true);
		customWidthField.setEnabled(false);
		customHeightField.setEnabled(false);
	}
	
	/**
	**	Enables the custom size textFields and disables the comboBox for suggested screen sizes.
	**/
	private static void activateCustomSizes()
	{
		suggestedSizeComboBox.setEnabled(false);
		customWidthField.setEnabled(true);
		customHeightField.setEnabled(true);
	}
	
	/**
	**	Checks to see if the newly entered info in the JTextFields are numbers, and not too big for the screen.
	**/
	private static void respondToTextField()
	{
		boolean widthIsBad = false;
		boolean heightIsBad = false;
		try
		{
			int width = Integer.parseInt(customWidthField.getText());
			if (width < 640)
			{
				badChoiceLabel.setText(customWidthField.getText() + " is too small of a width.");
				widthIsBad = true;
			}
				
		}
		catch (NumberFormatException fnfe)
		{
			widthIsBad = true;
			badChoiceLabel.setText(customWidthField.getText() + " is not a vaild width.");
		}
		try
		{
			int height = Integer.parseInt(customHeightField.getText());
			if (height < 480)
			{
				badChoiceLabel.setText(customHeightField.getText() + " is too small of a height.");
				heightIsBad = true;
			}
		}
		catch (NumberFormatException fnfe)
		{
			heightIsBad = true;
			if (widthIsBad)
				badChoiceLabel.setText("Please only enter valid numbers for the width and height.");
			else
				badChoiceLabel.setText(customHeightField.getText() + " is not a valid height.");
		}
		
		
		
		if (widthIsBad || heightIsBad)
			badChoiceLabel.setVisible(true);
		else
			badChoiceLabel.setVisible(false);
	}
	
	/** Empty constructor. This is a static class, so except for an ActionListener, this should never be called. **/
	private WindowSizeDialog() { }
	
	public void changedUpdate(DocumentEvent de) { respondToTextField(); }
	public void removeUpdate(DocumentEvent de) { respondToTextField(); }
	public void insertUpdate(DocumentEvent de) { respondToTextField(); }
	
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == okayButton)
			performOkay();
		else if (ae.getSource() == cancelButton)
			performCancel();
		else if (ae.getSource() == suggestedSizeRButt)
			activateSuggestedSizes();
		else if (ae.getSource() == customSizeRButt)
			activateCustomSizes();
	}
}