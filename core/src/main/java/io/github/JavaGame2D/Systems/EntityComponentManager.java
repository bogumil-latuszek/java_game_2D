package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.SaveData.PrefabInstance;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerHpChanged;

import java.util.HashMap;
import java.util.Optional;

public class EntityComponentManager {
    private EntityManager entityManager;
    private ComponentManager componentManager;
    private int playerEntityID;

    public EntityComponentManager() {
        this.entityManager = new EntityManager();
        this.componentManager = new ComponentManager();
    }

    public <T> ComponentCollection<T> getComponentCollection(Class<T> componentType) {
        return  componentManager.getComponentCollection(componentType);
    }

    public <T> void registerComponentCollection(Class<T> componentType, ComponentCollection<T> collection) {
        componentManager.registerComponentCollection(componentType, collection);
    }

    public <T> T getComponent(Class<T> componentType, int entityID) {
        return componentManager.getComponent(componentType, entityID);
    }

    public HashMap<Class<?>, Object> getEntityComponents(int entityID) {
        return componentManager.getEntityComponents(entityID);
    }

    public <T> void addComponent(Class<T> componentType, T component, int entityID) {
        componentManager.addComponent(componentType, component, entityID);
        long signature = ComponentSignatures.get((Class<? extends Component>) componentType);
        entityManager.addSignature(entityID, signature);
    }

    public <T> void removeComponent(Class<T> componentType, int entityID) {
        getComponentCollection(componentType).removeComponentFromEntity(entityID);
        long signature = ComponentSignatures.get((Class<? extends Component>) componentType);
        entityManager.removeSignature(entityID, signature);
    }

    // Check if a component type is registered
//    public boolean hasComponentType(Class<?> componentType) {
//        return componentManager.hasComponentType(componentType);
//    }

    // Get all registered component types
    public Class<?>[] getRegisteredComponentTypes() {
        return componentManager.getRegisteredComponentTypes();
    }

    public int getPlayerEntityID(){
        return playerEntityID;
    }

    public Entity getEntity(int entityID){
        return entityManager.getEntity(entityID);
    }

    public void deleteEntity(int entityID) {
        componentManager.removeAllComponentsFromEntity(entityID);
        entityManager.removeEntity(entityID);
    }

    public int[] getEntitiesMatchingSignature(long signature){
        return entityManager.getEntitiesMatchingSignature(signature);
    }

    public boolean hasComponent(int entityID, Class<? extends Component> compClass){
        Entity entity = entityManager.getEntity(entityID);
        long entitySignature = entity.signature;
        long compSignature = ComponentSignatures.get(compClass);
        if ( (entitySignature & compSignature) == compSignature ){
            // entitySignature contains compSignature
            return true;
        }
        return false;
    }

    public int[] getAllEntities(){
        return entityManager.getEntitiesMatchingSignature(0);
    }

    public DrawableComponent[] getAllDrawableComponents(){
        return componentManager.getAllComponents(DrawableComponent.class);
    }

//    public int createEmptyEntity(){
//        return  entityManager.createEntity();
//    }

    public void createEmptyEntity(int entityID){
        entityManager.createEntity(entityID);
    }

    public int createEntity(Vector2 position){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent(position);
        this.addComponent(TransformComponent.class, transform, entityID);

        return entityID;
    }

    public int createPlayer(Vector2 position){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = position;
        this.addComponent(TransformComponent.class, transform, entityID);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = true;
        body.usesGravity = true;
        this.addComponent(PhysicalBodyComponent.class, body, entityID);

        DrawableComponent drawable = new DrawableComponent();
        this.addComponent(DrawableComponent.class, drawable, entityID);

        ColliderComponent collider = new ColliderComponent();
        collider.width = 0.6f;
        collider.height = 1.5f;
        this.addComponent(ColliderComponent.class, collider, entityID);

        DestructibleComponent destructible = new DestructibleComponent();
        destructible.destructionDelay = 10f;
        this.addComponent(DestructibleComponent.class, destructible, entityID);

        int maxHp = 100;
        HealthComponent health = new HealthComponent(maxHp);
        health.damageTriggersiframes = true;
        health.iframeDuration = 1f;
        this.addComponent(HealthComponent.class, health, entityID);

        EventBus.getInstance().publish(new PlayerHpChanged(maxHp,maxHp));

        AnimationComponent animationComponent = new AnimationComponent();
        this.addComponent(AnimationComponent.class, animationComponent, entityID);

        DamageEmitterComponent damageEmitter = new DamageEmitterComponent();
        damageEmitter.damageAmount = 1;
        this.addComponent(DamageEmitterComponent.class, damageEmitter, entityID );

        System.out.println("player created");
        this.playerEntityID = entityID;
        return this.playerEntityID;
    }

    public void loadEntitiesFromCollections() {
        // component collections store entityID-s as keys in mappings
        // so, using that info, add them to EntityManager and rebuild their signatures
        HashMap<Integer, Long> entities = componentManager.loadEntitiesFromCollections();
        entityManager.loadEntitiesFromHashMap(entities);
    }

}
