package io.github.JavaGame2D.Systems;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.Entity;

import java.util.HashMap;

public class ComponentManager {

    private final HashMap<Class<?>, ComponentCollectionInterface> componentCollections;


    public ComponentManager() {
        this.componentCollections = new HashMap<Class<?>, ComponentCollectionInterface>();
        registerComponentCollection(TransformComponent.class, new ComponentCollection<>(TransformComponent.class, ComponentSignatures.get(TransformComponent.class)));
        registerComponentCollection(DrawableComponent.class, new ComponentCollection<>(DrawableComponent.class, ComponentSignatures.get(DrawableComponent.class)));
        registerComponentCollection(PhysicalBodyComponent.class, new ComponentCollection<>(PhysicalBodyComponent.class, ComponentSignatures.get(PhysicalBodyComponent.class)));
        registerComponentCollection(ColliderComponent.class, new ComponentCollection<>(ColliderComponent.class, ComponentSignatures.get(ColliderComponent.class)));
        registerComponentCollection(TeleporterComponent.class, new ComponentCollection<>(TeleporterComponent.class, ComponentSignatures.get(TeleporterComponent.class)));
        registerComponentCollection(HealthComponent.class, new ComponentCollection<>(HealthComponent.class, ComponentSignatures.get(HealthComponent.class)));
        registerComponentCollection(DamageEmitterComponent.class, new ComponentCollection<>(DamageEmitterComponent.class, ComponentSignatures.get(DamageEmitterComponent.class)));
        registerComponentCollection(AnimationComponent.class, new ComponentCollection<>(AnimationComponent.class, ComponentSignatures.get(AnimationComponent.class)));
        registerComponentCollection(DestructibleComponent.class, new ComponentCollection<>(DestructibleComponent.class, ComponentSignatures.get(DestructibleComponent.class)));
        registerComponentCollection(PrefabComponent.class, new ComponentCollection<>(PrefabComponent.class, ComponentSignatures.get(PrefabComponent.class)));
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

    //@SuppressWarnings("unchecked")
    public HashMap<Class<?>, Object> getEntityComponents(int entityID) {
        HashMap<Class<?>, Object> entityComponents = new HashMap<>();
        for ( ComponentCollectionInterface  componentCollection : this.componentCollections.values()){
            if(componentCollection.hasEntityID(entityID) ){
                Object component = componentCollection.getComponent(entityID);
                Class<?> componentType = componentCollection.getComponentType();
                entityComponents.put(componentType, component);
            }
        }
        return entityComponents;
    }

    public <T> void addComponent(Class<T> componentType, T component, int entityID) {
        ComponentCollection<T> collection = getComponentCollection(componentType);
        collection.addComponent(component, entityID);
        //getComponentCollection(componentType).addComponent(component, entityID);
    }

    public <T> void removeComponentFromEntity(Class<T> componentType, int entityID) {
        getComponentCollection(componentType).removeComponentFromEntity(entityID);
    }

    public void removeAllComponentsFromEntity(int entityID) {
        for (ComponentCollectionInterface collection : this.componentCollections.values()){
            collection.removeComponentFromEntity(entityID);
        }
    }

    // Check if a component type is registered
    public boolean hasComponentType(Class<?> componentType) {
        return componentCollections.containsKey(componentType);
    }

    // Get all registered component types
    public Class<?>[] getRegisteredComponentTypes() {
        return componentCollections.keySet().toArray(new Class<?>[0]);
    }

    public HashMap<Integer, Long> loadEntitiesFromCollections() {
        HashMap<Integer, Long> entityIDToSignature = new HashMap<>();
//        for (ComponentCollection collection: collections){
//            entityIDToSignature = collection.loadSignatures(entityIDToSignature);
//        }
        for (ComponentCollectionInterface collection : this.componentCollections.values()){
            entityIDToSignature = collection.updateEntitySignature(entityIDToSignature);
        }

        return entityIDToSignature;
    }
}
