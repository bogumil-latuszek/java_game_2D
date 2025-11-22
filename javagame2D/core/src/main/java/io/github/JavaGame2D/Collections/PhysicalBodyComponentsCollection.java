package io.github.JavaGame2D.Collections;


import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;

import java.util.Arrays;
import java.util.HashMap;

public class PhysicalBodyComponentsCollection{
    HashMap<Integer, Integer> entityIDToPosition;
    HashMap<Integer, Integer> positionToEntityID;
    int lastLivePosition;
    int defaultSize = 10;
    PhysicalBodyComponent[] physicsComponents;

    public PhysicalBodyComponentsCollection(){
        physicsComponents = new PhysicalBodyComponent[defaultSize];
        lastLivePosition = -1;
    }

    public PhysicalBodyComponent getPhysicalBodyComponent(int entityID){
        int position = entityIDToPosition.get(entityID);
        return physicsComponents[position];
    }


    public PhysicalBodyComponent[] getPhysicalBodyComponents(){
        if (lastLivePosition < 0){
            return new PhysicalBodyComponent[]{};
        }
        return Arrays.copyOfRange(physicsComponents, 0, lastLivePosition);
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
        physicsComponents[vacantPosition] = physicsComponents[lastLivePosition];

        // 3) update mappings for entityIDtoMove
        positionToEntityID.put(vacantPosition,entityIDtoMove);
        entityIDToPosition.put(entityIDtoMove,vacantPosition);

        // *3.5) remove remaining mapping at last live position
        positionToEntityID.remove(lastLivePosition);

        // 4) move last live position back by 1
        lastLivePosition -= 1;
    }

    public void addComponent(PhysicalBodyComponent component, int entityID){
        // if entity already has PhysicalBodyComponent, first delete the old one
        if(entityIDToPosition.containsKey(entityID)){
            removeComponentFromEntity(entityID);
        }
        // 1) resize physicsComponents if necessary
        if(lastLivePosition == (physicsComponents.length-1) ){
            enlargeComponentArray();
        }
        // 2) add new component
        lastLivePosition += 1;
        physicsComponents[lastLivePosition] = component;
        entityIDToPosition.put(entityID, lastLivePosition);
        positionToEntityID.put(lastLivePosition, entityID);
    }

    private void enlargeComponentArray(){
        int newSize = physicsComponents.length + 10;
        PhysicalBodyComponent[] newPhysicalBodyComponents =  new PhysicalBodyComponent[newSize];
        for (int i = 0; i < physicsComponents.length; i++){
            newPhysicalBodyComponents[i] = physicsComponents[i];
        }
        physicsComponents = newPhysicalBodyComponents;
    }
}
