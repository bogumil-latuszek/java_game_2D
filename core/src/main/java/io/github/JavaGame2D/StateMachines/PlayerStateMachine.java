package io.github.JavaGame2D.StateMachines;

import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.*;
import io.github.JavaGame2D.Systems.AnimationState;
import io.github.JavaGame2D.Systems.EntityComponentManager;
import io.github.JavaGame2D.Systems.PlayerCharacterController;

import java.util.EnumMap;
import java.util.HashMap;

public class PlayerStateMachine {
    private DecidingFactorsForPlayer decidingFactors;
    private final EntityComponentManager entityComponentManager;
    public final CharacterType characterType = CharacterType.PLAYER;
    // UpperBodyState upperBodyState <= can be null
    // lowerBodyState <= can be null
    // fullBodyState <= can be null
    public EnumMap<BodySegmentType, AnimationState> bodySegmentAnimations;
    private PlayerCharacterController playerController;

    // StateMachine upperBodyStateMachine; = new StateMachine(UpperBodyState);
    public PlayerStateMachine(EntityComponentManager entityComponentManager, PlayerCharacterController playerController){
        this.entityComponentManager = entityComponentManager;
        this.bodySegmentAnimations = new EnumMap<>(BodySegmentType.class);
        this.bodySegmentAnimations.put(BodySegmentType.UPPER_BODY, new AnimationState());
        this.bodySegmentAnimations.put(BodySegmentType.LOWER_BODY, new AnimationState());
        this.decidingFactors = new DecidingFactorsForPlayer();
        this.playerController = playerController;
    }

    public void update(float deltaTimeInSeconds){
        float deltaTimeInMilliseconds = deltaTimeInSeconds * 1000;
        // 1. get player entity
        int playerID = entityComponentManager.getPlayerEntityID();
        // 2. we're always going to assume that player entity has certain components;

        TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class, playerID);
        PhysicalBodyComponent body = entityComponentManager.getComponent(PhysicalBodyComponent.class, playerID);

        decidingFactors.direction = body.velocity;
        decidingFactors.grounded = body.onGround;
        decidingFactors.horizontalMovementAction = playerController.horizontalMovement;
        decidingFactors.verticalMovementAction = playerController.verticalMovement;
        decidingFactors.specialAction = playerController.specialAction;
        updateLowerBodyState(decidingFactors, deltaTimeInMilliseconds);
        updateUpperBodyState(decidingFactors, deltaTimeInMilliseconds);
        //updateReactionState(decidingFactors);
    }

    private void updateUpperBodyState(DecidingFactorsForPlayer decidingFactors, float deltaTimeInMilliseconds){
        AnimationState currentAnimationState = bodySegmentAnimations.get(BodySegmentType.UPPER_BODY);
        AnimationType currentAnimation = currentAnimationState.animationType;
        AnimationType nextAnimation = currentAnimation;
        FacingDirection facingDirection = FacingDirection.NONE;
        if(decidingFactors.grounded){
            // player on the ground
            switch (decidingFactors.horizontalMovementAction){
                case GO_LEFT:
                    nextAnimation = AnimationType.WALKING;
                    facingDirection = FacingDirection.LEFT;
                    break;
                case GO_RIGHT:
                    nextAnimation = AnimationType.WALKING;
                    facingDirection = FacingDirection.RIGHT;
                    break;
                case NONE:
                    nextAnimation = AnimationType.IDLE;
                    break;
            }
        }
        else {
            // player in the air
            if (decidingFactors.direction.y >= 0){
                //player rising
                nextAnimation = AnimationType.JUMPING;
                switch (decidingFactors.horizontalMovementAction){
                    case GO_LEFT:
                        facingDirection = FacingDirection.LEFT;
                        break;
                    case GO_RIGHT:
                        facingDirection = FacingDirection.RIGHT;
                        break;
                }
            }
            else{
                // player falling
                nextAnimation = AnimationType.FALLING;
                switch (decidingFactors.horizontalMovementAction){
                    case GO_LEFT:
                        facingDirection = FacingDirection.LEFT;
                        break;
                    case GO_RIGHT:
                        facingDirection = FacingDirection.RIGHT;
                        break;
                }
            }
        }
        if (decidingFactors.specialAction == PlayerAction.ATTACK){
            nextAnimation = AnimationType.ATTACKING;
            switch (decidingFactors.horizontalMovementAction){
                case GO_LEFT:
                    facingDirection = FacingDirection.LEFT;
                    break;
                case GO_RIGHT:
                    facingDirection = FacingDirection.RIGHT;
                    break;
                default:
                    //use saved last_faced_direction
                    break;
            }
        }
        if (nextAnimation == currentAnimation){
            currentAnimationState.durationInMilliseconds += deltaTimeInMilliseconds;
        }
        else{
            currentAnimationState.durationInMilliseconds = 0;
            currentAnimationState.animationType = nextAnimation;
        }
        if (facingDirection != FacingDirection.NONE){
            //this is a HACK of sorts, it's not very intuitive why it works
            //TODO:refactor facing direction logic, is there really a need for more directions then left-right for animations?
            currentAnimationState.facingDirection = facingDirection;
        }
        //this.bodySegmentAnimations.put(BodySegmentType.UPPER_BODY, currentAnimationState );
    }

    private void updateLowerBodyState(DecidingFactorsForPlayer decidingFactors, float deltaTimeInMilliseconds){
        AnimationState currentAnimationState = bodySegmentAnimations.get(BodySegmentType.LOWER_BODY);
        AnimationType currentAnimation = currentAnimationState.animationType;
        AnimationType nextAnimation = currentAnimation;
        FacingDirection facingDirection = FacingDirection.NONE;
        if(decidingFactors.grounded){
            // player on the ground
            switch (decidingFactors.horizontalMovementAction){
                case GO_LEFT:
                    nextAnimation = AnimationType.WALKING;
                    facingDirection = FacingDirection.LEFT;
                    break;
                case GO_RIGHT:
                    nextAnimation = AnimationType.WALKING;
                    facingDirection = FacingDirection.RIGHT;
                    break;
                case NONE:
                    nextAnimation = AnimationType.IDLE;
                    break;
            }
        }
        else {
            // player in the air
            if (decidingFactors.direction.y >= 0){
                //player rising
                nextAnimation = AnimationType.JUMPING;
                switch (decidingFactors.horizontalMovementAction){
                    case GO_LEFT:
                        facingDirection = FacingDirection.LEFT;
                        break;
                    case GO_RIGHT:
                        facingDirection = FacingDirection.RIGHT;
                        break;
                }
            }
            else{
                // player falling
                nextAnimation = AnimationType.FALLING;
                switch (decidingFactors.horizontalMovementAction){
                    case GO_LEFT:
                        facingDirection = FacingDirection.LEFT;
                        break;
                    case GO_RIGHT:
                        facingDirection = FacingDirection.RIGHT;
                        break;
                }
            }
        }
        if (nextAnimation == currentAnimation){
            currentAnimationState.durationInMilliseconds += deltaTimeInMilliseconds;
            currentAnimationState.facingDirection = facingDirection;
        }
        else{
            currentAnimationState.durationInMilliseconds = 0;
            currentAnimationState.animationType = nextAnimation;
            currentAnimationState.facingDirection = facingDirection;
        }
        //this.bodySegmentAnimations.put(BodySegmentType.LOWER_BODY, currentAnimationState );
    }

}
