/*
 * Name: Jack Whitman
 * Description: The CaveRunnerGamePanel class is the most complex class in the game. It creates a JLayeredPane that displays the game and handles player movement, physics, collisions, timing, animation, death, game creation, game restart, and cave generation.
 */
package caveRunner;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLayeredPane;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class CaveRunnerGamePanel extends JLayeredPane implements ActionListener {
	
	//Constants
	public final static int PANEL_WIDTH = 800;
	public final static int PANEL_HEIGHT = 400;
	private final static int JUMP_HEIGHT = 15;
	private final static int WALK_SPEED = 4;
	private final static double GRAVITY = -1;
	private final static float CLOSE_TIME = 3.0f;
	private final static float OPEN_TIME = 2.0f;
	private final static int CAVE_INTERVAL = 10000;
	
	//Instance vars for game components
	private Player plr;
	private CaveBlock[] blocks;
	private CaveRunnerGameFrame parentFrame;
	
	//Instance vars for animation and movement
	private boolean left, right, startJump, isOnGround, lastDirectionLeft;
	private int nearestBlockIndex, vy, highestBlock, coordInBlock;
	
	//Instance vars for game handling
	private int level, timeElapsedMs;
	private float newOpenTime, newCloseTime;
	private boolean wasOpening;
	private float openElapsedSec;

	//Tweening instance vars
	private TweenManager manager;
	private TweenCallback closeCallback, openCallback, startTimerCallback;
	
	//Timer instance vars
	private Timer animTimer, caveTimer;
	
	public CaveRunnerGamePanel(CaveRunnerGameFrame parentFrame) {
		//Set vars to default values
		level = timeElapsedMs = 0;
		newOpenTime = OPEN_TIME;
		newCloseTime = CLOSE_TIME;
		setLayout(null);
		this.parentFrame = parentFrame;
		manager = new TweenManager();
		isOnGround = false;
		nearestBlockIndex = 0;
		openElapsedSec = 0.0f;
		wasOpening = false;
		lastDirectionLeft = false;
		highestBlock = 0;
		
		//Set up tween callbacks
		closeCallback = new TweenCallback() {
			public void onEvent(int arg0, BaseTween<?> arg1) {
				blocks[0].setClosed(true);
			}
		};
		
		openCallback = new TweenCallback() {
			public void onEvent(int arg0, BaseTween<?> arg1) {
				blocks[0].setClosed(false);
				openElapsedSec = 0.0f;
			}
		};

		startTimerCallback = new TweenCallback() {
			public void onEvent(int arg0, BaseTween<?> arg1) {
				caveTimer.start();
			}
		};
		
		//Create array of blocks
		blocks = new CaveBlock[PANEL_WIDTH / CaveBlock.BLOCK_WIDTH];
		blocks[0] = new CaveBlock(0, true);
		for (int i = 1; i < blocks.length; i++)
			blocks[i] = new CaveBlock(blocks[i-1]);
		
		//Add blocks to panel
		for(int i = 0; i < blocks.length; i++)
			for (int j = 0; j < blocks[i].getPanels().length; j++)
				this.add(blocks[i].getPanels()[j], 1);
		
		//Create and add player
		plr = new Player(0, 200);
		this.add(plr, 2);
		
		//Create and start timers
		animTimer = new Timer(10, this);
		animTimer.start();
		caveTimer = new Timer(CAVE_INTERVAL, this);
		caveTimer.setInitialDelay(5000);
		caveTimer.start();	
	}
	
	//Method connected to restart game button
	public void startGame() {
		nearestBlockIndex = 0;
		animTimer.restart();
		caveTimer.restart();
		level = -1;
		regen();
	}
	
	//Animate loop method
	private void animate() {
		//Update tween manager
		manager.update(0.01f);
		
		//Update timing
		timeElapsedMs += 10;
		if (blocks[0].isClosed() == true) openElapsedSec += 0.01f;
		
		//Local vars for position
		int blockY = blocks[nearestBlockIndex].getPanels()[1].getY();
		int distanceToGround = plr.getY() + Player.CHAR_HEIGHT - blockY;
		
		//Local vars for movement restrictions
		boolean canLeft = true, canRight = true, canJump = true;
		boolean nextBlockCutoffJump = (coordInBlock >= CaveBlock.BLOCK_WIDTH / 2 && (nearestBlockIndex < 19 ? blocks[nearestBlockIndex + 1].isCutoffJumpBlock() : false));
		boolean nextBlockJump = !nextBlockCutoffJump && (coordInBlock >= CaveBlock.BLOCK_WIDTH / 2 && (nearestBlockIndex < 19 ? blocks[nearestBlockIndex + 1].isJumpBlock() : false));
		boolean prevBlockGap = (coordInBlock < CaveBlock.BLOCK_WIDTH / 2 && (nearestBlockIndex > 0 ? blocks[nearestBlockIndex - 1].isGap() : false));
		boolean nextBlockGap = (coordInBlock >= CaveBlock.BLOCK_WIDTH / 2 && (nearestBlockIndex < 19 ? blocks[nearestBlockIndex + 1].isGap() : false));
		boolean closedBelowHead = blocks[nearestBlockIndex].getClosureAmount() > CaveBlock.SEPARATION - Player.CHAR_HEIGHT + 10;
		
		//Instance var updates
		isOnGround = plr.getY() + Player.CHAR_HEIGHT >= blockY;
		nearestBlockIndex = (plr.getX() + Player.CHAR_WIDTH / 2) / CaveBlock.BLOCK_WIDTH;
		coordInBlock = (plr.getX() + Player.CHAR_WIDTH / 2) % CaveBlock.BLOCK_WIDTH;
		
		int yDistToNext = nearestBlockIndex < 19 ? (plr.getY() + Player.CHAR_HEIGHT) - (blocks[nearestBlockIndex + 1].getPanels()[1].getY()) : 0;

		//Gravity
		if (!isOnGround) {
			vy -= GRAVITY;
			plr.drawAt(plr.getX(), plr.getY() + vy);
		}
		
		//Figure out score
		if (nearestBlockIndex > highestBlock) {
			highestBlock = nearestBlockIndex;
			parentFrame.increaseScore();
		}
		
		//Regenerate if at end
		if (nearestBlockIndex >= blocks.length) {
			nearestBlockIndex = 0;
			regen();
		}
		
		//Block player if they are past a fall block
		if (blocks[nearestBlockIndex].isFallBlock() && coordInBlock < CaveBlock.BLOCK_WIDTH / 2) canLeft = canJump = false;
		
		//Block player if they need to jump up
		if ((nextBlockJump && yDistToNext > 0) || (nextBlockCutoffJump && yDistToNext > 50)) canRight = false;
		
		//Move player up to the ground
		if (!nextBlockJump)
			if (distanceToGround < CaveBlock.JUMP_FALL_HEIGHT)
				while (plr.getY() + Player.CHAR_HEIGHT > blockY)
					plr.drawAt(plr.getX(), plr.getY() - 1);
		
		//Prevent movement outside of gaps when cave is closed
		if (blocks[nearestBlockIndex].isGap() && closedBelowHead) {
			canLeft = canRight = false;
			if (prevBlockGap || coordInBlock > 10) canLeft = true;
			if (nextBlockGap || coordInBlock < CaveBlock.BLOCK_WIDTH - 10) canRight = true;
		}
		
		//Check if player should be dead
		if (!blocks[nearestBlockIndex].isGap() && closedBelowHead) {
			caveTimer.stop();
			animTimer.stop();
			parentFrame.onDeath();
		}
		
		//Ceiling collisions
		if (plr.getY() + 5 < blocks[nearestBlockIndex].getTopY() && !closedBelowHead) {
			vy = 0;
			while (plr.getY() < blocks[nearestBlockIndex].getTopY())
				plr.drawAt(plr.getX(), plr.getY() + 1);
		}
		
		//Jump, move left, and move right, with animations
		if (startJump == true && isOnGround && canJump) {
			vy = -JUMP_HEIGHT;
			plr.drawAt(plr.getX(), plr.getY() - JUMP_HEIGHT);
		}
		
		if (!isOnGround) plr.animate("jump", lastDirectionLeft);
		
		if (left == true && canLeft && plr.getX() > -30) {
			if (isOnGround) plr.animate("run", true);
			plr.drawAt(plr.getX() - WALK_SPEED, plr.getY());
			lastDirectionLeft = true;
		} 
		
		if (right == true && canRight) {
			if (isOnGround) plr.animate("run", false);
			plr.drawAt(plr.getX() + WALK_SPEED, plr.getY());
			lastDirectionLeft = false;
		} 
		
		//Idle animation
		if (isOnGround && (!startJump && !left && !right || ((left && !canLeft) || (right && !canRight)))) plr.animate("idle", lastDirectionLeft);
	}
	
	//Regenerate
	public void regen() {
		//Reset vars and redraw player
		level++;
		highestBlock = 0;
		plr.drawAt(-Player.CHAR_WIDTH/2, 200);
		
		
		//Exponential equation for cave interval
		int newCaveInterval = (int)Math.pow(0.95, level - 172.61) + 3000;
		int nextInterval = newCaveInterval - timeElapsedMs;
		
		//Exponential equations for new open and close time
		newCloseTime = (float)Math.pow(0.95, level - 17.864) + 0.5f;
		newOpenTime = (float)Math.pow(0.95, level - 10.34) + 0.3f;
		
		//Speed up game
		caveTimer.stop();
		caveTimer.setDelay(newCaveInterval);
		caveTimer.setInitialDelay(nextInterval > 0 ? nextInterval : 1);
		
		
		
		//Remake array of blocks
		int prevClosure = blocks[0].getClosureAmount();
		boolean prevClosed = blocks[0].isClosed();
		
		for(int i = 0; i < blocks.length; i++) {
			blocks[i].setClosed(false);
			for (int j = 0; j < blocks[i].getPanels().length; j++)
				this.remove(blocks[i].getPanels()[j]);
		}
			
		
		parentFrame.repaint();
		blocks = new CaveBlock[PANEL_WIDTH / CaveBlock.BLOCK_WIDTH];
		blocks[0] = new CaveBlock(0, level == 0);
		
		for (int i = 1; i < blocks.length; i++)
			blocks[i] = new CaveBlock(blocks[i-1]);
		
		//Add blocks to panel
		for(int i = 0; i < blocks.length; i++)
			for (int j = 0; j < blocks[i].getPanels().length; j++)
				this.add(blocks[i].getPanels()[j], 1);
		
				
		//Reset cave closing/opening to previous position
		if (prevClosure != 0) {
			caveTimer.stop();
			timeElapsedMs = 0;
			if (prevClosed == false && wasOpening == false) {
				for (int i = 0; i < blocks.length; i++)
					Tween.to(blocks [i], 0, newCloseTime).target(CaveBlock.SEPARATION).setCallback(closeCallback).ease(Quad.OUT).start(manager);
				
				for (int i = 0; i < blocks.length; i++)
					Tween.to(blocks[i], 0, newOpenTime).target(0).delay(newCloseTime).setCallback(openCallback).setCallback(startTimerCallback).start(manager);
			} else {
				for (int i = 0; i < blocks.length; i++) {
					wasOpening = true;
					Tween.to(blocks[i], 0, newOpenTime - openElapsedSec > 0 ? newOpenTime - openElapsedSec : 0.01f).setCallback(openCallback).setCallback(startTimerCallback).target(0).start(manager);
				}
			}	
			caveTimer.setInitialDelay(newCaveInterval);
		} else {
			caveTimer.start();
		}
		
		System.out.println("\nNew Interval: " + newCaveInterval);
		System.out.println("New Open Time: " + newOpenTime);
		System.out.println("New Close Time: " + newCloseTime);
	}

	//Open and close cave
	public void moveCave() {
		wasOpening = false;
		timeElapsedMs = 0;
		//Close
		for (int i = 0; i < blocks.length; i++)
			Tween.to(blocks[i], 0, newCloseTime).target(CaveBlock.SEPARATION).setCallback(closeCallback).ease(Quad.OUT).start(manager);
		//Open
		for (int i = 0; i < blocks.length; i++)
			Tween.to(blocks[i], 0, newOpenTime).target(0).delay(newCloseTime).setCallback(openCallback).start(manager);
	}
	
	//Timer handling
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == animTimer) animate();
		else if (e.getSource() == caveTimer) moveCave();
	}
	
	//Keyboard action handling
	public void setJump(boolean jump) {
		this.startJump = jump;
	}
	
	public void setLeft(boolean left) {
		this.left = left;
	}
	
	public void setRight(boolean right) {
		this.right = right;
	}
}