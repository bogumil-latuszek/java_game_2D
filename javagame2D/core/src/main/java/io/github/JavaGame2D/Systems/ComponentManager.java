package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Collections.ColliderComponentCollection;
import io.github.JavaGame2D.Collections.DrawableComponentsCollection;
import io.github.JavaGame2D.Collections.PhysicalBodyComponentsCollection;
import io.github.JavaGame2D.Collections.TransformComponentsCollection;
import io.github.JavaGame2D.Components.ColliderComponent;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;

public class ComponentManager {
    private DrawableComponentsCollection drawableCollection;
    private TransformComponentsCollection transformCollection;
    private PhysicalBodyComponentsCollection physicalBodyCollection;
    private ColliderComponentCollection colliderComponentCollection;


    public ComponentManager() {
        this.drawableCollection = new DrawableComponentsCollection();
        this.transformCollection = new TransformComponentsCollection();
        this.physicalBodyCollection = new PhysicalBodyComponentsCollection();
        this.colliderComponentCollection = new ColliderComponentCollection();
    }

    public TransformComponent getTransformComponent(int entityID){
        return transformCollection.getTransformComponent(entityID);
    }

    public PhysicalBodyComponent getPhysicalBodyComponent(int entityID){
        return  physicalBodyCollection.getPhysicalBodyComponent(entityID);
    }

    public ColliderComponent getColliderComponent(int entityID){
        return colliderComponentCollection.getColliderComponent(entityID);
    }

    public DrawableComponent getDrawableComponent(int entityID){
        return drawableCollection.getDrawableComponent(entityID);
    }

    public void addTransformComponent(TransformComponent transform, int entityID){
        transformCollection.addComponent(transform, entityID);
    }

    public void addBodyComponent(PhysicalBodyComponent body, int entityID){
        physicalBodyCollection.addComponent(body, entityID);
    }

    public void addColliderComponent(ColliderComponent collider, int entityID){
        colliderComponentCollection.addComponent(collider, entityID);
    }

    public void addDrawableComponent(DrawableComponent drawable, int entityID){
        drawableCollection.addComponent(drawable, entityID);
    }

    public DrawableComponent[] getAllDrawableComponents(){
        return drawableCollection.getDrawableComponents();
    }


}
