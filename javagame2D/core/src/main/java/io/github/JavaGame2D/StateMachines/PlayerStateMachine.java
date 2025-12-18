package io.github.JavaGame2D.StateMachines;

import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;
import io.github.JavaGame2D.Systems.EntityComponentManager;

import java.util.EnumMap;
import java.util.HashMap;

public class PlayerStateMachine {
    private DecidingFactorsForPlayer decidingFactors;
    private final EntityComponentManager entityComponentManager;
    public final CharacterType characterType = CharacterType.PLAYER;
    // UpperBodyState upperBodyState <= can be null
    // lowerBodyState <= can be null
    // fullBodyState <= can be null
    public EnumMap<BodySegmentType, AnimationType> bodySegmentAnimations;

    // StateMachine upperBodyStateMachine; = new StateMachine(UpperBodyState);
    public PlayerStateMachine(EntityComponentManager entityComponentManager){
        this.entityComponentManager = entityComponentManager;
        this.bodySegmentAnimations = new EnumMap<>(BodySegmentType.class);
        this.bodySegmentAnimations.put(BodySegmentType.UPPER_BODY, AnimationType.IDLE);
        this.bodySegmentAnimations.put(BodySegmentType.LOWER_BODY, AnimationType.IDLE);
    }

    public void update(){
        // 1. get player entity
        int playerID = entityComponentManager.getPlayerEntityID();
        // 2. we're always going to assume that player entity has certain components;

        TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class, playerID);
        PhysicalBodyComponent body = entityComponentManager.getComponent(PhysicalBodyComponent.class, playerID);

        decidingFactors.direction = body.velocity;
        decidingFactors.grounded = body.onGround;
        updateLowerBodyState(decidingFactors);
        updateUpperBodyState(decidingFactors);
        //updateReactionState(decidingFactors);
    }

    private void updateLowerBodyState(DecidingFactorsForPlayer decidingFactors){
        AnimationType currentState;
        if(decidingFactors.grounded){
            // player on the ground
            if (decidingFactors.direction.x > 0.0001f || decidingFactors.direction.x < - 0.0001f){
                currentState = AnimationType.WALKING;
            }
            else {
                currentState = AnimationType.IDLE;
            }
        }
        else {
            // player in the air
            if (decidingFactors.direction.y >= 0){
                //player rising
                currentState = AnimationType.JUMPING;
            }
            else{
                // player falling
                currentState = AnimationType.FALLING;
            }
        }
        this.bodySegmentAnimations.put(BodySegmentType.LOWER_BODY, currentState);
    }

    private void updateUpperBodyState(DecidingFactorsForPlayer decidingFactors){
        AnimationType currentState;
        if(decidingFactors.grounded){
            // player on the ground
            if (decidingFactors.direction.x > 0.0001f || decidingFactors.direction.x < - 0.0001f){
                currentState = AnimationType.WALKING;
            }
            else {
                currentState = AnimationType.IDLE;
            }
        }
        else {
            // player in the air
            if (decidingFactors.direction.y >= 0){
                //player rising
                currentState = AnimationType.JUMPING;
            }
            else{
                // player falling
                currentState = AnimationType.FALLING;
            }
        }
        this.bodySegmentAnimations.put(BodySegmentType.UPPER_BODY, currentState);
    }



}
