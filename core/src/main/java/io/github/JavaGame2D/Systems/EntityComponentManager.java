package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.SpriteData;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerHpChanged;

import java.util.HashMap;

public class EntityComponentManager {
    private EntityManager entityManager;
    private ComponentManager componentManager;
    private int playerEntityID;

    public EntityComponentManager() {
        this.entityManager = new EntityManager();
        this.componentManager = new ComponentManager();
    }

    //TODO: add function that returns all components for given Entity
    //public <T> Components

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
    }

    public <T> void removeComponentFromEntity(Class<T> componentType, int entityID) {
        getComponentCollection(componentType).removeComponentFromEntity(entityID);
    }

    // Check if a component type is registereda
    public boolean hasComponentType(Class<?> componentType) {
        return componentManager.hasComponentType(componentType);
    }

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

    public DrawableComponent[] getAllDrawableComponents(){
        return componentManager.getAllComponents(DrawableComponent.class);
    }

    public int createPlatform(float x, float y, float width, float height){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(x,y);
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        componentManager.addComponent(PhysicalBodyComponent.class, body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.spriteData.textureID = 0;
        drawable.spriteData.textureIsTiled = true;
        drawable.spriteData.width = width;
        drawable.spriteData.height = height;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        collider.width = width;
        collider.height = height;
        componentManager.addComponent(ColliderComponent.class, collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        return entityID;
    }

    public int createDestructibleCrate(float x, float y, float width, float height){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(x,y);
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        componentManager.addComponent(PhysicalBodyComponent.class, body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.spriteData.width = width;
        drawable.spriteData.height = height;
        drawable.spriteData.textureID = 7;
        drawable.spriteData.usesSizeFromTexture = false;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        collider.width = width;
        collider.height = height;
        componentManager.addComponent(ColliderComponent.class, collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        int maxHp = 1;
        HealthComponent health = new HealthComponent(maxHp);
        componentManager.addComponent(HealthComponent.class, health, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.HEALTH);

        return entityID;
    }

    public int createPlayer(Vector2 position){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = position;
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = true;
        body.usesGravity = true;
        componentManager.addComponent(PhysicalBodyComponent.class, body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.hasSegmentedBody = true;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        SegmentedDrawableComponent segmentedDrawable = new SegmentedDrawableComponent();
        segmentedDrawable.drawableSegments.put(BodySegmentType.UPPER_BODY,new SpriteData());
        componentManager.addComponent(SegmentedDrawableComponent.class, segmentedDrawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.SEGMENTED_DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        collider.width = 0.6f;
        collider.height = 1.5f;
        componentManager.addComponent(ColliderComponent.class, collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        DestructibleComponent destructible = new DestructibleComponent();
        destructible.destructionDelay = 10f;
        componentManager.addComponent(DestructibleComponent.class, destructible, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.DESTRUCTIBLE);

        int maxHp = 100;
        HealthComponent health = new HealthComponent(maxHp);
        health.damageTriggersiframes = true;
        health.iframeDuration = 1f;
        componentManager.addComponent(HealthComponent.class, health, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.HEALTH);

        EventBus.getInstance().publish(new PlayerHpChanged(maxHp,maxHp));

        AnimationComponent animationComponent = new AnimationComponent();
        componentManager.addComponent(AnimationComponent.class, animationComponent, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.ANIMATION);

        DamageEmitterComponent damageEmitter = new DamageEmitterComponent();
        damageEmitter.damageAmount = 1;
        componentManager.addComponent(DamageEmitterComponent.class, damageEmitter, entityID );
        entityManager.addSignature(entityID, ComponentSignatures.DAMAGE_EMITTER);

        System.out.println("player created");
        this.playerEntityID = entityID;
        return this.playerEntityID;
    }

    public int createTeleporter(float x, float y, float width, float height, int targetLevel){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(x,y);
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        componentManager.addComponent(PhysicalBodyComponent.class, body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.spriteData.width = width;
        drawable.spriteData.height = height;
        drawable.spriteData.textureID = 2;
        drawable.spriteData.usesSizeFromTexture = false;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        collider.width = width;
        collider.height = height;
        componentManager.addComponent(ColliderComponent.class, collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        TeleporterComponent teleporter = new TeleporterComponent();
        teleporter.targetLevelID = targetLevel;
        componentManager.addComponent(TeleporterComponent.class, teleporter, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.TELEPORTER);

        return entityID;
    }

    public int createSpikes(float x, float y, float width, float height){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(x,y);
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        body.ignoresPhysicalCollision = true;
        componentManager.addComponent(PhysicalBodyComponent.class, body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.spriteData.width = width;
        drawable.spriteData.height = height;
        drawable.spriteData.textureID = 4;
        drawable.spriteData.usesSizeFromTexture = false;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        collider.width = width*0.9f;
        collider.height = height*0.4f;
        collider.usesOffset = true;
        collider.offset = new Vector2(0,-0.3f);
        componentManager.addComponent(ColliderComponent.class, collider, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.COLLIDER);

        DamageEmitterComponent damageEmitter = new DamageEmitterComponent();
        damageEmitter.damageAmount = 20;
        componentManager.addComponent(DamageEmitterComponent.class, damageEmitter, entityID );
        entityManager.addSignature(entityID, ComponentSignatures.DAMAGE_EMITTER);

        return entityID;
    }

    public int createBackgroundElement(float x, float y, float width, float height){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(x,y);
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        DrawableComponent drawable = new DrawableComponent();
        drawable.spriteData.width = width;
        drawable.spriteData.height = height;
        drawable.spriteData.textureID = 3;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        return entityID;
    }

    public void loadEntitiesFromCollections() {
        // component collections store entityID-s as keys in mappings
        // so, using that info, add them to EntityManager and rebuild their signatures
        HashMap<Integer, Long> entities = componentManager.loadEntitiesFromCollections();
        entityManager.loadEntitiesFromHashMap(entities);
    }

}
