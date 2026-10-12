package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.DestructibleComponent;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;

import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Enums.PlayerAction;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.*;

public class PlayerCharacterController {
    private boolean playerNotSpecified = true;
    private int playerEntityID;
    private float jumpTimer = 0;
    private float maxJumpDuration = 0.22f;
    private boolean canExtendJump;
    EntityComponentManager ecm;
    //public PlayerAction currentPlayerAction = PlayerAction.NONE;
    public PlayerAction horizontalMovement = PlayerAction.NONE;
    public PlayerAction verticalMovement = PlayerAction.NONE;
    public PlayerAction specialAction = PlayerAction.NONE;
    private int playerActionEventsSinceLastUpdate = 0;

    public PlayerCharacterController(EntityComponentManager ecm){
        this.ecm = ecm;
        EventBus.getInstance().subscribe(PlayerActionEvent.class, this::handlePlayerAction);
        EventBus.getInstance().subscribe(PlayerIDChanged.class, this::handlePlayerIDChanged);
        EventBus.getInstance().subscribe(MovePlayerEvent.class, this::handleMovePlayerEvent);
        this.jumpTimer = 0;
    }

    private void handleMovePlayerEvent(MovePlayerEvent event){
        int targetEntityID = event.targetEntityID;
        try {
            // move player to target entity
            TransformComponent targetTransform = ecm.getComponent(TransformComponent.class, targetEntityID);
            TransformComponent playerTransform = ecm.getComponent(TransformComponent.class, this.playerEntityID);
            playerTransform.position = targetTransform.position.cpy();

            // make sure the player doesn't hover in the center of the door
            PhysicalBodyComponent playerBody = ecm.getComponent(PhysicalBodyComponent.class, this.playerEntityID);
            playerBody.onGround = false;
        }
        catch (RuntimeException e) {
        }
    }

    public void handlePlayerIDChanged(PlayerIDChanged event){
        this.playerEntityID = event.playerEntityID;
        this.playerNotSpecified = false;
    }

    public void update(float deltaTimeInSeconds){
        if (isPlayerDead()){
            PlayerIsDeadEvent playerIsDeadEvent = new PlayerIsDeadEvent();
            EventBus.getInstance().publish(playerIsDeadEvent);
        }
        else{
            updatePlayerMovement(deltaTimeInSeconds);
        }
    }

    public void handlePlayerAction(PlayerActionEvent event){
        if (playerNotSpecified){
            return;
        }
        this.playerActionEventsSinceLastUpdate += 1;
        this.horizontalMovement = event.horizontalMovementAction;
        this.verticalMovement = event.verticalMovementAction;
        this.specialAction = event.specialAction;
    }

    private boolean isPlayerDead(){
        DestructibleComponent destructible = ecm.getComponent(DestructibleComponent.class, playerEntityID);
        return !destructible.isAlive;
    }

    private void updatePlayerMovement(float deltaTimeInSeconds){
        PhysicalBodyComponent body = ecm.getComponent(PhysicalBodyComponent.class, playerEntityID);
        if (playerActionEventsSinceLastUpdate <= 0){
            //currentPlayerAction = PlayerAction.NONE;
            horizontalMovement = PlayerAction.NONE;
            verticalMovement = PlayerAction.NONE;
            specialAction = PlayerAction.NONE;
        }
        switch (horizontalMovement) {
            case GO_LEFT:
                if (body.onGround) {
                    body.velocity.x = -body.moveSpeed * deltaTimeInSeconds;
                } else {
                    body.velocity.x = -(body.moveSpeed * deltaTimeInSeconds) / 1.0f;
                }
                break;
            case GO_RIGHT:
                if (body.onGround) {
                    body.velocity.x = body.moveSpeed * deltaTimeInSeconds;
                } else {
                    body.velocity.x = (body.moveSpeed * deltaTimeInSeconds) / 1.0f;
                }
                break;
        }
        switch (verticalMovement) {
            case JUMP:
                if (body.onGround) {
                    // initial burst
                    body.onGround = false;
                    jumpTimer = deltaTimeInSeconds;
                    //body.velocity.y += body.jumpForce*deltaTime/jumpTimer;
                    body.velocity.y += body.jumpForce / 5;
                    canExtendJump = true;
                }
                else{
                    // extend jump
                    if (canExtendJump){
                        jumpTimer += deltaTimeInSeconds;
                        if (jumpTimer >= maxJumpDuration){
                            jumpTimer = maxJumpDuration;
                            canExtendJump = false;
                        }
                        if (body.velocity.y > 0 && canExtendJump){
                            body.velocity.y += body.jumpForce * deltaTimeInSeconds;
                        }
                    }
                }
                break;
            default:
                jumpTimer = 0;
                canExtendJump = false;
        }
        playerActionEventsSinceLastUpdate = 0;
    }



}
