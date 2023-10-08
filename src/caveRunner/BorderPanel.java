/*
 * Name: Jack Whitman
 * Description: The BorderPanel class provides the background images for the game and paints the "You Lost" text on the crseen when it is needed.
 */
package caveRunner;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
@SuppressWarnings("serial")

public class BorderPanel extends JPanel {
	private boolean isLost;
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Paint background images
		g.drawImage(new ImageIcon("assets/purple_L1.png").getImage(), 17, 17, CaveRunnerGamePanel.PANEL_WIDTH, CaveRunnerGamePanel.PANEL_HEIGHT, null);
		g.drawImage(new ImageIcon("assets/purple_L2.png").getImage(), 17, 17, CaveRunnerGamePanel.PANEL_WIDTH, CaveRunnerGamePanel.PANEL_HEIGHT, null);
		g.drawImage(new ImageIcon("assets/purple_L3.png").getImage(), 17, 17, CaveRunnerGamePanel.PANEL_WIDTH, CaveRunnerGamePanel.PANEL_HEIGHT, null);
		g.drawImage(new ImageIcon("assets/purple_L4.png").getImage(), 17, 17, CaveRunnerGamePanel.PANEL_WIDTH, CaveRunnerGamePanel.PANEL_HEIGHT, null);
		
		//Paint loser text if the user has lost
		if (isLost) {
			g.setFont(new Font("Segoe UI", Font.BOLD, 50));
			g.setColor(Color.red);
			g.drawString("You lost...", 300, 250);
			g.setFont(new Font("Comic Sans MS", Font.ITALIC, 30));
			g.setColor(Color.green);
			g.drawString("Press Start to Play Again", 240, 290);
			g.setFont(new Font("Comic Sans MS", 0, 10));
			g.setColor(Color.gray);
			g.drawString("thank you for playing! Cave Runner by Jack Whitman, APCS Final Project", 240, 310);
		}
     }
	
	//Start painting loser text
	public void paintYouLose() {
		Graphics2D g = ((Graphics2D) getGraphics());
		g.setFont(new Font("Segoe UI", Font.BOLD, 50));
		g.setColor(Color.red);
		g.drawString("You lost...", 300, 250);
		g.setFont(new Font("Segoe UI", Font.ITALIC, 30));
		g.setColor(Color.green);
		g.drawString("Press Start to Play Again", 240, 290);
		g.setFont(new Font("Comic Sans MS", 0, 10));
		g.setColor(Color.gray);
		g.drawString("thank you for playing! Cave Runner by Jack Whitman, APCS Final Project", 240, 310);;
		isLost = true;
	}
	
	//Reset loser text
	public void resetLost() {
		isLost = false;
	}
}
