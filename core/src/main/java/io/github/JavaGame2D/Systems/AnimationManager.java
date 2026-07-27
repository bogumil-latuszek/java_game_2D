package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.SpriteData;
import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;

import java.util.EnumMap;

public class AnimationManager {

    private final EnumMap<CharacterType, CharacterAnimations> animations;

    public AnimationManager(){
        this.animations = new EnumMap<>(CharacterType.class);
    }

    public void addAnimation(CharacterType characterType, BodySegmentType segment, AnimationType animationType, Animation animation){
        if (!animations.containsKey(characterType)){
            CharacterAnimations characterAnimations = new CharacterAnimations();
            animations.put(characterType, characterAnimations);
        }
        animations.get(characterType).addAnimation(segment, animationType, animation);
    }

    public SpriteData getAnimationFrameByNumber(CharacterType characterType, BodySegmentType segmentType, AnimationType animationType, int frameNumber){
        CharacterAnimations characterAnimations = animations.get(characterType);
        if (characterAnimations == null){
            return null;
        }
        return characterAnimations.getFrameByNumber(segmentType, animationType, frameNumber);
    }

    public SpriteData getAnimationFrameByDuration(CharacterType characterType, BodySegmentType segmentType, AnimationType animationType, float durationInMilliseconds){
        CharacterAnimations characterAnimations = animations.get(characterType);
        if (characterAnimations == null){
            return null;
        }
        return characterAnimations.getFrameByDuration( segmentType, animationType, durationInMilliseconds);
    }
}



