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

    public void addAnimation(CharacterType characterType, AnimationType animationType, Animation animation){
        if (!animations.containsKey(characterType)){
            AnimationCollection animationCollection = new AnimationCollection();
            animations.put(characterType,animationCollection);
        }
        animations.get(characterType).addAnimation(animationType, animation);
    }

    public Texture getAnimationFrameByNumber(CharacterType characterType, AnimationType animationType, int frameNumber){
        AnimationCollection characterAnimations = animations.get(characterType);
        if (characterAnimations == null){
            return null;
        }
        return characterAnimations.getFrameByNumber(animationType, frameNumber);
    }

    public Texture getAnimationFrameByDuration(CharacterType characterType, AnimationType animationType, float durationInMilliseconds){
        AnimationCollection characterAnimations = animations.get(characterType);
        if (characterAnimations == null){
            return null;
        }
        return characterAnimations.getFrameByDuration(animationType, durationInMilliseconds);
    }
}



