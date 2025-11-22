package io.github.JavaGame2D.Collections;


import io.github.JavaGame2D.Components.TransformComponent;

import java.util.Arrays;
import java.util.HashMap;

public class TransformComponentsCollection {
    HashMap<Integer, Integer> entityIDToPosition;
    HashMap<Integer, Integer> positionToEntityID;
    int lastLivePosition;
    int defaultSize = 10;
    TransformComponent[] transformComponents;

    public TransformComponentsCollection(){
        transformComponents = new TransformComponent[defaultSize];
        lastLivePosition = -1;
    }

    public TransformComponent getTransformComponent(int entityID){
        int position = entityIDToPosition.get(entityID);
        return transformComponents[position];
    }

    public TransformComponent[] getTransformComponents(){
        if (lastLivePosition < 0){
            return new TransformComponent[]{};
        }
        return Arrays.copyOfRange(transformComponents, 0, lastLivePosition);
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
        transformComponents[vacantPosition] = transformComponents[lastLivePosition];

        // 3) update mappings for entityIDtoMove
        positionToEntityID.put(vacantPosition,entityIDtoMove);
        entityIDToPosition.put(entityIDtoMove,vacantPosition);

        // *3.5) remove remaining mapping at last live position
        positionToEntityID.remove(lastLivePosition);

        // 4) move last live position back by 1
        lastLivePosition -= 1;
    }

    public void addComponent(TransformComponent component, int entityID){
        // if entity already has TransformComponent, first delete the old one
        if(entityIDToPosition.containsKey(entityID)){
            removeComponentFromEntity(entityID);
        }
        // 1) resize physicsComponents if necessary
        if(lastLivePosition == (transformComponents.length-1) ){
            enlargeComponentArray();
        }
        // 2) add new component
        lastLivePosition += 1;
        transformComponents[lastLivePosition] = component;
        entityIDToPosition.put(entityID, lastLivePosition);
        positionToEntityID.put(lastLivePosition, entityID);
    }

    private void enlargeComponentArray(){
        int newSize = transformComponents.length + 10;
        TransformComponent[] newTransformComponents =  new TransformComponent[newSize];
        for (int i = 0; i < transformComponents.length; i++){
            newTransformComponents[i] = transformComponents[i];
        }
        transformComponents = newTransformComponents;
    }
}
