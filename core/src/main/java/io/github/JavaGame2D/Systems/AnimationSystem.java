package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.AnimationFrame;
import io.github.JavaGame2D.Components.AnimationComponent;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;
import io.github.JavaGame2D.Enums.FacingDirection;
import io.github.JavaGame2D.StateMachines.PlayerStateMachine;

import java.util.EnumMap;

public class AnimationSystem {

    private EntityComponentManager entityComponentManager;
    private PlayerStateMachine playerStateMachine;
    private AnimationManager animationManager;

    public AnimationSystem(EntityComponentManager entityComponentManager, FileSystem fileSystem, PlayerCharacterController playerController, TextureManager textureManager) {
        this.entityComponentManager = entityComponentManager;
        playerStateMachine = new PlayerStateMachine(entityComponentManager, playerController);
        animationManager = new AnimationManager();
        fileSystem.loadPlayerAnimations(this.animationManager, textureManager);
    }

    public void update(float deltaTimeInSeconds){
        // updates AnimationComponents using data from StateMachine:
        // 1. get all entities with drawable, transform, and animation components
//        long signature = ComponentSignatures.ANIMATION | ComponentSignatures.DRAWABLE;
//        int[] animatedEntities = entityComponentManager.getEntitiesMatchingSignature(signature);

        // 2. using data from animation component match it with state machine
        // #1 player is a special case
        int playerID = this.entityComponentManager.getPlayerEntityID();
        AnimationComponent animationComponent = entityComponentManager.getComponent(AnimationComponent.class, playerID);
        DrawableComponent drawableComponent = entityComponentManager.getComponent(DrawableComponent.class, playerID);

        playerStateMachine.update(deltaTimeInSeconds);
        EnumMap<BodySegmentType, AnimationState> currentPlayerState = playerStateMachine.bodySegmentAnimations;
        CharacterType characterType = animationComponent.characterType;
        BodySegmentType upperBody = BodySegmentType.UPPER_BODY;
        AnimationState animationState = currentPlayerState.get(upperBody);
        AnimationType animationType = animationState.animationType;
        float durationInMillis = animationState.durationInMilliseconds;
        AnimationFrame animationFrame = animationManager.getAnimationFrameByDuration(characterType, upperBody, animationType, durationInMillis);
        animationFrame = mirrorAnimationFrameIfFacingWrongDirection(animationFrame, animationState.facingDirection);
        copyAnimationFrameToDrawable(animationFrame,drawableComponent);
    }

    private void copyAnimationFrameToDrawable(AnimationFrame animationFrame, DrawableComponent drawable){
        drawable.textureID = animationFrame.textureID;
        drawable.facingDirection = animationFrame.facingDirection;
        drawable.width = animationFrame.width;
        drawable.height = animationFrame.height;
        drawable.offset = animationFrame.offset;
        drawable.mirrorHorizontal = animationFrame.mirrorHorizontal;
        drawable.mirrorVertical = animationFrame.mirrorVertical;
        drawable.usesSizeFromTexture = true;
        drawable.textureIsTiled = false;
        drawable.pixelsPerUnit = animationFrame.pixelsPerUnit;
    }

    private AnimationFrame mirrorAnimationFrameIfFacingWrongDirection(AnimationFrame animationFrame, FacingDirection direction){
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
