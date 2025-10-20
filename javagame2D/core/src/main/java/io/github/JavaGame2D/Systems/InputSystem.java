package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.JavaGame2D.EventBus;

import java.util.HashMap;

public class InputSystem {
    public enum Action{
        GO_RIGHT,
        GO_LEFT
    }
    private HashMap<Integer, Action> keyBindings;

    public class PlayerAction{
        public Action action;
    }

    public InputSystem(){
        keyBindings = new HashMap<>();
        keyBindings.put(Input.Keys.RIGHT, Action.GO_RIGHT);
        keyBindings.put(Input.Keys.LEFT, Action.GO_LEFT);
    }

    public void update(){
        //get pressed key
        Action action = null;
        action = Gdx.input.isKeyPressed(Input.Keys.RIGHT) ? keyBindings.get(Input.Keys.RIGHT) : null;
        action = Gdx.input.isKeyPressed(Input.Keys.LEFT) ? keyBindings.get(Input.Keys.LEFT) : action;
        //find action bound to that key
        if (action != null){
            PlayerAction playerAction = new PlayerAction();
            playerAction.action = action;
            EventBus.getInstance().publish(playerAction);
        }
        //execute action
    }
}
