package io.github.JavaGame2D.Collections;


import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.TeleporterComponent;

import java.util.Arrays;
import java.util.HashMap;

public class TeleporterComponentsCollection {
    public HashMap<Integer, Integer> entityIDToPosition;
    public HashMap<Integer, Integer> positionToEntityID;
    public int lastLivePosition;
    public int defaultSize = 10;
    public TeleporterComponent[] teleporterComponents;
    public Long componentSignature;

    public TeleporterComponentsCollection(){
        teleporterComponents = new TeleporterComponent[defaultSize];
        lastLivePosition = -1;
        entityIDToPosition = new HashMap<>();
        positionToEntityID = new HashMap<>();
        componentSignature = ComponentSignatures.TELEPORTER;
    }

    public HashMap<Integer, Long> updateEntitySignature(HashMap<Integer,Long> entityIDtoSignature){
        for (Integer key : entityIDToPosition.keySet()){
            if (entityIDtoSignature.containsKey(key)){
                Long existingSignature = entityIDtoSignature.get(key);
                Long combinedSignature = existingSignature | componentSignature;
                entityIDtoSignature.put(key,combinedSignature);
            }
            else{
                entityIDtoSignature.put(key, componentSignature);
            }
        }
        return entityIDtoSignature;
    }

    public TeleporterComponent getTeleporterComponent(int entityID){
        int position = entityIDToPosition.get(entityID);
        return teleporterComponents[position];
    }

    public TeleporterComponent[] getTeleporterComponents(){
        if (lastLivePosition < 0){
            return new TeleporterComponent[]{};
        }
        return Arrays.copyOfRange(teleporterComponents, 0, lastLivePosition+1);
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
        teleporterComponents[vacantPosition] = teleporterComponents[lastLivePosition];

        // 3) update mappings for entityIDtoMove
        positionToEntityID.put(vacantPosition,entityIDtoMove);
        entityIDToPosition.put(entityIDtoMove,vacantPosition);

        // *3.5) remove remaining mapping at last live position
        positionToEntityID.remove(lastLivePosition);

        // 4) move last live position back by 1
        lastLivePosition -= 1;
    }

    public void addComponent(TeleporterComponent component, int entityID){
        // if entity already has TeleporterComponent, first delete the old one
        if(entityIDToPosition.containsKey(entityID)){
            removeComponentFromEntity(entityID);
        }
        // 1) resize physicsComponents if necessary
        if(lastLivePosition == (teleporterComponents.length-1) ){
            enlargeComponentArray();
        }
        // 2) add new component
        lastLivePosition += 1;
        teleporterComponents[lastLivePosition] = component;
        entityIDToPosition.put(entityID, lastLivePosition);
        positionToEntityID.put(lastLivePosition, entityID);
    }

    private void enlargeComponentArray(){
        int newSize = teleporterComponents.length + 10;
        TeleporterComponent[] newTeleporterComponents =  new TeleporterComponent[newSize];
        for (int i = 0; i < teleporterComponents.length; i++){
            newTeleporterComponents[i] = teleporterComponents[i];
        }
        teleporterComponents = newTeleporterComponents;
    }
}
