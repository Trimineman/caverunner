/*
 * Name: Jack Whitman
 * Description: The CaveRunnerGameFrame class, the main frame of the game, handles game setup, frame creation, data loading and saving, score handling, component layout, and keypresses.
 */
package caveRunner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import aurelienribon.tweenengine.Tween;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;

@SuppressWarnings("serial")
public class CaveRunnerGameFrame extends JFrame implements ActionListener, KeyListener {
	
	//Components of game frame
	private CaveRunnerGamePanel mainPanel;
	private BorderPanel borderPanel;
	private JLabel scoreLabel, highscoreLabel;
	private JButton start;
	private int score, highscore;
	private File highscoreFile;
	
	public CaveRunnerGameFrame() {
		//Set up cave block tweening
		Tween.registerAccessor(CaveBlock.class, new CaveBlockTweener());
		
		//Set score and high score
		score = highscore = 0;
		
		//Set up main frame
		setTitle("Cave Runner");
		setSize(850, 535);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(new GridBagLayout());
		
		//Instantiate components of layout
		borderPanel = new BorderPanel();
		mainPanel = new CaveRunnerGamePanel(this);
		scoreLabel = new JLabel("Score: 0");
		highscoreLabel = new JLabel("Highscore: 0");
		start = new JButton("Restart Game");
		
		//Set cosmetics of components
		borderPanel.setBackground(Color.gray);
		borderPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		
		//Add components to the frame
		addComponent(scoreLabel, 0, 0, 1, 1, 1, 0, 0, 0, 0, 10);
		addComponent(highscoreLabel, 1, 0, 1, 1, 1, 0, 0, 0, 0, 10);
		addComponent(borderPanel, 0, 1, 2, 1, 1, 1, 0, 0, 5, 5);
		addComponent(start, 0, 2, 2, 1, 0, 0, 200, 0, 0, 10);
		
		//Position main panel in border
		mainPanel.setBounds(17, 17, CaveRunnerGamePanel.PANEL_WIDTH, CaveRunnerGamePanel.PANEL_HEIGHT);
		borderPanel.add(mainPanel);
		borderPanel.setLayout(null);
		
		//Make keys work
		this.addKeyListener(this);
		
		//Display frame
		this.setVisible(true);
		
		//Make Start Game button start the game
		start.addActionListener(this);
		start.setFocusable(false);
		start.setEnabled(false);
		
		//Highscore setup
		try {
			highscoreFile = new File("highscore.txt");
			boolean newFile = highscoreFile.createNewFile();
			Scanner in = new Scanner(highscoreFile);
			if (newFile) {
				FileWriter out = new FileWriter(highscoreFile);
			    out.write("0");
			    out.close();
			}
			highscore = Integer.parseInt(in.nextLine());
			highscoreLabel.setText("Highscore: " + highscore);
			in.close();
		} catch (IOException e) {
			System.out.println("File error occured. Highscores will not be available");
			highscoreLabel.setText("");
		}
	}
	
	//Method to add component to GridLayout
	private void addComponent(Component c, int x, int y, int xSpan, int ySpan, double xWeight, double yWeight, int insetX, int insetY, int paddingX, int paddingY) {
		GridBagConstraints gbc = new GridBagConstraints(x, y, xSpan, ySpan, xWeight, yWeight, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(insetY, insetX, insetY, insetX), paddingX, paddingY);
		this.add(c, gbc);
	}
	
	//Start game button action
	public void actionPerformed(ActionEvent e) {
		System.out.println("button");
		if (e.getSource() == start) {
			score = 0;
			scoreLabel.setText("Score: 0");
			mainPanel.setVisible(true);
			mainPanel.startGame();
			borderPanel.resetLost();
			start.setEnabled(false);
		}
	}
	
	//Increase score labels
	public void increaseScore() {
		score++;
		scoreLabel.setText("Score: " + score);
		if (score > highscore) {
			highscore = score;
			highscoreLabel.setText("Highscore: " + highscore);
		}
	}
	
	//Save high score to file
	public void saveHighScore() {
		try {
			Scanner in = new Scanner(highscoreFile);
			int lastHighscore = Integer.parseInt(in.nextLine());
			if (lastHighscore < highscore) {
				FileWriter out = new FileWriter(highscoreFile);
			    out.write(Integer.toString(highscore));
			    out.close();
			}
			in.close();
		} catch (IOException e) {
			System.out.print("File error occured. Highscores will not be available");
			highscoreLabel.setText("");
		}
	}
	
	//Reset components on death
	public void onDeath() {
		saveHighScore();
		score = 0;
		start.setEnabled(true);
		mainPanel.setVisible(false);
		borderPanel.paintYouLose();
	}
	

	public void keyTyped(KeyEvent e) {}

	//Key press handling
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_UP:
				mainPanel.setJump(true);
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				mainPanel.setLeft(true);
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				mainPanel.setRight(true);
				break;
		}
	}

	//Key release handling
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_UP:
			mainPanel.setJump(false);
			break;
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			mainPanel.setLeft(false);
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
			mainPanel.setRight(false);
			break;
		}
	}

}
