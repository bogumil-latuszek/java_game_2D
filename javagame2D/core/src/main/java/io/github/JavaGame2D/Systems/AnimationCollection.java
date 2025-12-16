package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import io.github.JavaGame2D.Enums.AnimationType;

import java.util.EnumMap;

public class AnimationCollection {
    private final EnumMap<AnimationType, Animation> animations;

    public AnimationCollection() {
        this.animations = new EnumMap<>(AnimationType.class);
    }

    public Texture getFrame(AnimationType animationType, int frameNumber){
        Animation animation = animations.get(animationType);
        if ( animation == null ){
            return null;
        }
        return animation.getFrame(frameNumber);
    }
}
