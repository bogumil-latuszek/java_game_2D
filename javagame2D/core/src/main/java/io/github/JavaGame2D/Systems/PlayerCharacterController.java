package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ComponentType;
import io.github.JavaGame2D.EventBus;

public class PlayerCharacterController {
    private Entity playerCharacter;
    private float jumpTimer;

    public PlayerCharacterController(){
        EventBus.getInstance().subscribe(InputSystem.PlayerAction.class, this::handlePlayerAction);
        this.jumpTimer = 0;
    }

    public void setPlayerCharacter(Entity newPlayerCharacter){
        this.playerCharacter = newPlayerCharacter;
    }

    public void handlePlayerAction(InputSystem.PlayerAction event){
        if (playerCharacter == null){
            return;
        }
        float deltaTime = event.deltaTime;
        PhysicalBodyComponent body = (PhysicalBodyComponent) playerCharacter.getComponent(ComponentType.PHYSICAL_BODY);

        switch (event.action){
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
        }
    }

}
