package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Components.DrawableComponent;

public class EntityComponentManager {
    private EntityManager entityManager;
    private ComponentManager componentManager;

    public int CreateEntity(int signature){
        int entityID = entityManager.createEntity();
        entityManager.addSignature(entityID, signature);
        componentManager.addComponents(entityID, signature); // this creates components with default values
        return entityID;
    }

    public DrawableComponent[] getDrawableComponents(){
        return  componentManager.getDrawableComponents();
    }


}
