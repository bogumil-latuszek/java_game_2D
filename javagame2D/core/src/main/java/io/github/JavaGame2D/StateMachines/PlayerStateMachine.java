package io.github.JavaGame2D.StateMachines;

import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;
import io.github.JavaGame2D.Enums.PlayerAction;
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

    public void update(float deltaTimeInMilliseconds){
        // 1. get player entity
        int playerID = entityComponentManager.getPlayerEntityID();
        // 2. we're always going to assume that player entity has certain components;

        TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class, playerID);
        PhysicalBodyComponent body = entityComponentManager.getComponent(PhysicalBodyComponent.class, playerID);

        decidingFactors.direction = body.velocity;
        decidingFactors.grounded = body.onGround;
        decidingFactors.playerAction = playerController.currentPlayerAction;
        updateLowerBodyState(decidingFactors, deltaTimeInMilliseconds);
        updateUpperBodyState(decidingFactors, deltaTimeInMilliseconds);
        //updateReactionState(decidingFactors);
    }

    private void updateLowerBodyState(DecidingFactorsForPlayer decidingFactors, float deltaTimeInMilliseconds){
        AnimationState currentAnimationState = bodySegmentAnimations.get(BodySegmentType.LOWER_BODY);
        AnimationType currentAnimation = currentAnimationState.animationType;
        AnimationType nextAnimation = currentAnimation;
        if(decidingFactors.grounded){
            // player on the ground
            if (decidingFactors.playerAction == PlayerAction.GO_LEFT || decidingFactors.playerAction == PlayerAction.GO_RIGHT){
                nextAnimation = AnimationType.WALKING;
            }
            else{
                nextAnimation = AnimationType.IDLE;
            }
//            if (decidingFactors.direction.x > 0.0001f || decidingFactors.direction.x < - 0.0001f){
//                nextAnimation = AnimationType.WALKING;
//            }
//            else {
//                nextAnimation = AnimationType.IDLE;
//            }
        }
        else {
            // player in the air
            if (decidingFactors.direction.y >= 0){
                //player rising
                nextAnimation = AnimationType.JUMPING;
            }
            else{
                // player falling
                nextAnimation = AnimationType.FALLING;
            }
        }
        if (nextAnimation == currentAnimation){
            currentAnimationState.durationInMilliseconds += deltaTimeInMilliseconds;
        }
        else{
            currentAnimationState.durationInMilliseconds = 0;
            currentAnimationState.animationType = nextAnimation;
        }
        //this.bodySegmentAnimations.put(BodySegmentType.LOWER_BODY, currentAnimationState );
    }

    private void updateUpperBodyState(DecidingFactorsForPlayer decidingFactors, float deltaTimeInMilliseconds){
        AnimationState currentAnimationState = bodySegmentAnimations.get(BodySegmentType.UPPER_BODY);
        AnimationType currentAnimation = currentAnimationState.animationType;
        AnimationType nextAnimation = currentAnimation;
        if(decidingFactors.grounded){
            // player on the ground
            if (decidingFactors.playerAction == PlayerAction.GO_LEFT || decidingFactors.playerAction == PlayerAction.GO_RIGHT){
                nextAnimation = AnimationType.WALKING;
            }
            else{
                nextAnimation = AnimationType.IDLE;
            }
//            if (decidingFactors.direction.x > 0.0001f || decidingFactors.direction.x < - 0.0001f){
//                nextAnimation = AnimationType.WALKING;
//            }
//            else {
//                nextAnimation = AnimationType.IDLE;
//            }
        }
        else {
            // player in the air
            if (decidingFactors.direction.y >= 0){
                //player rising
                nextAnimation = AnimationType.JUMPING;
            }
            else{
                // player falling
                nextAnimation = AnimationType.FALLING;
            }
        }
        if (nextAnimation == currentAnimation){
            currentAnimationState.durationInMilliseconds += deltaTimeInMilliseconds;
        }
        else{
            currentAnimationState.durationInMilliseconds = 0;
            currentAnimationState.animationType = nextAnimation;
        }
        //this.bodySegmentAnimations.put(BodySegmentType.UPPER_BODY, currentAnimationState );
    }

}
