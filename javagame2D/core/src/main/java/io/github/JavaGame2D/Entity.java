package io.github.JavaGame2D;

import io.github.JavaGame2D.Components.*;

public class Entity {
    private int ID;
    public TransformComponent transformComponent;
    public DrawableComponent drawableComponent;
    public PhysicalBodyComponent physicalBodyComponent;
    public ColliderComponent colliderComponent;

//    public boolean hasTransform() { return transform != null; }
//    public boolean hasDrawable() { return drawable != null; }
//    public boolean hasPhysicalBody() { return physicalBody != null; }
//    public boolean hasCollider() { return collider != null; }

    public Entity() {
    }

    // Since Entity will only ever be created by EntityManager
    // the ID will just get passed to it's constructor
    public Entity(int ID) {
        this.ID = ID;
    }

    // should only be used when deserializing
    public void setID(int ID){
        this.ID = ID;
    }
    public int getID(){
        return this.ID;
    }

}
