package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
//import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import io.github.JavaGame2D.Components.ColliderComponent;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ColliderType;
import io.github.JavaGame2D.Enums.ComponentType;
import io.github.JavaGame2D.GameSettings;
//import jdk.javadoc.internal.doclets.toolkit.util.DocFinder;


public class PhysicsSystem {
    // TODO: load this from settings
    private float gravity; //acceleration: unit/s in y direction
    //TODO: dependency injection
    private EntityComponentManager entityComponentManager;
    private float groundCheckDepth;

    public PhysicsSystem(EntityComponentManager entityComponentManager, GameSettings settings) {
        this.entityComponentManager = entityComponentManager;
        this.gravity = settings.gravity;
        this.groundCheckDepth = settings.groundCheckDepth;
    }

    public void update(float deltaTime){
        // #1 do the GroundCheck
        // #2 move entites
        moveEntities(deltaTime);
        // #3 detect collisions
        // #4 send list of collided items to systems like:
        // PhysicsSystem.resolveCollisions(list);
        // TrapSystem.resolveTraps(list)
        // TeleportSystem.resolveTeleports(list);
        detectAndResolveCollisions();
    }

    private void moveEntities(float deltaTime){

        long signature = ComponentSignatures.PHYSICAL_BODY & ComponentSignatures.TRANSFORM;
        int[] physicalEntities = entityComponentManager.getEntitiesMatchingSignature(signature);

        // move them:
        for(int entityID: physicalEntities){
            TransformComponent transform = entityComponentManager.getTransformComponent(entityID);
            PhysicalBodyComponent body = entityComponentManager.getPhysicalBodyComponent(entityID);
            Vector2 position = transform.position;
            Vector2 previousPosition = transform.previousPosition;
            Vector2 velocity = body.velocity;
            //TODO: ground checks should be made for all entities capable of movement and grounded, but before the moveEntities step
            if (body.usesGravity && body.onGround){
                //do the ground check:
                ColliderComponent collider = entityComponentManager.getColliderComponent(entityID);
                boolean stillOnGround = groundCheck(collider, transform, entityID);
                if (!stillOnGround){
                    body.onGround = false;
                }
            }
            // apply gravity
            if (body.usesGravity && !body.onGround){
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

    private boolean groundCheck(ColliderComponent collider, TransformComponent transform, int entityID){
        boolean stillOnGround = true;
        if (collider.colliderType == ColliderType.AABB){
            stillOnGround = groundCheckForAABB(collider, transform, entityID);
        }
        return stillOnGround;
    }

    private boolean groundCheckForAABB(ColliderComponent collider, TransformComponent transform, int entityID){
        boolean groundCollisionDetected = true;
        boolean atLeastOneCollision = false;

        AABBCollider transformedCollider = createAABBCollider(transform, collider);
        AABBCollider groundCollider = createAABBGroundCollider(transformedCollider);

        long signature = ComponentSignatures.TRANSFORM
                         & ComponentSignatures.PHYSICAL_BODY
                         & ComponentSignatures.COLLIDER;
        int[] potentialGround = entityComponentManager.getEntitiesMatchingSignature(signature);

        for (int otherEntityID : potentialGround){
            // check if collision is possible
            // impossible for entity to collide with itself
            if (entityID == otherEntityID){
                continue;
            }
            ColliderComponent otherCollider = entityComponentManager.getColliderComponent(otherEntityID);
            // impossible for entities on different layers to collide
            if (collider.layer != otherCollider.layer){
                continue;
            }
            TransformComponent otherTransform = entityComponentManager.getTransformComponent(otherEntityID);
            if (otherCollider.colliderType == ColliderType.AABB){
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

        long signature = ComponentSignatures.PHYSICAL_BODY &
                         ComponentSignatures.TRANSFORM &
                         ComponentSignatures.COLLIDER;
        int[] potentiallyColliding = entityComponentManager.getEntitiesMatchingSignature(signature);

        for (int entityID : potentiallyColliding){
            // if it's not dynamic, it can't move by itself
            // therefore it didn't INITIATE any collisions
            // * what about collisions if it was pushed?
            PhysicalBodyComponent body = entityComponentManager.getPhysicalBodyComponent(entityID);
            if (!body.dynamic){
                continue;
            }
            for (int otherEntityID : potentiallyColliding){
                // check if collision is possible
                // impossible for entity to collide with itself
                if (entityID == otherEntityID){
                    continue;
                }
                ColliderComponent collider1 = entityComponentManager.getColliderComponent(entityID);
                ColliderComponent collider2 = entityComponentManager.getColliderComponent(otherEntityID);
                // impossible for entities on different layers to collide
                if (collider1.layer != collider2.layer){
                    continue;
                }

                // check if objects are colliding
                TransformComponent transform1 = entityComponentManager.getTransformComponent(entityID);
                TransformComponent transform2 = entityComponentManager.getTransformComponent(otherEntityID);
                boolean collisionDetected = detectCollision(transform1, collider1, transform2, collider2);

                if (!collisionDetected){
                    continue;
                }
                // resolve collision
                // we know that entity is dynamic
                // now if other entity is static:
                PhysicalBodyComponent otherBody = entityComponentManager.getPhysicalBodyComponent(otherEntityID);
                if (otherBody.dynamic){
                    // resolve collision between 2 dynamic entities
                }
                else {
                    resolveDynamicxStaticAABBCollision(transform1, body, collider1,
                                                       transform2, otherBody, collider2);
                }
                System.out.println("collision detected between" + entityID + " and " + otherEntityID);
            }
        }

    }

    public boolean detectCollision(TransformComponent transformA, ColliderComponent colliderA,
                                   TransformComponent transformB, ColliderComponent colliderB){
        boolean collisionDetected = false;

        if (colliderA.colliderType == ColliderType.AABB && colliderB.colliderType == ColliderType.AABB){
            AABBCollider transformedColliderA = createAABBCollider(transformA, colliderA);
            AABBCollider transformedColliderB = createAABBCollider(transformB, colliderB);
            collisionDetected = detectAABBxAABBCollision(transformedColliderA, transformedColliderB);
        }
        return collisionDetected;
    }

    public void resolveDynamicxStaticAABBCollision(TransformComponent dTransform, PhysicalBodyComponent dBody, ColliderComponent dCollider,
                                                   TransformComponent sTransform, PhysicalBodyComponent sBody, ColliderComponent sCollider){

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
