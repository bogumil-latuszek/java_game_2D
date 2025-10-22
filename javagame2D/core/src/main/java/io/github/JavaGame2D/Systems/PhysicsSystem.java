package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ComponentType;

public class PhysicsSystem {
    // TODO: load this from settings
    private float gravity;
    //TODO: dependency injection
    private EntityManager entityManager;

    public PhysicsSystem(EntityManager entityManager) {
        this.gravity = -9.8f; //acceleration: 9.8 m/s in -y direction
        this.entityManager = entityManager;
    }

    public void update(float deltaTime){
        // get all entities with PhysicalBodyComponent and TransformComponent
        ComponentType[] requiredComponents = {ComponentType.PHYSICAL_BODY,
                                              ComponentType.TRANSFORM};
        Entity[] physicalEntities = entityManager.getMatchingEntities(requiredComponents);
        // for each of them:
        for(Entity entity: physicalEntities){
            TransformComponent transformComponent = (TransformComponent) entity.getComponent(ComponentType.TRANSFORM);
            PhysicalBodyComponent physicalBodyComponent = (PhysicalBodyComponent) entity.getComponent(ComponentType.PHYSICAL_BODY);
            Vector2 position = transformComponent.position;
            Vector2 velocity = physicalBodyComponent.velocity;
            // apply gravity
            if (physicalBodyComponent.usesGravity){
                //assuming deltaTime is in seconds! also shouldn't it be capped? or is that irrelevant to gravity calc?
                velocity.y += gravity*deltaTime;
            }
            // move objects
            position.x += velocity.x * deltaTime;
            position.y += velocity.y * deltaTime;

            // detect collisions
            // resolve collisions

        }
    }
}
