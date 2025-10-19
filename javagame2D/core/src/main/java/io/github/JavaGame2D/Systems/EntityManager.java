package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Entity;

import java.util.HashMap;

public class EntityManager {
    private HashMap<Integer, Entity> entities;
    private int nextEntityId;

    public EntityManager(){
        nextEntityId = 0;
    }

    public Entity createEntity(){
        int id = nextEntityId;
        nextEntityId++;
        Entity newEntity = new Entity(id);
        entities.put(id,newEntity);
        return newEntity;
    }
}
