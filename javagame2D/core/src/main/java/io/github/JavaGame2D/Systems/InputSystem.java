package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.JavaGame2D.Enums.PlayerAction;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerActionEvent;

import java.util.HashMap;

public class InputSystem {
    private boolean space_previously_pressed;

    private HashMap<Integer, PlayerAction> keyBindings;

    public InputSystem(){
        keyBindings = new HashMap<>();
        keyBindings.put(Input.Keys.RIGHT, PlayerAction.GO_RIGHT);
        keyBindings.put(Input.Keys.LEFT, PlayerAction.GO_LEFT);
        keyBindings.put(Input.Keys.SPACE, PlayerAction.JUMP);
        keyBindings.put(Input.Keys.P, PlayerAction.CHANGE_LEVEL);
        space_previously_pressed = false;
    }

    public void update(float deltaTime){

        PlayerAction horizontal_movement = null;
        PlayerAction jump_action = null;

        // independent system - directional movement
        horizontal_movement = Gdx.input.isKeyPressed(Input.Keys.RIGHT) ? keyBindings.get(Input.Keys.RIGHT) : null;
        horizontal_movement = Gdx.input.isKeyPressed(Input.Keys.LEFT) ? keyBindings.get(Input.Keys.LEFT) : horizontal_movement;

        // independent system - jumping
        boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if(spacePressed){
            if (!space_previously_pressed){
                space_previously_pressed = true;
                jump_action = keyBindings.get(Input.Keys.SPACE);
            }
            else{
                jump_action = PlayerAction.EXTEND_JUMP;
            }
        }
        else {
            space_previously_pressed = false;
        }
        // independent system - changing level
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            PlayerActionEvent playerAction = new PlayerActionEvent();
            playerAction.action = keyBindings.get(Input.Keys.P);
            EventBus.getInstance().publish(playerAction);
        }
        //find action bound to that key
        if (horizontal_movement != null){
            PlayerActionEvent playerAction = new PlayerActionEvent();
            playerAction.action = horizontal_movement;
            playerAction.deltaTime = deltaTime;
            EventBus.getInstance().publish(playerAction);
        }
        if (jump_action != null){
            PlayerActionEvent playerAction = new PlayerActionEvent();
            playerAction.action = jump_action;
            playerAction.deltaTime = deltaTime;
            EventBus.getInstance().publish(playerAction);
        }
    }
}
