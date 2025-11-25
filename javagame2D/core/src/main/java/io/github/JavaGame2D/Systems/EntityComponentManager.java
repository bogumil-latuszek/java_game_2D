package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.Entity;

public class EntityComponentManager {
    private EntityManager entityManager;
    private ComponentManager componentManager;

    public EntityComponentManager() {
        this.entityManager = new EntityManager();
        this.componentManager = new ComponentManager();
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
        componentManager.addDrawableComponent(drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        componentManager.addColliderComponent(collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        return entityID;
    }

    public int createPlayer(){
        int entityID = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.position = new Vector2(10f, 20f);
        transform.width = 1f;
        transform.height = 1.7f;
        componentManager.addTransformComponent(transform, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.TRANSFORM);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = true;
        body.usesGravity = true;
        componentManager.addBodyComponent(body, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.PHYSICAL_BODY);

        DrawableComponent drawable = new DrawableComponent();
        drawable.texturePath = "playerSprite.png";
        componentManager.addDrawableComponent(drawable, entityID);
        entityManager.addSignature(entityID, ComponentSignatures.DRAWABLE);

        ColliderComponent collider = new ColliderComponent();
        collider.sizeFromTransform = false;
        collider.width = transform.width/2;
        collider.height = transform.height;
        componentManager.addColliderComponent(collider, entityID);
        entityManager.addSignature(entityID,ComponentSignatures.COLLIDER);

        System.out.println("player created");
        return entityID;
    }

}
