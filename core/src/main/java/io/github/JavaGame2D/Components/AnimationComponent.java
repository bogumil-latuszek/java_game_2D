package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;
import io.github.JavaGame2D.Systems.Animation;
import io.github.JavaGame2D.Systems.AnimationState;
import io.github.JavaGame2D.Systems.BodySegmentAnimations;

import java.util.EnumMap;
import java.util.HashMap;

public class AnimationComponent {
    public CharacterType characterType = CharacterType.PLAYER;
    public EnumMap<BodySegmentType, AnimationState> segmentAnimations = new EnumMap<>(BodySegmentType.class);
}
