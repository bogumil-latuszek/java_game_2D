package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.SpriteData;
import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;

import java.util.EnumMap;

public class CharacterAnimations {
    private final EnumMap<BodySegmentType, BodySegmentAnimations> animations;

    public CharacterAnimations() {
        this.animations = new EnumMap<>(BodySegmentType.class);
    }

    public void addAnimation(BodySegmentType segmentType, AnimationType animationType, Animation animation){
        if (!animations.containsKey(segmentType)){
            BodySegmentAnimations bodySegmentAnimations = new BodySegmentAnimations();
            animations.put(segmentType, bodySegmentAnimations);
        }
        animations.get(segmentType).addAnimation(animationType, animation);
    }

    public SpriteData getFrameByNumber(BodySegmentType segment, AnimationType animationType, int frameNumber){
        BodySegmentAnimations segmentAnimations = animations.get(segment);
        if ( segmentAnimations == null ){
            return null;
        }
        return segmentAnimations.getFrameByNumber(animationType,frameNumber);
    }

    public SpriteData getFrameByDuration(BodySegmentType segment, AnimationType animationType, float durationInMilliseconds){
        BodySegmentAnimations segmentAnimations = animations.get(segment);
        if ( segmentAnimations == null ){
            return null;
        }
        return segmentAnimations.getFrameByDuration(animationType, durationInMilliseconds);
    }
}
