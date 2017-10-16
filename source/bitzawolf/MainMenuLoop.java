package bitzawolf;

import bitzawolf.dialog.*;
import convcomm.engine.util.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
**	Runs the main menu which is shown upon starting the game. Facilitates starting a new game, changing
**		the game's window size, running the title-screen background "puzzle", and quitting.
**/
public class MainMenuLoop extends GameEngine implements ActionListener
{
	private Font textFont = new Font("Dialog", Font.PLAIN, 50);
	
	private SlidyPuzzleLoop gameLoop;
	private JLabel title;
	private JButton playButton, optionsButton, quitButton;
	private JFileChooser jfc;
	private JPanel mainPanel;
	private TileGrid backgroundTileGrid;
	private int bgPaddingLeft, bgPaddingTop, bgMovesIndex;
	private long checkInGameTimer;
	private boolean bgFinished, panelDisabled, inGame;
	private ArrayList<Point> bgMovesReverseOrder;
	
	public static void main(String[] args)
	{
		MainMenuLoop mml = new MainMenuLoop();
		mml.run(800, 600, false);
	}
	
	public MainMenuLoop()
	{
		jfc = new JFileChooser();
		jfc.setFileFilter(new ImageFilter());
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setAccessory(new ImagePreview(jfc));
		gameLoop = null;
		inGame = false;
	}
	
	@Override
	public void init()
	{
		super.init();
		super.screen.getGameWindow().setTitle("Slidy Puzzles");
		super.screen.getGameWindow().setIconImage(new ImageIcon(this.getClass().getResource("SlidyPuzzleIcon.png")).getImage());
		mainPanel = new JPanel(new BorderLayout());
		JPanel northPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets.top = 70;
		mainPanel.setOpaque(false);
		northPanel.setOpaque(false);
		title = new JLabel("Slidy Puzzles!");
		title.setFont(textFont);
		title.setForeground(Color.WHITE);
		northPanel.add(title, gbc);
		mainPanel.add(northPanel, BorderLayout.NORTH);
		
		JPanel southPanel = new JPanel(new GridBagLayout());
		southPanel.setOpaque(false);
		gbc = new GridBagConstraints();
		gbc.insets.bottom = 10;
		
		playButton = createButton("Play");
		optionsButton = createButton("Options");
		quitButton = createButton("Quit");
		
		southPanel.add(playButton, gbc);
		gbc.gridy = 2;
		southPanel.add(optionsButton, gbc);
		gbc.gridy = 3;
		gbc.insets.bottom = 20;
		southPanel.add(quitButton, gbc);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		
		enablePanel();
		resetBackgroundPuzzle();
	}
	
	private void resetBackgroundPuzzle()
	{
		Random r = new Random();
		backgroundTileGrid = new TileGrid(4, 3, new ImageIcon(this.getClass().getResource("StandardImage" + r.nextInt(4) + ".png")), super.screen.getWidth(), super.screen.getHeight());
		bgMovesReverseOrder = backgroundTileGrid.getReverseMoves();
		bgMovesIndex = 1;
		bgPaddingLeft = 0;
		bgPaddingTop = 0;
		if (backgroundTileGrid.getScaledImageWidth() < super.screen.getWidth())
			bgPaddingLeft = (super.screen.getWidth() - backgroundTileGrid.getScaledImageWidth()) / 2;
		if (backgroundTileGrid.getScaledImageHeight() < super.screen.getHeight())
			bgPaddingTop = (super.screen.getHeight() - backgroundTileGrid.getScaledImageHeight()) / 2;
		bgFinished = false;
	}
	
	private void enablePanel()
	{
		Container UIContainer = super.screen.getGameWindow().getContentPane();
		if (UIContainer instanceof JComponent)
			((JComponent) UIContainer).setOpaque(false);
		UIContainer.removeAll();
		UIContainer.setLayout(new BorderLayout());
		UIContainer.add(mainPanel, BorderLayout.CENTER);
		panelDisabled = false;
	}
	
	private void disablePanel()
	{
		Container UIContainer = super.screen.getGameWindow().getContentPane();
		if (UIContainer instanceof JComponent)
			((JComponent) UIContainer).setOpaque(false);
		UIContainer.removeAll();
		panelDisabled = true;
	}
	
	private JButton createButton(String text)
	{
		JButton b = new JButton(text);
		b.addActionListener(this);
		b.setIgnoreRepaint(true);
		b.setFocusable(false);
		b.setBorder(null);
		b.setContentAreaFilled(false);
		b.setForeground(Color.WHITE);
		b.setFont(textFont.deriveFont(35.0f));
		return b;
	}
	
	@Override
	public void globalUpdate(long time)
	{
		if (inGame)
		{
			checkInGameTimer -= time;
			if (checkInGameTimer <= 0)
				inGame = false;
		}
		if (! super.screen.isAWTEnabled())
		{
			if (panelDisabled)
				enablePanel(); // to fix a glitch where a slidy puzzle game ends and returns to main screen.
			if (! bgFinished)
			{
				backgroundTileGrid.update(time);
				if (backgroundTileGrid.isNotMoving())
				{
					if (bgMovesIndex == bgMovesReverseOrder.size())
					{
						bgFinished = true;
						return;
					}
					Point useMe = bgMovesReverseOrder.get(bgMovesIndex);
					++bgMovesIndex;
					backgroundTileGrid.moveTileViaSlot(useMe.x, useMe.y);
					bgFinished = backgroundTileGrid.isComplete();
				}
			}
			else if (! backgroundTileGrid.isNotMoving())
				backgroundTileGrid.update(time);
		}
	}
	
	@Override
	public void globalDraw(Graphics2D g)
	{
		backgroundTileGrid.draw(g, bgPaddingLeft, bgPaddingTop);
		Color oldColor = g.getColor();
		Rectangle bounds = title.getBounds();
		bounds.x += 5;
		bounds.y += 6;
		g.setColor(new Color(0.8f, 0.8f, 0.8f));
		g.fillRect(bounds.x - 15, bounds.y - 15, bounds.width + 15, bounds.height + 15);
		g.setColor(Color.BLACK);
		g.fillRect(bounds.x - 11, bounds.y - 11, bounds.width + 7, bounds.height + 7);
		
		bounds.x = optionsButton.getBounds().x - 5;
		bounds.width = optionsButton.getBounds().width + 30;
		bounds.height = playButton.getBounds().height + optionsButton.getBounds().height + quitButton.getBounds().height + 35;
		bounds.y = super.screen.getHeight() - bounds.height;
		g.setColor(new Color(0.8f, 0.8f, 0.8f));
		g.fillRect(bounds.x - 15, bounds.y - 15, bounds.width + 15, bounds.height + 15);
		g.setColor(Color.BLACK);
		g.fillRect(bounds.x - 11, bounds.y - 11, bounds.width + 7, bounds.height + 7);
		g.setColor(oldColor);
		if (! super.screen.isAWTEnabled())
			super.screen.getGameWindow().getLayeredPane().paintComponents(g);
	}
	
	/**
	**	Starts a slidy puzzle game, if one is ready to start. See <code>actionPerformed(ActionEvent ae)</code> source for how one gets started.
	**/
	@Override
	public GameLoop nextLoop()
	{
		return gameLoop;
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if (inGame)
		{
			disablePanel();
			return;
		}
		if (ae.getSource() == playButton)
		{
			disablePanel();
			super.screen.enableAWTPaint(true);
			int retVal = GameTypeDialog.show(super.screen.getGameWindow());
			if (retVal == GameTypeDialog.PLAY_PRESET)
			{
				int imageIndex = GameTypeDialog.getSelectedPresetIndex();
				ArrayList<Point> tileSuggestions = TileGrid.createSuggestions(this.getClass().getResource("StandardImage" + imageIndex + ".png"), super.screen.getWidth(), super.screen.getHeight());
				String[] options = new String[tileSuggestions.size()];
				for (int i = 0; i < options.length; ++i)
				{
					Point p = tileSuggestions.get(i);
					options[i] = "" + p.x + " x " + p.y;
				}
				String s = (String) JOptionPane.showInputDialog(super.screen.getGameWindow(), "Select the dimensions of the play area. Rows by Columns.", "Select Grid Size", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				super.screen.enableAWTPaint(false);
				if (s != null)
				{
					Scanner reader = new Scanner(s);
					int columns = reader.nextInt();
					reader.next();
					int rows = reader.nextInt();
					disablePanel();
					super.setCurrentLoop(new SlidyPuzzleLoop(this.getClass().getResource("StandardImage" + imageIndex + ".png"), columns, rows, super.screen.getWidth(), super.screen.getHeight(), super.screen.getGameWindow(), super.inputManager, super.screen));
					inGame = true;
					checkInGameTimer = 800;
				}
				else
				{
					super.screen.enableAWTPaint(false);
					enablePanel();
				}
			}
			else if (retVal == GameTypeDialog.PLAY_CUSTOM)
			{
				retVal = jfc.showDialog(super.screen.getGameWindow(), "Select Image");
				if (retVal == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = jfc.getSelectedFile();
					ArrayList<Point> tileSuggestions = TileGrid.createSuggestions(selectedFile, super.screen.getWidth(), super.screen.getHeight());
					if (tileSuggestions != null)
					{
						String[] options = new String[tileSuggestions.size()];
						for (int i = 0; i < options.length; ++i)
						{
							Point p = tileSuggestions.get(i);
							options[i] = "" + p.x + " x " + p.y;
						}
						String s = (String) JOptionPane.showInputDialog(super.screen.getGameWindow(), "Select the dimensions of the play area. Rows by Columns.", "Select Grid Size", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
						super.screen.enableAWTPaint(false);
						if (s != null)
						{
							Scanner reader = new Scanner(s);
							int columns = reader.nextInt();
							reader.next();
							int rows = reader.nextInt();
							disablePanel();
							super.setCurrentLoop(new SlidyPuzzleLoop(selectedFile, columns, rows, super.screen.getWidth(), super.screen.getHeight(), super.screen.getGameWindow(), super.inputManager, super.screen));
							inGame = true;
							checkInGameTimer = 800;
						}
						else
						{
							super.screen.enableAWTPaint(false);
							enablePanel();
						}
					}
					else
					{
						JOptionPane.showMessageDialog(super.screen.getGameWindow(), "Chose a different image. This picture isn't recommended to play with. It's either too small or of an odd aspect ratio.", "Bad image choice.", JOptionPane.ERROR_MESSAGE);
						super.screen.enableAWTPaint(false);
						enablePanel();
					}
				}
				else
				{
					super.screen.enableAWTPaint(false);
					enablePanel();
				}
				jfc.setSelectedFile(null);
			}
			else if (retVal == GameTypeDialog.CANCEL)
			{
				super.screen.enableAWTPaint(false);
				enablePanel();
			}
		}
		else if (ae.getSource() == optionsButton)
		{
			super.screen.enableAWTPaint(true);
			int[] newWindowSize = WindowSizeDialog.show(super.screen.getGameWindow(), super.screen.getWidth(), super.screen.getHeight());
			int width = newWindowSize[0];
			int height = newWindowSize[1];
			if (width != super.screen.getWidth() || height != super.screen.getHeight())
			{
				super.screen.setSize(width, height);
				resetBackgroundPuzzle();
			}
			super.screen.enableAWTPaint(false);
		}
		else if (ae.getSource() == quitButton)
		{
			super.stop();
		}
	}
}