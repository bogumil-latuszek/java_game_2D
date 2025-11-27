package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.ColliderComponentCollection;
import io.github.JavaGame2D.Collections.DrawableComponentsCollection;
import io.github.JavaGame2D.Collections.PhysicalBodyComponentsCollection;
import io.github.JavaGame2D.Collections.TransformComponentsCollection;
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


    public int getPlayerEntityID(){
        return playerEntityID;
    }

    public Entity getEntity(int entityID){
        return entityManager.getEntity(entityID);
    }

    public int[] getEntitiesMatchingSignature(long signature){
        return entityManager.getEntitiesMatchingSignature(signature);
    }

    public TransformComponent getTransformComponent(int entityID){
        return componentManager.getTransformComponent(entityID);
    }

    public PhysicalBodyComponent getPhysicalBodyComponent(int entityID){
        return  componentManager.getPhysicalBodyComponent(entityID);
    }

    public ColliderComponent getColliderComponent(int entityID){
        return componentManager.getColliderComponent(entityID);
    }

    public DrawableComponent getDrawableComponent(int entityID){
        return componentManager.getDrawableComponent(entityID);
    }

    public TeleporterComponent getTeleporterComponent(int entityID) {
        return componentManager.getTeleporterComponent(entityID);
    }

    public DrawableComponent[] getAllDrawableComponents(){
        return componentManager.getAllDrawableComponents();
    }

    public int createPlatform(float x, float y, float width, float height){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(x,y);
        transform.width = width;
        transform.height = height;
        componentManager.addTransformComponent(transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        componentManager.addBodyComponent(body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.textureID = 0;
        componentManager.addDrawableComponent(drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        componentManager.addColliderComponent(collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        return entityID;
    }

    public int createPlayer(){
        int playerID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(10f, 20f);
        transform.width = 1f;
        transform.height = 1.7f;
        componentManager.addTransformComponent(transform, playerID);
        entityManager.addSignature(playerID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = true;
        body.usesGravity = true;
        componentManager.addBodyComponent(body, playerID);
        entityManager.addSignature(playerID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.textureID = 1;
        componentManager.addDrawableComponent(drawable, playerID);
        entityManager.addSignature(playerID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        collider.sizeFromTransform = false;
        collider.width = transform.width/2;
        collider.height = transform.height;
        componentManager.addColliderComponent(collider, playerID);
        entityManager.addSignature(playerID,ComponentSignatures.COLLIDER);

        System.out.println("player created");
        this.playerEntityID = playerID;
        return this.playerEntityID;
    }

    public int createTeleporter(float x, float y, float width, float height, int targetLevel){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(x,y);
        transform.width = width;
        transform.height = height;
        componentManager.addTransformComponent(transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        componentManager.addBodyComponent(body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.textureID = 2;
        componentManager.addDrawableComponent(drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        componentManager.addColliderComponent(collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        TeleporterComponent teleporter = new TeleporterComponent();
        teleporter.targetLevelID = targetLevel;
        componentManager.addTeleporterComponent(teleporter, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.TELEPORTER);

        return entityID;
    }

    public TransformComponentsCollection getTransformCollection() {
        return componentManager.getTransformCollection();
    }

    public PhysicalBodyComponentsCollection getPhysicalBodyCollection() {
        return componentManager.getPhysicalBodyCollection();
    }

    public ColliderComponentCollection getColliderCollection() {
        return componentManager.getColliderCollection();
    }

    public DrawableComponentsCollection getDrawableCollection() {
        return componentManager.getDrawableCollection();
    }

    public void loadEntitiesFromCollections() {
        // component collections store entityID-s as keys in mappings
        // so, using that info, add them to EntityManager and rebuild their signatures
        HashMap<Integer, Long> entities = componentManager.loadEntitiesFromCollections();
        entityManager.loadEntitiesFromHashMap(entities);
    }

    public void setTransformComponentCollection(TransformComponentsCollection transformCollection) {
        componentManager.setTransformCollection(transformCollection);
    }

    public void setPhysicalBodyComponentCollection(PhysicalBodyComponentsCollection bodyCollection) {
        componentManager.setPhysicalBodyCollection(bodyCollection);
    }

    public void setColliderComponentCollection(ColliderComponentCollection colliderCollection) {
        componentManager.setColliderCollection(colliderCollection);
    }

    public void setDrawableComponentCollection(DrawableComponentsCollection drawableCollection) {
        componentManager.setDrawableCollection(drawableCollection);
    }

}
