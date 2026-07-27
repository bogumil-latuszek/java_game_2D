package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.SpriteData;
import io.github.JavaGame2D.Enums.AnimationType;

import java.util.EnumMap;

public class BodySegmentAnimations {
    private final EnumMap<AnimationType, Animation> animations;

    public BodySegmentAnimations() {
        this.animations = new EnumMap<>(AnimationType.class);
    }

    public void addAnimation(AnimationType animationType, Animation animation){
        animations.put(animationType,animation);
    }

    public SpriteData getFrameByNumber(AnimationType animationType, int frameNumber){
        Animation animation = animations.get(animationType);
        if ( animation == null ){
            return null;
        }
        return animation.getFrameByNumber(frameNumber);
    }

    public SpriteData getFrameByDuration(AnimationType animationType, float durationInMilliseconds){
        Animation animation = animations.get(animationType);
        if ( animation == null ){
            return null;
        }
        return animation.getFrameByDuration(durationInMilliseconds);
    }
}
