package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ComponentType;

import java.util.ArrayList;
import java.util.HashMap;

public class EntityManager {
    private HashMap<Integer, Entity> entities;
    private int nextEntityId;

    public EntityManager(){
        entities = new HashMap<Integer, Entity>();
        nextEntityId = 0;
    }

    public Entity createEntity(){
        int id = nextEntityId;
        nextEntityId++;
        Entity newEntity = new Entity(id);
        entities.put(id,newEntity);
        return newEntity;
    }

    public Entity[] getMatchingEntities(ComponentType[] requiredComponents){
        ArrayList<Entity> matchingEntitiesFound = new ArrayList<Entity>();
        for(Entity entity : entities.values() ){
            boolean hasAllRequiredComponents = true;
            for(ComponentType componentType : requiredComponents ){
                if(!entity.hasComponent(componentType)){
                    hasAllRequiredComponents = false;
                }
            }
            if (hasAllRequiredComponents){
                matchingEntitiesFound.add(entity);
            }
        }
        Entity[] output = new Entity[matchingEntitiesFound.size()];
        matchingEntitiesFound.toArray(output);
        return output;
    }
}
