package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;
import io.github.JavaGame2D.Enums.FacingDirection;

public class AnimationState {
    //public CharacterType characterType = CharacterType.UNDEFINED; // may be redundant
    //public BodySegmentType segmentType = BodySegmentType.UNDEFINED; // may be redundant
    public float durationInMilliseconds = 0;
    public AnimationType animationType = AnimationType.IDLE;
    public FacingDirection facingDirection = FacingDirection.NONE;

    public AnimationState makeCopy(){
        AnimationState temp = new AnimationState();
        temp.durationInMilliseconds = this.durationInMilliseconds;
        temp.animationType = this.animationType;
        temp.facingDirection = this.facingDirection;
        return temp;
    }
}
