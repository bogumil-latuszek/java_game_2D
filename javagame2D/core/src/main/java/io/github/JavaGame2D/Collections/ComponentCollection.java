package io.github.JavaGame2D.Collections;

import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.TransformComponent;

import java.util.Arrays;
import java.lang.reflect.Array;
import java.util.HashMap;


public class ComponentCollection<ComponentType> implements ComponentCollectionInterface {
    public HashMap<Integer, Integer> entityIDToPosition;
    public HashMap<Integer, Integer> positionToEntityID;
    public int lastLivePosition;
    public int defaultSize = 10;
    public ComponentType[] collectionOfComponents;
    public Long componentSignature;
    private final Class<ComponentType> componentClass;

    @SuppressWarnings("unchecked")
    private ComponentCollection() {
        // Jackson will set these via setters or constructor
        this.componentClass = null;
        this.componentSignature = 0L;
        // Initialize arrays to avoid NPE, they'll be replaced during deserialization
        //this.collectionOfComponents = (ComponentType[]) new Object[0];
        this.collectionOfComponents = null;
        this.lastLivePosition = -1;
        this.entityIDToPosition = new HashMap<>();
        this.positionToEntityID = new HashMap<>();
    }

    public ComponentCollection(Class<ComponentType> componentClass, long signature){
        this.componentClass = componentClass;
        // Create a ComponentType[] array of size defaultSize using reflection
        @SuppressWarnings("unchecked")
        ComponentType[] tmp = (ComponentType[]) Array.newInstance(componentClass, defaultSize);
        this.collectionOfComponents = tmp;
        lastLivePosition = -1;
        entityIDToPosition = new HashMap<>();
        positionToEntityID = new HashMap<>();
        componentSignature = signature;
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

    public ComponentType getComponent(int entityID){
        int position = entityIDToPosition.get(entityID);
        return collectionOfComponents[position];
    }

    public ComponentType[] getAllComponents(){
        if (lastLivePosition < 0){
            @SuppressWarnings("unchecked")
            ComponentType[] empty = (ComponentType[]) Array.newInstance(this.componentClass, 0);
            return empty;
        }
        return Arrays.copyOfRange(collectionOfComponents, 0, lastLivePosition+1);
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
        collectionOfComponents[vacantPosition] = collectionOfComponents[lastLivePosition];

        // 3) update mappings for entityIDtoMove
        positionToEntityID.put(vacantPosition,entityIDtoMove);
        entityIDToPosition.put(entityIDtoMove,vacantPosition);

        // *3.5) remove remaining mapping at last live position
        positionToEntityID.remove(lastLivePosition);

        // 4) move last live position back by 1
        lastLivePosition -= 1;
    }

    public void addComponent(ComponentType component, int entityID){
        // if entity already has ComponentType, first delete the old one
        if(entityIDToPosition.containsKey(entityID)){
            removeComponentFromEntity(entityID);
        }
        // 1) resize physicsComponents if necessary
        if(lastLivePosition == (collectionOfComponents.length-1) ){
            enlargeComponentArray();
        }
        // 2) add new component
        lastLivePosition += 1;
        collectionOfComponents[lastLivePosition] = component;
        entityIDToPosition.put(entityID, lastLivePosition);
        positionToEntityID.put(lastLivePosition, entityID);
    }

    private void enlargeComponentArray(){
        int newSize = collectionOfComponents.length + 10;
        @SuppressWarnings("unchecked")
        ComponentType[] newCollection = (ComponentType[]) Array.newInstance(this.componentClass, newSize);
        for (int i = 0; i < collectionOfComponents.length; i++){
            newCollection[i] = collectionOfComponents[i];
        }
        this.collectionOfComponents = newCollection;
    }
}
