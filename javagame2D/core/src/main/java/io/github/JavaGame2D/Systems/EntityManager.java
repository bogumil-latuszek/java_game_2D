package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Entity;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.function.Predicate;

public class EntityManager {
    private HashMap<Integer, Entity> entities;
    private int nextEntityId;

    public EntityManager(){
        entities = new HashMap<Integer, Entity>();
        nextEntityId = 0;
    }

    public Entity getEntity(int entityID){
        return entities.get(entityID);
    }

    public int createEntity(){
        int id = nextEntityId;
        nextEntityId++;
        Entity newEntity = new Entity(id);
        entities.put(id,newEntity);
        return id;
    }

    public void addSignature(int entityID, long signature){
        Entity entity  = entities.get(entityID);
        entity.signature = entity.signature & signature;
    }

    public  int[] getEntitiesMatchingSignature(long signature) {
        return entities.values().stream()
            .filter(entity -> ((entity.signature & signature) == signature))  // Your condition here
            .mapToInt(obj -> obj.ID)      // Extract ID
            .toArray();                   // Convert to array
    }

//    public int addEntity(Entity entity){
//        int id = nextEntityId;
//        nextEntityId++;
//        entity.setID(id);
//        entities.put(id,entity);
//        return id;
//    }

//    public void removeEntity(int ID){
//        entities.remove(ID);
//    }

}
