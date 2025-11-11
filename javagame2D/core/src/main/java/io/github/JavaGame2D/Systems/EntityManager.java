package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Entity;
import java.util.HashMap;
import java.util.function.Predicate;

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

    public void addEntity(Entity entity){
        int id = nextEntityId;
        nextEntityId++;
        entity.setID(id);
        entities.put(id,entity);
    }

    public Entity[] getEntitiesWith(Predicate<Entity> condition) {
        return entities.values().stream()
            .filter(condition)
            .toArray(Entity[]::new);
    }
}
