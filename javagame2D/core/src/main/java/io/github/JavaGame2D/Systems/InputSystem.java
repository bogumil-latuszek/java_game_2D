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
        addBinding(Input.Keys.RIGHT, PlayerAction.GO_RIGHT);
        addBinding(Input.Keys.LEFT, PlayerAction.GO_LEFT);
        addBinding(Input.Keys.SPACE, PlayerAction.JUMP);
        addBinding(Input.Keys.P, PlayerAction.CHANGE_LEVEL);
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

    public void update(float deltaTime){

        // the player can take many independent actions at once, but only one in each category
        PlayerAction horizontalMovementAction = getExclusiveAction(new PlayerAction[]{PlayerAction.GO_LEFT, PlayerAction.GO_RIGHT});
        PlayerAction verticalMovementAction = getExclusiveAction(new PlayerAction[]{PlayerAction.JUMP});
        //PlayerAction specialAction = getExclusiveAction(new PlayerAction[]{PlayerAction.ATTACK, PlayerAction.INTERACT});

        // extend jumping
//        boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
//        if(spacePressed){
//            if (!space_previously_pressed){
//                space_previously_pressed = true;
//                jump_action = keyToActionBindings.get(Input.Keys.SPACE);
//            }
//            else{
//                jump_action = PlayerAction.EXTEND_JUMP;
//            }
//        }
//        else {
//            space_previously_pressed = false;
//        }
        // changing level
//        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
//            PlayerActionEvent playerAction = new PlayerActionEvent();
//            playerAction.action = keyBindings.get(Input.Keys.P);
//            EventBus.getInstance().publish(playerAction);
//        }
        PlayerActionEvent event = new PlayerActionEvent();
        event.horizontalMovementAction = horizontalMovementAction;
        event.verticalMovementAction = verticalMovementAction;
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
