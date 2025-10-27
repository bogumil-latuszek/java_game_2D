package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ComponentType;
import io.github.JavaGame2D.EventBus;

public class PlayerCharacterController {
    private Entity playerCharacter;

    public PlayerCharacterController(){
        EventBus.getInstance().subscribe(InputSystem.PlayerAction.class, this::handlePlayerAction);
    }

    public void setPlayerCharacter(Entity newPlayerCharacter){
        this.playerCharacter = newPlayerCharacter;
    }

    public void handlePlayerAction(InputSystem.PlayerAction event){
        if (playerCharacter == null){
            return;
        }
        PhysicalBodyComponent body = (PhysicalBodyComponent) playerCharacter.getComponent(ComponentType.PHYSICAL_BODY);
        switch (event.action){
            case GO_LEFT:
                body.velocity.x = -body.moveSpeed;
                break;
            case GO_RIGHT:
                body.velocity.x = body.moveSpeed;
                break;
            case JUMP:
                if (body.onGround){
                    body.onGround = false;
                    body.velocity.y += 50;
                }
        }
    }
}
