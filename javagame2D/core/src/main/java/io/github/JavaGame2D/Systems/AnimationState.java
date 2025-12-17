package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;

public class AnimationState {
    public CharacterType characterType; // may be redundant
    public BodySegmentType segmentType; // may be redundant
    public float durationInMilliseconds;
    public AnimationType animationType;
}
