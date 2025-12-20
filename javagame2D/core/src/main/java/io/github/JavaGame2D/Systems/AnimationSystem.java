package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import io.github.JavaGame2D.Components.AnimationComponent;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Drawable;
import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;
import io.github.JavaGame2D.Enums.FacingDirection;
import io.github.JavaGame2D.StateMachines.PlayerStateMachine;

import java.util.EnumMap;
import java.util.HashMap;

public class AnimationSystem {

    private EntityComponentManager entityComponentManager;
    private PlayerStateMachine playerStateMachine;
    private AnimationManager animationManager;

    public AnimationSystem(EntityComponentManager entityComponentManager, FileSystem fileSystem, PlayerCharacterController playerController) {
        this.entityComponentManager = entityComponentManager;
        playerStateMachine = new PlayerStateMachine(entityComponentManager, playerController);
        animationManager = new AnimationManager();
        fileSystem.loadPlayerAnimations(this.animationManager);
    }

    public void update(float deltaTimeInMilliseconds){
        // updates AnimationComponents using data from StateMachine:
        // 1. get all entities with drawable, transform, and animation components
//        long signature = ComponentSignatures.ANIMATION | ComponentSignatures.DRAWABLE;
//        int[] animatedEntities = entityComponentManager.getEntitiesMatchingSignature(signature);

        // 2. using data from animation component match it with state machine
        // #1 player is a special case
        int playerID = this.entityComponentManager.getPlayerEntityID();
        AnimationComponent animationComponent = entityComponentManager.getComponent(AnimationComponent.class, playerID);
        DrawableComponent drawableComponent = entityComponentManager.getComponent(DrawableComponent.class, playerID);

        playerStateMachine.update(deltaTimeInMilliseconds);
        EnumMap<BodySegmentType, AnimationState> currentPlayerState = playerStateMachine.bodySegmentAnimations;
        BodySegmentType[] allSegments = currentPlayerState.keySet().toArray(new BodySegmentType[currentPlayerState.size()]);

        CharacterType characterType = animationComponent.characterType;
        for (BodySegmentType segmentType : allSegments){
            if(drawableComponent.drawableSegments.containsKey(segmentType)){
                AnimationState animationState = currentPlayerState.get(segmentType);
                AnimationType animationType = animationState.animationType;
                float durationInMillis = animationState.durationInMilliseconds;
                Drawable animationFrame = animationManager.getAnimationFrameByDuration(characterType, segmentType, animationType, durationInMillis);
                animationFrame = mirrorAnimationFrameIfFacingWrongDirection(animationFrame, animationState.facingDirection);
                drawableComponent.drawableSegments.put(segmentType, animationFrame);
            }
        }
    }

    private Drawable mirrorAnimationFrameIfFacingWrongDirection(Drawable animationFrame, FacingDirection direction){
        // instead of having separate frames for left/right, up/down, we're going to mirror existing ones
        // never mirror frames that don't face any direction:
        if (animationFrame.facingDirection != FacingDirection.NONE){
            // only mirror frame if it's facing an opposite direction than the animation:
            if (direction == FacingDirection.LEFT ||
                direction == FacingDirection.RIGHT){
                if (direction != animationFrame.facingDirection){
                    animationFrame.mirrorVertical = true;
                }
                else  animationFrame.mirrorVertical = false;
            }
            if (direction == FacingDirection.UP ||
                direction == FacingDirection.DOWN){
                if (direction != animationFrame.facingDirection){
                    animationFrame.mirrorHorizontal = true;
                }
                else  animationFrame.mirrorHorizontal = false;
            }
        }
        return  animationFrame;
    }


}
