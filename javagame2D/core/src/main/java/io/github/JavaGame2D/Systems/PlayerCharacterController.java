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
    public PlayerAction currentPlayerAction;

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

    public void handlePlayerAction(PlayerActionEvent event){
        if (playerNotSpecified){
            return;
        }
        float deltaTime = event.deltaTime;
        PhysicalBodyComponent body = entityComponentManager.getComponent(PhysicalBodyComponent.class, playerEntityID);

        currentPlayerAction = event.action;
        switch (currentPlayerAction){
            case GO_LEFT:
                if (body.onGround){
                    body.velocity.x = -body.moveSpeed*deltaTime;
                }
                else{
                    body.velocity.x = -(body.moveSpeed*deltaTime)/1.0f;
                }
                break;
            case GO_RIGHT:
                if (body.onGround){
                    body.velocity.x = body.moveSpeed*deltaTime;
                }
                else{
                    body.velocity.x = (body.moveSpeed*deltaTime)/1.0f;
                }
                break;
            case JUMP:
                if (body.onGround){
                    body.onGround = false;
                    jumpTimer = deltaTime;
                    //body.velocity.y += body.jumpForce*deltaTime/jumpTimer;
                    body.velocity.y += body.jumpForce/5;
                }
                break;
            case EXTEND_JUMP:
                if (!body.onGround && body.velocity.y > 0 && jumpTimer < 0.2f){
                    jumpTimer += deltaTime;
                    //body.velocity.y += (body.jumpForce*deltaTime)/(jumpTimer*2);
                    body.velocity.y += (body.jumpForce*deltaTime);
                }
                break;
            case CHANGE_LEVEL:
                TeleportPlayerEvent teleportEvent = new TeleportPlayerEvent();
                teleportEvent.targetLevelID = 0;
                EventBus.getInstance().publish(teleportEvent);
                break;
        }
    }

}
