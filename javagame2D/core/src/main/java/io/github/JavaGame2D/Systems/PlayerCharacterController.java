package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;

import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.PlayerAction;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerActionEvent;
import io.github.JavaGame2D.Events.PlayerIDChanged;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;

public class PlayerCharacterController {
    private boolean playerNotSpecified = true;
    private int playerEntityID;
    private float jumpTimer;
    EntityComponentManager entityComponentManager;
    public PlayerAction currentPlayerAction = PlayerAction.NONE;
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
        PhysicalBodyComponent body = entityComponentManager.getComponent(PhysicalBodyComponent.class, playerEntityID);
        if (playerActionEventsSinceLastUpdate <= 0){
            currentPlayerAction = PlayerAction.NONE;
        }
        switch (currentPlayerAction) {
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
            case JUMP:
                if (body.onGround) {
                    body.onGround = false;
                    jumpTimer = deltaTimeInMilliseconds;
                    //body.velocity.y += body.jumpForce*deltaTime/jumpTimer;
                    body.velocity.y += body.jumpForce / 5;
                }
                break;
            case EXTEND_JUMP:
                if (!body.onGround && body.velocity.y > 0 && jumpTimer < 0.2f) {
                    jumpTimer += deltaTimeInMilliseconds;
                    //body.velocity.y += (body.jumpForce*deltaTime)/(jumpTimer*2);
                    body.velocity.y += (body.jumpForce * deltaTimeInMilliseconds);
                }
                break;
            case CHANGE_LEVEL:
                TeleportPlayerEvent teleportEvent = new TeleportPlayerEvent();
                teleportEvent.targetLevelID = 0;
                EventBus.getInstance().publish(teleportEvent);
                break;
        }
        playerActionEventsSinceLastUpdate = 0;
    }

    public void handlePlayerAction(PlayerActionEvent event){
        if (playerNotSpecified){
            return;
        }
        this.currentPlayerAction = event.action;
        this.playerActionEventsSinceLastUpdate += 1;
    }

}
