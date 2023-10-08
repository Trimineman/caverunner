/*
 * Name: Jack Whitman
 * Description: The CaveBlockTweener class implements the Java Tween Library's TweenAccessor to specify the parameters that need to be changed when tweening the CaveBlock objects.
 */
package caveRunner;
import aurelienribon.tweenengine.*;

public class CaveBlockTweener implements TweenAccessor<CaveBlock> {

	//Gets closure amount
	public int getValues(CaveBlock target, int type, float[] returnValues) {
		returnValues[0] = target.getClosureAmount();
		return 1;
	}

	//Sets closure amount
	public void setValues(CaveBlock target, int type, float[] newValues) {
		target.setClosureAmount((int)newValues[0]);
	}

}