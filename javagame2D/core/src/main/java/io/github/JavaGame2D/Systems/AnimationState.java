package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;

public class AnimationState {
    //public CharacterType characterType = CharacterType.UNDEFINED; // may be redundant
    //public BodySegmentType segmentType = BodySegmentType.UNDEFINED; // may be redundant
    public float durationInMilliseconds = 0;
    public AnimationType animationType = AnimationType.IDLE;
}
