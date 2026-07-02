package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Components.DestructibleComponent;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;

import io.github.JavaGame2D.Enums.PlayerAction;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerActionEvent;
import io.github.JavaGame2D.Events.PlayerIDChanged;
import io.github.JavaGame2D.Events.PlayerIsDeadEvent;

public class PlayerCharacterController {
    private boolean playerNotSpecified = true;
    private int playerEntityID;
    private float jumpTimer = 0;
    private float maxJumpDuration = 0.22f;
    private boolean canExtendJump;
    EntityComponentManager entityComponentManager;
    //public PlayerAction currentPlayerAction = PlayerAction.NONE;
    public PlayerAction horizontalMovement = PlayerAction.NONE;
    public PlayerAction verticalMovement = PlayerAction.NONE;
    public PlayerAction specialAction = PlayerAction.NONE;
    private int playerActionEventsSinceLastUpdate = 0;

    public PlayerCharacterController(EntityComponentManager entityComponentManager){
        this.entityComponentManager = entityComponentManager;
        EventBus.getInstance().subscribe(PlayerActionEvent.class, this::handlePlayerAction);
        EventBus.getInstance().subscribe(PlayerIDChanged.class, this::handlePlayerIDChanged);
        this.jumpTimer = 0;
    }

    public void handlePlayerIDChanged(PlayerIDChanged event){
        this.playerEntityID = event.playerEntityID;
        this.playerNotSpecified = false;
    }

    public void update(float deltaTimeInMilliseconds){
        if (isPlayerDead()){
            PlayerIsDeadEvent playerIsDeadEvent = new PlayerIsDeadEvent();
            EventBus.getInstance().publish(playerIsDeadEvent);
        }
        else{
            updatePlayerMovement(deltaTimeInMilliseconds);
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
        DestructibleComponent destructible = entityComponentManager.getComponent(DestructibleComponent.class, playerEntityID);
        return !destructible.isAlive;
    }

    private void updatePlayerMovement(float deltaTimeInMilliseconds){
        PhysicalBodyComponent body = entityComponentManager.getComponent(PhysicalBodyComponent.class, playerEntityID);
        if (playerActionEventsSinceLastUpdate <= 0){
            //currentPlayerAction = PlayerAction.NONE;
            horizontalMovement = PlayerAction.NONE;
            verticalMovement = PlayerAction.NONE;
            specialAction = PlayerAction.NONE;
        }
        switch (horizontalMovement) {
            case GO_LEFT:
                if (body.onGround) {
                    body.velocity.x = -body.moveSpeed * deltaTimeInMilliseconds;
                } else {
                    body.velocity.x = -(body.moveSpeed * deltaTimeInMilliseconds) / 1.0f;
                }
                break;
            case GO_RIGHT:
                if (body.onGround) {
                    body.velocity.x = body.moveSpeed * deltaTimeInMilliseconds;
                } else {
                    body.velocity.x = (body.moveSpeed * deltaTimeInMilliseconds) / 1.0f;
                }
                break;
        }
        switch (verticalMovement) {
            case JUMP:
                if (body.onGround) {
                    // initial burst
                    body.onGround = false;
                    jumpTimer = deltaTimeInMilliseconds;
                    //body.velocity.y += body.jumpForce*deltaTime/jumpTimer;
                    body.velocity.y += body.jumpForce / 5;
                    canExtendJump = true;
                }
                else{
                    // extend jump
                    if (canExtendJump){
                        jumpTimer += deltaTimeInMilliseconds;
                        if (jumpTimer >= maxJumpDuration){
                            jumpTimer = maxJumpDuration;
                            canExtendJump = false;
                        }
                        if (body.velocity.y > 0 && canExtendJump){
                            body.velocity.y += body.jumpForce * deltaTimeInMilliseconds;
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
