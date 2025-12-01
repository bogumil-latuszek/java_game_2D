package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.Entity;

import java.util.HashMap;

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

    public <T> T[] getAllComponents(Class<T> componentType) {
        return componentManager.getAllComponents(componentType);
    }

    public <T> void addComponent(Class<T> componentType, T component, int entityID) {
        componentManager.addComponent(componentType, component, entityID);
    }

    public <T> void removeComponentFromEntity(Class<T> componentType, int entityID) {
        getComponentCollection(componentType).removeComponentFromEntity(entityID);
    }

    // Check if a component type is registered
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
        transform.width = width;
        transform.height = height;
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        componentManager.addComponent(PhysicalBodyComponent.class, body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.textureID = 0;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        componentManager.addComponent(ColliderComponent.class, collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        return entityID;
    }

    public int createPlayer(Vector2 position){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = position;
        transform.width = 1f;
        transform.height = 1.7f;
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = true;
        body.usesGravity = true;
        componentManager.addComponent(PhysicalBodyComponent.class, body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.textureID = 1;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        collider.sizeFromTransform = false;
        collider.width = transform.width/2;
        collider.height = transform.height;
        componentManager.addComponent(ColliderComponent.class, collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        int maxHp = 100;
        HealthComponent health = new HealthComponent(maxHp);
        componentManager.addComponent(HealthComponent.class, health, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.HEALTH);

        System.out.println("player created");
        this.playerEntityID = entityID;
        return this.playerEntityID;
    }

    public int createTeleporter(float x, float y, float width, float height, int targetLevel){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(x,y);
        transform.width = width;
        transform.height = height;
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        componentManager.addComponent(PhysicalBodyComponent.class, body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.textureID = 2;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
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
        transform.width = width;
        transform.height = height;
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        componentManager.addComponent(PhysicalBodyComponent.class, body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.textureID = 4;
        componentManager.addComponent(DrawableComponent.class, drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        collider.sizeFromTransform = false;
        collider.width = width*0.9f;
        collider.height = height*0.4f;
        collider.usesOffset = true;
        collider.offset = new Vector2(0,-0.3f);
        componentManager.addComponent(ColliderComponent.class, collider, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.COLLIDER);

        DamageEmitterComponent damageEmitter = new DamageEmitterComponent();
        componentManager.addComponent(DamageEmitterComponent.class, damageEmitter, entityID );
        entityManager.addSignature(entityID, ComponentSignatures.DAMAGE_EMITTER);

        return entityID;
    }

    public int createBackgroundElement(float x, float y, float width, float height){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(x,y);
        transform.width = width;
        transform.height = height;
        componentManager.addComponent(TransformComponent.class, transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        DrawableComponent drawable = new DrawableComponent();
        drawable.textureID = 3;
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
