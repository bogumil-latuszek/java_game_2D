package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Collections.DrawableComponentsCollection;
import io.github.JavaGame2D.Collections.PhysicalBodyComponentsCollection;
import io.github.JavaGame2D.Collections.TransformComponentsCollection;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.TransformComponent;

public class ComponentManager {
    private DrawableComponentsCollection drawableCollection;
    private TransformComponentsCollection transformCollection;
    private PhysicalBodyComponentsCollection physicalBodyCollection;


    public ComponentManager() {
        this.drawableCollection = new DrawableComponentsCollection();
        this.transformCollection = new TransformComponentsCollection();
        this.physicalBodyCollection = new PhysicalBodyComponentsCollection();
    }

    public TransformComponent getTransformComponent(int entityID){
        return transformCollection.getTransformComponent(entityID);
    }

    public void addTransformComponent(TransformComponent transform, int entityID){
        transformCollection.addComponent(transform, entityID);
    }

    public void addComponents(int entityID, int signature){
        // for each possible collection:
        // bool hasComponent = BitwiseAnd(collection.signature, signature)
        // if (hasComponent) => collection.add(new T component, entityID)
    }

    public DrawableComponent[] getDrawableComponents(){
        return  drawableCollection.getDrawableComponents();
    }

}
