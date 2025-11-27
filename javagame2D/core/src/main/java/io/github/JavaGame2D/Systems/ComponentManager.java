package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;

import java.util.HashMap;

public class ComponentManager {
    private DrawableComponentsCollection drawableCollection;
    private TransformComponentsCollection transformCollection;
    private PhysicalBodyComponentsCollection physicalBodyCollection;
    private ColliderComponentCollection colliderCollection;
    private TeleporterComponentsCollection teleporterCollection;


    public ComponentManager() {
        this.drawableCollection = new DrawableComponentsCollection();
        this.transformCollection = new TransformComponentsCollection();
        this.physicalBodyCollection = new PhysicalBodyComponentsCollection();
        this.colliderCollection = new ColliderComponentCollection();
        this.teleporterCollection = new TeleporterComponentsCollection();
    }

    public TransformComponent getTransformComponent(int entityID){
        return transformCollection.getTransformComponent(entityID);
    }

    public PhysicalBodyComponent getPhysicalBodyComponent(int entityID){
        return  physicalBodyCollection.getPhysicalBodyComponent(entityID);
    }

    public ColliderComponent getColliderComponent(int entityID){
        return colliderCollection.getColliderComponent(entityID);
    }

    public DrawableComponent getDrawableComponent(int entityID){
        return drawableCollection.getDrawableComponent(entityID);
    }

    public TeleporterComponent getTeleporterComponent(int entityID) {
        return teleporterCollection.getTeleporterComponent(entityID);
    }

    public void addTransformComponent(TransformComponent transform, int entityID){
        transformCollection.addComponent(transform, entityID);
    }

    public void addBodyComponent(PhysicalBodyComponent body, int entityID){
        physicalBodyCollection.addComponent(body, entityID);
    }

    public void addColliderComponent(ColliderComponent collider, int entityID){
        colliderCollection.addComponent(collider, entityID);
    }

    public void addDrawableComponent(DrawableComponent drawable, int entityID){
        drawableCollection.addComponent(drawable, entityID);
    }

    public void addTeleporterComponent(TeleporterComponent teleporter, int entityID) {
        teleporterCollection.addComponent(teleporter, entityID);
    }

    public DrawableComponent[] getAllDrawableComponents(){
        return drawableCollection.getDrawableComponents();
    }

    public TransformComponentsCollection getTransformCollection() { return transformCollection; }
    public PhysicalBodyComponentsCollection getPhysicalBodyCollection() { return physicalBodyCollection; }
    public ColliderComponentCollection getColliderCollection() { return colliderCollection; }
    public DrawableComponentsCollection getDrawableCollection() { return drawableCollection; }
    public TeleporterComponentsCollection getTeleporterCollection() { return teleporterCollection; }

    public void setTransformCollection(TransformComponentsCollection transformCollection) {
        this.transformCollection = transformCollection;
    }

    public void setPhysicalBodyCollection(PhysicalBodyComponentsCollection bodyCollection) {
        this.physicalBodyCollection = bodyCollection;
    }

    public void setColliderCollection(ColliderComponentCollection colliderCollection) {
        this.colliderCollection = colliderCollection;
    }

    public void setDrawableCollection(DrawableComponentsCollection drawableCollection) {
        this.drawableCollection = drawableCollection;
    }

    public void setTeleporterCollection(TeleporterComponentsCollection teleporterCollection) {
        this.teleporterCollection = teleporterCollection;
    }

    public HashMap<Integer, Long> loadEntitiesFromCollections() {
        HashMap<Integer, Long> entityIDToSignature = new HashMap<>();
//        for (ComponentCollection collection: collections){
//            entityIDToSignature = collection.loadSignatures(entityIDToSignature);
//        }
        entityIDToSignature = transformCollection.updateEntitySignature(entityIDToSignature);
        entityIDToSignature = drawableCollection.updateEntitySignature(entityIDToSignature);
        entityIDToSignature = colliderCollection.updateEntitySignature(entityIDToSignature);
        entityIDToSignature = physicalBodyCollection.updateEntitySignature(entityIDToSignature);
        entityIDToSignature = teleporterCollection.updateEntitySignature(entityIDToSignature);
        return entityIDToSignature;
    }

}
