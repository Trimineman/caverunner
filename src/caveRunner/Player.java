/*
 * Name: Jack Whitman
 * Description: The Player class functions as a label to display player animations and move around the screen
 */
package caveRunner;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Player extends JLabel {
	//Constants
	public final static int CHAR_HEIGHT = 74;
	public final static int CHAR_WIDTH = 100;
	
	//Static mathods for images
	//Scale image
	public static ImageIcon scale(ImageIcon img, int width, int height) {
		return new ImageIcon(img.getImage().getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING));
	}
		
	public static ImageIcon flip(ImageIcon img){
		BufferedImage sprite = toBufferedImage(img.getImage());
	    BufferedImage buffimg = new BufferedImage(sprite.getWidth(),sprite.getHeight(),BufferedImage.TYPE_INT_ARGB);
	    for(int xx = sprite.getWidth()-1;xx>0;xx--){
	    	for(int yy = 0;yy < sprite.getHeight();yy++){
	    		buffimg.setRGB(sprite.getWidth()-xx, yy, sprite.getRGB(xx, yy));
	    	}
	    }
	    return new ImageIcon(buffimg);
	}
		
	//Convert image to a buffered image
	public static BufferedImage toBufferedImage(Image img) {
		//Return image if it is a buffered image
	    if (img instanceof BufferedImage) return (BufferedImage) img;

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}

	
	//Arrays of animations
	private final static ImageIcon[] idleAnims = {
		scale(new ImageIcon("assets/adventurer-idle-00.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-idle-01.png"), CHAR_WIDTH, CHAR_HEIGHT), 
		scale(new ImageIcon("assets/adventurer-idle-02.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-idle-03.png"), CHAR_WIDTH, CHAR_HEIGHT)
	};
	
	private final static ImageIcon[] runAnims = {
		scale(new ImageIcon("assets/adventurer-run-00.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-run-01.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-run-02.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-run-03.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-run-04.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-run-05.png"), CHAR_WIDTH, CHAR_HEIGHT)
	};
	
	private final static ImageIcon[] jumpAnims = {
		scale(new ImageIcon("assets/adventurer-jump-00.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-jump-01.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-jump-02.png"), CHAR_WIDTH, CHAR_HEIGHT),
		scale(new ImageIcon("assets/adventurer-jump-03.png"), CHAR_WIDTH, CHAR_HEIGHT)
	};
	
	private final static ImageIcon[] flippedIdleAnims = {
		flip(idleAnims[0]),
		flip(idleAnims[1]),
		flip(idleAnims[2]),
		flip(idleAnims[3])
	};
	
	private final static ImageIcon[] flippedRunAnims = {
		flip(runAnims[0]),
		flip(runAnims[1]),
		flip(runAnims[2]),
		flip(runAnims[3]),
		flip(runAnims[4]),
		flip(runAnims[5])
	};
	
	private final static ImageIcon[] flippedJumpAnims = {
		flip(jumpAnims[0]),
		flip(jumpAnims[1]),
		flip(jumpAnims[2]),
		flip(jumpAnims[3])
	};
	

	//Instance vars
	private int x;
	private int y;
	private int idleIndex;
	private int runIndex;
	private int jumpIndex;
	private long animationCounter;
	
	//Create player
	public Player(int xPos, int yPos) {
		x = xPos;
		y = yPos;
		idleIndex = 0;
		runIndex = 0;
		jumpIndex = 0;
		this.setIcon(scale(idleAnims[0], CHAR_WIDTH, CHAR_HEIGHT));
		drawAt(x, y);
	}
	
	//Method to increase animation frame
	public void animate(String type, boolean isFacingBack) {
		//Animate every seven frames
		if (animationCounter++ % 7 == 0) {
			//Animate based on type of action and direction
			if (type.equals("idle")) {
				idleIndex++;
				jumpIndex = 0;
				
				this.setIcon(isFacingBack ? flippedIdleAnims[idleIndex % flippedIdleAnims.length] : idleAnims[idleIndex % idleAnims.length]);
			} else if (type.equals("run")) {
				runIndex++;
				jumpIndex = 0;
				this.setIcon(isFacingBack ? flippedRunAnims[runIndex % flippedRunAnims.length] : runAnims[runIndex % runAnims.length]);
			} else if (type.equals("jump")) {
				if (jumpIndex < jumpAnims.length - 1) {
					jumpIndex++;
				}
				this.setIcon(isFacingBack ? flippedJumpAnims[jumpIndex] : jumpAnims[jumpIndex]);
			}
		}
		
	}
	
	//Getter methods
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	//Draw player
	public void drawAt(int xPos, int yPos) {
		x = xPos;
		y = yPos;
		this.setBounds(x, y, CHAR_WIDTH, CHAR_HEIGHT);
	}
	
}
