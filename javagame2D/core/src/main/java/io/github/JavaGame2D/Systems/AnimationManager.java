package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.CharacterType;

import java.util.EnumMap;

public class AnimationManager {

    private final EnumMap<CharacterType, AnimationCollection> animations;

    public AnimationManager(){
        this.animations = new EnumMap<>(CharacterType.class);
    }

    public Texture getAnimationFrame(CharacterType characterType, AnimationType animationType, int frameNumber){
        AnimationCollection characterAnimations = animations.get(characterType);
        if (characterAnimations == null){
            return null;
        }
        return characterAnimations.getFrame(animationType, frameNumber);
    }
}



