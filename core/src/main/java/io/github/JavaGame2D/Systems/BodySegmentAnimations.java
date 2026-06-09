package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import io.github.JavaGame2D.Drawable;
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

    public Drawable getFrameByNumber(AnimationType animationType, int frameNumber){
        Animation animation = animations.get(animationType);
        if ( animation == null ){
            return null;
        }
        return animation.getFrameByNumber(frameNumber);
    }

    public Drawable getFrameByDuration(AnimationType animationType, float durationInMilliseconds){
        Animation animation = animations.get(animationType);
        if ( animation == null ){
            return null;
        }
        return animation.getFrameByDuration(durationInMilliseconds);
    }
}
