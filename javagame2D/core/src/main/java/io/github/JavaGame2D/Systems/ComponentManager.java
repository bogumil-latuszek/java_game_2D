package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;

import java.util.HashMap;

public class ComponentManager {

    private final HashMap<Class<?>, Object> componentCollections;


    public ComponentManager() {
        this.componentCollections = new HashMap<Class<?>, Object>();
        registerComponentCollection(TransformComponent.class, new ComponentCollection<>(TransformComponent.class, ComponentSignatures.TRANSFORM));
        registerComponentCollection(DrawableComponent.class, new ComponentCollection<>(DrawableComponent.class, ComponentSignatures.DRAWABLE));
        registerComponentCollection(PhysicalBodyComponent.class, new ComponentCollection<>(PhysicalBodyComponent.class, ComponentSignatures.PHYSICAL_BODY));
        registerComponentCollection(ColliderComponent.class, new ComponentCollection<>(ColliderComponent.class, ComponentSignatures.COLLIDER));
        registerComponentCollection(TeleporterComponent.class, new ComponentCollection<>(TeleporterComponent.class, ComponentSignatures.TELEPORTER));
    }

    // Get a component collection by type (type-safe)
    @SuppressWarnings("unchecked")
    public <T> ComponentCollection<T> getComponentCollection(Class<T> componentType) {
        Object collection = componentCollections.get(componentType);
        if (collection == null) {
            throw new IllegalArgumentException("No collection registered for type: " + componentType.getSimpleName());
        }
        return (ComponentCollection<T>) collection;
    }

    public <T> void registerComponentCollection(Class<T> componentType, ComponentCollection<T> collection) {
        componentCollections.put(componentType, collection);
    }

    // Convenience methods that delegate to the specific collection
    public <T> T getComponent(Class<T> componentType, int entityID) {
        return getComponentCollection(componentType).getComponent(entityID);
    }

    public <T> T[] getAllComponents(Class<T> componentType) {
        return getComponentCollection(componentType).getAllComponents();
    }

    public <T> void addComponent(Class<T> componentType, T component, int entityID) {
        getComponentCollection(componentType).addComponent(component, entityID);
    }

    public <T> void removeComponentFromEntity(Class<T> componentType, int entityID) {
        getComponentCollection(componentType).removeComponentFromEntity(entityID);
    }

    // Check if a component type is registered
    public boolean hasComponentType(Class<?> componentType) {
        return componentCollections.containsKey(componentType);
    }

    // Get all registered component types
    public Class<?>[] getRegisteredComponentTypes() {
        return componentCollections.keySet().toArray(new Class<?>[0]);
    }

//    public HashMap<Integer, Long> loadEntitiesFromCollections() {
//        HashMap<Integer, Long> entityIDToSignature = new HashMap<>();
////        for (ComponentCollection collection: collections){
////            entityIDToSignature = collection.loadSignatures(entityIDToSignature);
////        }
//        entityIDToSignature = transformCollection.updateEntitySignature(entityIDToSignature);
//        entityIDToSignature = drawableCollection.updateEntitySignature(entityIDToSignature);
//        entityIDToSignature = colliderCollection.updateEntitySignature(entityIDToSignature);
//        entityIDToSignature = physicalBodyCollection.updateEntitySignature(entityIDToSignature);
//        entityIDToSignature = teleporterCollection.updateEntitySignature(entityIDToSignature);
//        return entityIDToSignature;
//    }

//    public TransformComponent getTransformComponent(int entityID){
//        return transformCollection.getTransformComponent(entityID);
//    }
//
//    public PhysicalBodyComponent getPhysicalBodyComponent(int entityID){
//        return  physicalBodyCollection.getPhysicalBodyComponent(entityID);
//    }
//
//    public ColliderComponent getColliderComponent(int entityID){
//        return colliderCollection.getColliderComponent(entityID);
//    }
//
//    public DrawableComponent getDrawableComponent(int entityID){
//        return drawableCollection.getDrawableComponent(entityID);
//    }
//
//    public TeleporterComponent getTeleporterComponent(int entityID) {
//        return teleporterCollection.getTeleporterComponent(entityID);
//    }
//
//    public void addTransformComponent(TransformComponent transform, int entityID){
//        transformCollection.addComponent(transform, entityID);
//    }
//
//    public void addBodyComponent(PhysicalBodyComponent body, int entityID){
//        physicalBodyCollection.addComponent(body, entityID);
//    }
//
//    public void addColliderComponent(ColliderComponent collider, int entityID){
//        colliderCollection.addComponent(collider, entityID);
//    }
//
//    public void addDrawableComponent(DrawableComponent drawable, int entityID){
//        drawableCollection.addComponent(drawable, entityID);
//    }
//
//    public void addTeleporterComponent(TeleporterComponent teleporter, int entityID) {
//        teleporterCollection.addComponent(teleporter, entityID);
//    }
//
//    public DrawableComponent[] getAllDrawableComponents(){
//        return drawableCollection.getDrawableComponents();
//    }
//
//    public TransformComponentsCollection getTransformCollection() { return transformCollection; }
//    public PhysicalBodyComponentsCollection getPhysicalBodyCollection() { return physicalBodyCollection; }
//    public ColliderComponentCollection getColliderCollection() { return colliderCollection; }
//    public DrawableComponentsCollection getDrawableCollection() { return drawableCollection; }
//    public TeleporterComponentsCollection getTeleporterCollection() { return teleporterCollection; }

//    public void setTransformCollection(TransformComponentsCollection transformCollection) {
//        this.transformCollection = transformCollection;
//    }
//
//    public void setPhysicalBodyCollection(PhysicalBodyComponentsCollection bodyCollection) {
//        this.physicalBodyCollection = bodyCollection;
//    }
//
//    public void setColliderCollection(ColliderComponentCollection colliderCollection) {
//        this.colliderCollection = colliderCollection;
//    }
//
//    public void setDrawableCollection(DrawableComponentsCollection drawableCollection) {
//        this.drawableCollection = drawableCollection;
//    }
//
//    public void setTeleporterCollection(TeleporterComponentsCollection teleporterCollection) {
//        this.teleporterCollection = teleporterCollection;
//    }

}
