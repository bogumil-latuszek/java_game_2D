package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
//import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import io.github.JavaGame2D.Components.ColliderComponent;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ColliderType;
import io.github.JavaGame2D.Enums.ComponentType;
//import jdk.javadoc.internal.doclets.toolkit.util.DocFinder;


public class PhysicsSystem {
    // TODO: load this from settings
    private float gravity;
    //TODO: dependency injection
    private EntityManager entityManager;
    private float groundCheckDepth;

    public PhysicsSystem(EntityManager entityManager) {
        this.gravity = -60f; //acceleration: unit/s in -y direction
        this.entityManager = entityManager;
        this.groundCheckDepth = 1f;
    }

    public void update(float deltaTime){
        moveEntities(deltaTime);
        detectAndResolveCollisions();
    }

    private void moveEntities(float deltaTime){
        // get all entities that can move: ones with PhysicalBodyComponent and TransformComponent
        ComponentType[] requiredComponents = {ComponentType.PHYSICAL_BODY,
                                              ComponentType.TRANSFORM};
        Entity[] physicalEntities = entityManager.getMatchingEntities(requiredComponents);
        // move them:
        for(Entity entity: physicalEntities){
            TransformComponent transformComponent = (TransformComponent) entity.getComponent(ComponentType.TRANSFORM);
            PhysicalBodyComponent physicalBodyComponent = (PhysicalBodyComponent) entity.getComponent(ComponentType.PHYSICAL_BODY);
            Vector2 position = transformComponent.position;
            Vector2 previousPosition = transformComponent.previousPosition;
            Vector2 velocity = physicalBodyComponent.velocity;
            if (physicalBodyComponent.usesGravity && physicalBodyComponent.onGround){
                //do the ground check:
                boolean stillOnGround = groundCheck(entity);
                if (!stillOnGround){
                    physicalBodyComponent.onGround = false;
                }
            }
            // apply gravity
            if (physicalBodyComponent.usesGravity && !physicalBodyComponent.onGround){
                //assuming deltaTime is in seconds! also shouldn't it be capped? or is that irrelevant to gravity calc?
                if (velocity.y < 1){
                    velocity.y += gravity*2*deltaTime;
                }
                else{
                    velocity.y += gravity*deltaTime;
                }
            }
            // save previous position
            previousPosition.x = position.x;
            previousPosition.y = position.y;
            // move object
            position.x += velocity.x * deltaTime;
            // reset player's horizontal velocity
            velocity.x = 0;
            position.y += velocity.y * deltaTime;
        }
    }

    private boolean groundCheck(Entity entity){
        boolean stillOnGround = true;
        ColliderComponent collider = (ColliderComponent) entity.getComponent(ComponentType.COLLIDER);

        if (collider.type == ColliderType.AABB){
            stillOnGround = groundCheckForAABB(entity);
        }
        return stillOnGround;
    }

    private boolean groundCheckForAABB(Entity entity){
        boolean groundCollisionDetected = true;
        boolean atLeastOneCollision = false;
        TransformComponent transform = (TransformComponent) entity.getComponent(ComponentType.TRANSFORM);
        ColliderComponent collider = (ColliderComponent) entity.getComponent(ComponentType.COLLIDER);
        AABBCollider transformedCollider = createAABBCollider(transform, collider);
        AABBCollider groundCollider = createAABBGroundCollider(transformedCollider);

        ComponentType[] requiredComponents = {ComponentType.TRANSFORM,
                                              ComponentType.PHYSICAL_BODY,
                                              ComponentType.COLLIDER};
        Entity[] potentialGround = entityManager.getMatchingEntities(requiredComponents);

        for (Entity otherEntity : potentialGround){
            // check if collision is possible
            // impossible for entity to collide with itself
            if (entity == otherEntity){
                continue;
            }
            ColliderComponent otherCollider = (ColliderComponent) otherEntity.getComponent(ComponentType.COLLIDER);
            // impossible for entities on different layers to collide
            if (collider.layer != otherCollider.layer){
                continue;
            }
            TransformComponent otherTransform = (TransformComponent) otherEntity.getComponent(ComponentType.TRANSFORM);
            if (otherCollider.type == ColliderType.AABB){
                AABBCollider otherTransformedCollider = createAABBCollider(otherTransform, otherCollider);
                boolean detectedCollision = detectAABBxAABBCollision(groundCollider, otherTransformedCollider);
                if(detectedCollision){
                    atLeastOneCollision = true;
                }
            }
        }
        if (!atLeastOneCollision){
            groundCollisionDetected = false;
        }
        return groundCollisionDetected;
    }

    private void detectAndResolveCollisions(){
        // get all entities with Transform Component and Collider Component
        ComponentType[] requiredComponents = {ComponentType.TRANSFORM,
                                              ComponentType.PHYSICAL_BODY,
                                              ComponentType.COLLIDER};
        Entity[] potentiallyColliding = entityManager.getMatchingEntities(requiredComponents);
        //ArrayList<Entity> collidedEntities = new ArrayList<>();

        for (Entity entity: potentiallyColliding){
            PhysicalBodyComponent body = (PhysicalBodyComponent) entity.getComponent(ComponentType.PHYSICAL_BODY);
            // if it's not dynamic, it can't move by itself
            // therefore it didn't INITIATE any collisions
            // * what about collisions if it was pushed?
            if (!body.dynamic){
                continue;
            }
            for (Entity otherEntity : potentiallyColliding){
                // check if collision is possible
                // impossible for entity to collide with itself
                if (entity == otherEntity){
                    continue;
                }
                ColliderComponent collider1 = (ColliderComponent) entity.getComponent(ComponentType.COLLIDER);
                ColliderComponent collider2 = (ColliderComponent) otherEntity.getComponent(ComponentType.COLLIDER);
                // impossible for entities on different layers to collide
                if (collider1.layer != collider2.layer){
                    continue;
                }

                // check if objects are colliding
                boolean collisionDetected = detectCollision(entity, otherEntity);

                if (!collisionDetected){
                    continue;
                }
                // resolve collision
                // we know that entity is dynamic
                // now if other entity is static:
                PhysicalBodyComponent otherBody = (PhysicalBodyComponent) otherEntity.getComponent(ComponentType.PHYSICAL_BODY);
                if (otherBody.dynamic){
                    // resolve collision between 2 dynamic entities
                }
                else {
                    resolveDynamicxStaticAABBCollision(entity, otherEntity);
                }
                System.out.println("collision detected between" + entity.getID() + " and " + otherEntity.getID());
            }
        }

    }

    public boolean detectCollision(Entity a, Entity b){
        boolean collisionDetected = false;

        TransformComponent transformA = (TransformComponent) a.getComponent(ComponentType.TRANSFORM);
        ColliderComponent colliderA = (ColliderComponent) a.getComponent(ComponentType.COLLIDER);

        TransformComponent transformB = (TransformComponent) b.getComponent(ComponentType.TRANSFORM);
        ColliderComponent colliderB = (ColliderComponent) b.getComponent(ComponentType.COLLIDER);

        if (colliderA.type == ColliderType.AABB && colliderB.type == ColliderType.AABB){
            AABBCollider transformedColliderA = createAABBCollider(transformA, colliderA);
            AABBCollider transformedColliderB = createAABBCollider(transformB, colliderB);
            collisionDetected = detectAABBxAABBCollision(transformedColliderA, transformedColliderB);
        }
        return collisionDetected;
    }

    public void resolveDynamicxStaticAABBCollision(Entity dynamicEntity, Entity staticEntity){
        TransformComponent dTransform = (TransformComponent) dynamicEntity.getComponent(ComponentType.TRANSFORM);
        PhysicalBodyComponent dBody = (PhysicalBodyComponent) dynamicEntity.getComponent(ComponentType.PHYSICAL_BODY);
        ColliderComponent dCollider = (ColliderComponent) dynamicEntity.getComponent(ComponentType.COLLIDER);

        TransformComponent sTransform = (TransformComponent) staticEntity.getComponent(ComponentType.TRANSFORM);
        PhysicalBodyComponent sBody = (PhysicalBodyComponent) staticEntity.getComponent(ComponentType.PHYSICAL_BODY);
        ColliderComponent sCollider = (ColliderComponent) staticEntity.getComponent(ComponentType.COLLIDER);

        AABBCollider dTransformedCollider = createAABBCollider(dTransform, dCollider);
        AABBCollider dPreviousTransformedCollider = createAABBCollider(dTransform, dCollider, true);
        AABBCollider sTransformedCollider = createAABBCollider(sTransform, sCollider);

        //1) find overlap on previous frame:
        Vector2 dPreviousPosition = dPreviousTransformedCollider.position;
        Vector2 sPreviousPosition = sTransformedCollider.position;

        boolean previouslyOverlappedOnYAxis = false;

        float dPreviousMinY = dPreviousPosition.y - dPreviousTransformedCollider.height/2;
        float dPreviousMaxY = dPreviousPosition.y + dPreviousTransformedCollider.height/2;
        float sPreviousMinY = sPreviousPosition.y - sTransformedCollider.height/2;
        float sPreviousMaxY = sPreviousPosition.y + sTransformedCollider.height/2;

        if (sPreviousMaxY > dPreviousMinY && dPreviousMaxY > sPreviousMinY){
            previouslyOverlappedOnYAxis = true;
        }

        float pushOffset = 0.0001f;// used to push dynamic entity slightly further to reduce unnecessary collisions
        if (previouslyOverlappedOnYAxis){
            float xAxisOverlap = calculateOverlap(dTransformedCollider.position.x, dTransformedCollider.width,
                sTransformedCollider.position.x, sTransformedCollider.width);

            // update previous position to current?
            // dTransform.previousPosition.x =  dTransform.position.x;
            // dTransform.previousPosition.y = dTransform.position.y;

            int pushDirection = sPreviousPosition.x < dPreviousPosition.x ? 1 : -1;
            float xAxisPushLength = (xAxisOverlap + pushOffset)*pushDirection;
            dTransform.position.x += xAxisPushLength;
            System.out.println("pushing on x by "+xAxisPushLength);
            dBody.velocity.x = 0;
        }
        else{
            float yAxisOverlap = calculateOverlap(dTransformedCollider.position.y, dTransformedCollider.height,
                sTransformedCollider.position.y, sTransformedCollider.height);

            // update previous position to current?
            //  dTransform.previousPosition.x =  dTransform.position.x;
            //   dTransform.previousPosition.y = dTransform.position.y;

            int pushDirection = sPreviousPosition.y < dPreviousPosition.y ? 1 : -1;

            float yAxisPushLength = (yAxisOverlap + pushOffset)*pushDirection ;
            dTransform.position.y += yAxisPushLength;
            System.out.println("pushing on y by "+yAxisPushLength);

            if(pushDirection > 0){
                // pushed on top => stands on Ground
                dBody.onGround = true;
            }

            dBody.velocity.y = 0;
        }
    }

    public boolean detectAABBxAABBCollision(AABBCollider a, AABBCollider b){
        boolean overlapOnXAxis = detectOverlap(a.position.x, a.width, b.position.x, b.width);
        boolean overlapOnYAxis = detectOverlap(a.position.y, a.height, b.position.y, b.height);
        boolean detectedCollision = overlapOnXAxis && overlapOnYAxis;
        return detectedCollision;
    }
//    public boolean detectAABBxAABBCollision(Vector2 positionA, float widthA, float heightA,
//                                            Vector2 positionB, float widthB, float heightB){
//        boolean overlapOnXAxis = detectOverlap(positionA.x, widthA, positionB.x, widthB);
//        boolean overlapOnYAxis = detectOverlap(positionA.y, heightA, positionB.y, heightB);
//        boolean detectedCollision = overlapOnXAxis && overlapOnYAxis;
//        return detectedCollision;
//    }

    public boolean detectOverlap(float pointA, float rangeA, float pointB, float rangeB){
        boolean overlapDetected = false;
        float aMin = pointA - rangeA/2;
        float aMax = pointA + rangeA/2;
        float bMin = pointB - rangeB/2;
        float bMax = pointB + rangeB/2;
        if (aMax > bMin && bMax > aMin){
            overlapDetected = true;
        }
        return  overlapDetected;
    }

    public float calculateOverlap(float pointA, float rangeA, float pointB, float rangeB){
        // remember to only use this function if you know for sure that there IS an overlap
        // for lines that are apart from each other, the results will be wrong
        float aMin = pointA - rangeA/2;
        float aMax = pointA + rangeA/2;
        float bMin = pointB - rangeB/2;
        float bMax = pointB + rangeB/2;
        float min = Math.min(aMin,bMin);
        float max = Math.max(aMax,bMax);
        float maximumRange = rangeA + rangeB;
        float actualRange = max - min;
        float overlap = maximumRange - actualRange;
        return overlap;
    }

    //TODO: check why you can't use "record" instead. Is it because of Java versioning problem?
    //public record AABBCollider (Vector2 position, float width, float height) {}
    public class AABBCollider{
        Vector2 position;
        float width;
        float height;

        public AABBCollider(Vector2 position, float width, float height){
            this.position = position;
            this.width = width;
            this.height = height;
        }
    }

    public AABBCollider createAABBCollider(TransformComponent transform, ColliderComponent collider){
        return createAABBCollider(transform, collider, false);
    }

    public AABBCollider createAABBCollider(TransformComponent transform, ColliderComponent collider, boolean usePreviousPosition){
        Vector2 transformPosition = usePreviousPosition ? transform.previousPosition : transform.position;
        Vector2 position = collider.usesOffset ? transformPosition.add(collider.offset): transformPosition;
        float width = collider.sizeFromTransform ? transform.width : collider.width;
        float height = collider.sizeFromTransform ? transform.height : collider.height;
        return new AABBCollider(position, width, height);
    }

    public AABBCollider createAABBGroundCollider(AABBCollider collider){
        float width = collider.width;
        float height = groundCheckDepth;
        float positionX = collider.position.x;
        float positionY = collider.position.y - (collider.height+groundCheckDepth)/2;
        Vector2 position = new Vector2(positionX, positionY);
        return new AABBCollider(position, width, height);
    }
}
