/*
 * Name: Jack Whitman
 * Description: CaveBlock objects are one vertical pillar of the cave. The CaveBlock class provides functionality for gaps, jump blocks, fall blocks, multicolors, and random cave generation.
 */
package caveRunner;
import javax.swing.JPanel;
import java.awt.Color;
import java.lang.Math;
import java.util.Map;

public class CaveBlock {
	//Constants
	public static final int BLOCK_WIDTH = 40,  GAP = Player.CHAR_HEIGHT + 20, SEPARATION = GAP * 2, JUMP_FALL_HEIGHT = 100;
	private static final double GAP_CHANCE = 0.1, JUMP_FALL_CHANCE = 0.1;
	private static final int TOLERANCE = GAP / 3, TOP_BOTTOM_DISTANCE = 20;
	
	//Colors
	private static final Map<String, String> colors = Map.of(
			"normal", "#334766",
			"jump", "#1f2b3d",
			"fall", "#242b7e"
	);
	
	//Instance vars
	private JPanel[] panels;
	private int x, topHeight, closureAmount, blocksSinceLastGap;
	private boolean isGap, isClosed, isJumpBlock, isFallBlock, cutoffJumpBlock;
	
	//Create generic block
	public CaveBlock() {
		isClosed = cutoffJumpBlock = false;
		closureAmount = 0;
		panels = new JPanel[2];
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel();
			panels[i].setBackground(Color.decode(colors.get("normal")));
		}
	}
	
	//Create first block
	public CaveBlock(int xPos, boolean isFirst) {
		this();
		blocksSinceLastGap = 8;
		isGap = isFirst;
		isJumpBlock = false;
		x = xPos;
		topHeight = (CaveRunnerGamePanel.PANEL_HEIGHT) / 2 - GAP;
		draw();
	}
	
	//Create first block with some closure amount
	public CaveBlock(int xPos, int closureAmt, boolean isFirst) {
		this(xPos, isFirst);
		closureAmount = closureAmt;
	}
	
	//Create block off of previous block
	public CaveBlock(CaveBlock prev) {
		this();
		
		//Determine if this block is a gap block
		blocksSinceLastGap = prev.getBlocksSinceLastGap();
		if (blocksSinceLastGap < 10) {
			isGap = Math.random() < GAP_CHANCE;
		} else {
			isGap = true;
		}
		
		//Reset gap counter
		if (isGap) {
			blocksSinceLastGap = 0;
		} else {
			blocksSinceLastGap++;
		}
		
		//Sync closure amounts
		closureAmount = prev.getClosureAmount();
		
		//Decide if the block is a jump or fall block
		isJumpBlock = Math.random() < JUMP_FALL_CHANCE;
		isFallBlock = isJumpBlock ? false : Math.random() < JUMP_FALL_CHANCE;
		
		//Set x coord
		x = prev.getX() + BLOCK_WIDTH;
	
		//Set custom colors for jump and fall blocks
		if (isJumpBlock || isFallBlock) {
			Color blockColor = Color.decode(colors.get(isJumpBlock ? "jump" : "fall"));
			panels[0].setBackground(blockColor);
			panels[1].setBackground(blockColor);
		}
		
		//Set top heights for jump and fall blocks
		if (isJumpBlock) {
			topHeight = prev.getTopHeight() - JUMP_FALL_HEIGHT;
			if (topHeight < 0) {
				cutoffJumpBlock = true;
				topHeight = 0;
			}
		} else if (isFallBlock) {
			topHeight = prev.getTopHeight() + JUMP_FALL_HEIGHT;
			
			if (topHeight > CaveRunnerGamePanel.PANEL_HEIGHT - SEPARATION) topHeight = CaveRunnerGamePanel.PANEL_HEIGHT - SEPARATION;
		//Set random top height for normal block
		} else {
			do {
				topHeight = (int) (Math.random() * CaveRunnerGamePanel.PANEL_HEIGHT);
			} while (Math.abs(topHeight - prev.getTopHeight()) > TOLERANCE || topHeight < TOP_BOTTOM_DISTANCE || topHeight + SEPARATION + TOP_BOTTOM_DISTANCE > CaveRunnerGamePanel.PANEL_HEIGHT);
		}
		//Draw the block
		draw();
	}
	
	//Draw this block
	public void draw() {
		if (isGap) {
			panels[0].setBounds(x, 0, BLOCK_WIDTH, topHeight - GAP + closureAmount);
		} else {
			panels[0].setBounds(x, 0, BLOCK_WIDTH, topHeight + closureAmount);
		}
		panels[1].setBounds(x, topHeight + SEPARATION, BLOCK_WIDTH, CaveRunnerGamePanel.PANEL_HEIGHT - topHeight);	
	}
	
	//Setter methods
	public void setClosureAmount(int amt) {
		closureAmount = amt;
		draw();
	}
	
	public void setClosed(boolean closed) {
		isClosed = closed;
	}
	
	//Getter methods
	public JPanel[] getPanels() {
		return panels;
	}
	
	public int getClosureAmount() {
		return closureAmount;
	}
	
	public int getX() {
		return x;
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	
	public boolean isJumpBlock() {
		return isJumpBlock;
	}
	
	public boolean isCutoffJumpBlock() {
		return cutoffJumpBlock;
	}
	
	public boolean isFallBlock() {
		return isFallBlock;
	}
	public boolean isGap() {
		return isGap;
	}
	
	public int getTopHeight() {
		return topHeight;
	}
	
	public int getTopY() {
		return isGap ? topHeight - GAP + closureAmount : topHeight + closureAmount;
	}
	
	public int getBlocksSinceLastGap() {
		return blocksSinceLastGap;
	}
	
}