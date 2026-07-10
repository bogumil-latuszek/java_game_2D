package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.JavaGame2D.Enums.PlayerAction;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerActionEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class InputSystem {
    private boolean space_previously_pressed;
    // could be good idea to separate it further into KeyPressedBindings, and KeyReleasedBindings
    private HashMap<Integer, PlayerAction> keyToActionBindings;
    private HashMap<PlayerAction, Integer> actionToKeyBindings;

    public InputSystem(){
        keyToActionBindings = new HashMap<>();
        actionToKeyBindings = new HashMap<>();
        addBinding(Input.Keys.D, PlayerAction.GO_RIGHT);
        addBinding(Input.Keys.A, PlayerAction.GO_LEFT);
        addBinding(Input.Keys.SPACE, PlayerAction.JUMP);
        addBinding(Input.Keys.P, PlayerAction.CHANGE_LEVEL);
        addBinding(Input.Keys.E, PlayerAction.INTERACT);
        addBinding(Input.Keys.LEFT, PlayerAction.ATTACK);
        space_previously_pressed = false;
    }

    private void addBinding(int keyCode, PlayerAction action){
        this.actionToKeyBindings.put(action,keyCode);
        this.keyToActionBindings.put(keyCode,action);
    }

    private PlayerAction getExclusiveAction (PlayerAction[] exclusiveActions){
        ArrayList<PlayerAction> currentlyChosenExclusiveActions = new ArrayList<>();
        for (PlayerAction action: exclusiveActions){
            int keyCode = actionToKeyBindings.get(action);
            if( Gdx.input.isKeyPressed(keyCode) ){
                currentlyChosenExclusiveActions.add(action);
            }
        }
        if (currentlyChosenExclusiveActions.size() == 1){
            return currentlyChosenExclusiveActions.get(0);
        }
        // in case of no action or multiple conflicting actions, return none
        return PlayerAction.NONE;
    }

    public void update(){

        // the player can take many independent actions at once, but only one in each category
        PlayerAction horizontalMovementAction = getExclusiveAction(new PlayerAction[]{PlayerAction.GO_LEFT, PlayerAction.GO_RIGHT});
        PlayerAction verticalMovementAction = getExclusiveAction(new PlayerAction[]{PlayerAction.JUMP});
        PlayerAction specialAction = getExclusiveAction(new PlayerAction[]{PlayerAction.ATTACK, PlayerAction.INTERACT});


        // changing level
//        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
//            PlayerActionEvent playerAction = new PlayerActionEvent();
//            playerAction.action = keyBindings.get(Input.Keys.P);
//            EventBus.getInstance().publish(playerAction);
//        }
        PlayerActionEvent event = new PlayerActionEvent();
        event.horizontalMovementAction = horizontalMovementAction;
        event.verticalMovementAction = verticalMovementAction;
        event.specialAction = specialAction;
        EventBus.getInstance().publish(event);
    }

//    private ArrayList<Integer> getAllPressedKeys() {
//        ArrayList<Integer> pressedKeys = new ArrayList<>();
//        for (int keyCode : keyToActionBindings.keySet()) {
//            if (Gdx.input.isKeyPressed(keyCode)) {
//                pressedKeys.add(keyCode);
//            }
//        }
//        return pressedKeys;
//    }
//
//    private ArrayList<PlayerAction> getAllPlayerActions(ArrayList<Integer> keys) {
//        ArrayList<PlayerAction> playerActions = new ArrayList<>();
//        for (int keyCode : keys) {
//            PlayerAction playerAction = keyToActionBindings.get(keyCode);
//            playerActions.add(playerAction);
//        }
//        return playerActions;
//    }

}
