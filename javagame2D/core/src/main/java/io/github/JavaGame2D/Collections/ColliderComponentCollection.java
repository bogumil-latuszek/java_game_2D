package io.github.JavaGame2D.Collections;


import io.github.JavaGame2D.Components.ColliderComponent;

import java.util.Arrays;
import java.util.HashMap;

public class ColliderComponentCollection {
    public HashMap<Integer, Integer> entityIDToPosition;
    public HashMap<Integer, Integer> positionToEntityID;
    public int lastLivePosition;
    public int defaultSize = 10;
    public ColliderComponent[] colliderComponents;

    public ColliderComponentCollection(){
        colliderComponents = new ColliderComponent[defaultSize];
        lastLivePosition = -1;
        entityIDToPosition = new HashMap<>();
        positionToEntityID = new HashMap<>();
    }

    public ColliderComponent getColliderComponent(int entityID){
        int position = entityIDToPosition.get(entityID);
        return colliderComponents[position];
    }

    public ColliderComponent[] getColliderComponents(){
        if (lastLivePosition < 0){
            return new ColliderComponent[]{};
        }
        return Arrays.copyOfRange(colliderComponents, 0, lastLivePosition+1);
    }

    public void removeComponentAtPosition(int position){
        if(!positionToEntityID.containsKey(position)){
            return;
        }
        int entityID = positionToEntityID.get(position);
        removeComponent(entityID, position);
    }

    public void removeComponentFromEntity(int entityID){
        if(!entityIDToPosition.containsKey(entityID)){
            return;
        }
        int position = entityIDToPosition.get(entityID);
        removeComponent(entityID, position);
    }

    private void removeComponent(int entityIDtoDelete, int vacantPosition){

        int entityIDtoMove = positionToEntityID.get(lastLivePosition);

        // 1) remove mapping for entityIDtoDelete
        entityIDToPosition.remove(entityIDtoDelete);

        // 2) move the component from last live position to vacant position
        colliderComponents[vacantPosition] = colliderComponents[lastLivePosition];

        // 3) update mappings for entityIDtoMove
        positionToEntityID.put(vacantPosition,entityIDtoMove);
        entityIDToPosition.put(entityIDtoMove,vacantPosition);

        // *3.5) remove remaining mapping at last live position
        positionToEntityID.remove(lastLivePosition);

        // 4) move last live position back by 1
        lastLivePosition -= 1;
    }

    public void addComponent(ColliderComponent component, int entityID){
        // if entity already has ColliderComponent, first delete the old one
        if(entityIDToPosition.containsKey(entityID)){
            removeComponentFromEntity(entityID);
        }
        // 1) resize physicsComponents if necessary
        if(lastLivePosition == (colliderComponents.length-1) ){
            enlargeComponentArray();
        }
        // 2) add new component
        lastLivePosition += 1;
        colliderComponents[lastLivePosition] = component;
        entityIDToPosition.put(entityID, lastLivePosition);
        positionToEntityID.put(lastLivePosition, entityID);
    }

    private void enlargeComponentArray(){
        int newSize = colliderComponents.length + 10;
        ColliderComponent[] newColliderComponents =  new ColliderComponent[newSize];
        for (int i = 0; i < colliderComponents.length; i++){
            newColliderComponents[i] = colliderComponents[i];
        }
        colliderComponents = newColliderComponents;
    }
}
