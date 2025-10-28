package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import io.github.JavaGame2D.Components.ColliderComponent;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ColliderType;
import io.github.JavaGame2D.Enums.ComponentType;
import jdk.javadoc.internal.doclets.toolkit.util.DocFinder;

import java.awt.geom.Line2D;

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
        boolean stillOnGround = true;
        boolean atLeastOneCollision = false;
        TransformComponent transform = (TransformComponent) entity.getComponent(ComponentType.TRANSFORM);
        ColliderComponent collider = (ColliderComponent) entity.getComponent(ComponentType.COLLIDER);

        // #1 construct "ground check collider" from collider:
        float groundColliserPositionX = transform.position.x;
        float groundColliderPositionY = transform.position.y - ( (transform.height/2) + groundCheckDepth );
        Vector2 groundColliderPosition = new Vector2(groundColliserPositionX, groundColliderPositionY );
        float groundColliderWidth = transform.width;
        float groundColliderHeight = groundCheckDepth;

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
            ColliderComponent collider2 = (ColliderComponent) otherEntity.getComponent(ComponentType.COLLIDER);
            // impossible for entities on different layers to collide
            if (collider.layer != collider2.layer){
                continue;
            }
            TransformComponent transfrom2 = (TransformComponent) otherEntity.getComponent(ComponentType.TRANSFORM);
            if (collider2.type == ColliderType.AABB){
                boolean detectedCollision = detectAABBxAABBCollision(groundColliderPosition, groundColliderWidth, groundColliderHeight,
                                                                     transfrom2.position, transfrom2.width, transfrom2.height);
                if(detectedCollision){
                    atLeastOneCollision = true;
                }
            }
        }
        if (!atLeastOneCollision){
            stillOnGround = false;
        }
        return stillOnGround;
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
                //System.out.println("collision detected between" + entity.getID() + " and " + otherEntity.getID());
            }
        }

    }

    public boolean detectCollision(Entity a, Entity b){
        boolean collisionDetected = false;

        TransformComponent transformA = (TransformComponent) a.getComponent(ComponentType.TRANSFORM);
        //PhysicalBodyComponent bodyA = (PhysicalBodyComponent) a.getComponent(ComponentType.PHYSICAL_BODY);
        ColliderComponent colliderA = (ColliderComponent) a.getComponent(ComponentType.COLLIDER);

        TransformComponent transformB = (TransformComponent) b.getComponent(ComponentType.TRANSFORM);
        //PhysicalBodyComponent bodyB = (PhysicalBodyComponent) b.getComponent(ComponentType.PHYSICAL_BODY);
        ColliderComponent colliderB = (ColliderComponent) b.getComponent(ComponentType.COLLIDER);

        Vector2 positionA = transformA.position;
        float widthA = transformA.width;
        float heightA = transformA.height;

        Vector2 positionB = transformB.position;
        float widthB = transformB.width;
        float heightB = transformB.height;

        if (colliderA.type == ColliderType.AABB && colliderB.type == ColliderType.AABB){
            collisionDetected = detectAABBxAABBCollision(positionA, widthA, heightA,
                                                         positionB, widthB, heightB);
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

        //1) find overlap on previous frame:
        Vector2 dPreviousPosition = dTransform.previousPosition;
        Vector2 sPreviousPosition = sTransform.previousPosition;

//        float dPreviousMinX = dPreviousPosition.x - dTransform.width/2;
//        float dPreviousMaxX = dPreviousPosition.x + dTransform.width/2;
//        float sPreviousMinX = sPreviousPosition.x - sTransform.width/2;
//        float sPreviousMaxX = sPreviousPosition.x + sTransform.width/2;

//        boolean previouslyOverlappedOnXAxis = false;


//        if (sPreviousMaxX > dPreviousMinX && dPreviousMaxX > sPreviousMinX){
//            previouslyOverlappedOnXAxis = true;
//        }
        boolean previouslyOverlappedOnYAxis = false;

        float dPreviousMinY = dPreviousPosition.y - dTransform.height/2;
        float dPreviousMaxY = dPreviousPosition.y + dTransform.height/2;
        float sPreviousMinY = sPreviousPosition.y - sTransform.height/2;
        float sPreviousMaxY = sPreviousPosition.y + sTransform.height/2;

        if (sPreviousMaxY > dPreviousMinY && dPreviousMaxY > sPreviousMinY){
            previouslyOverlappedOnYAxis = true;
        }

        float pushOffset = 0.0001f;// used to push dynamic entity slightly further to reduce unnecessary collisions
        if (previouslyOverlappedOnYAxis){
            float xAxisOverlap = calculateXAxisOverlap(dTransform.position.x, dTransform.width,
                sTransform.position.x, sTransform.width);

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
            float yAxisOverlap = calculateYAxisOverlap(dTransform.position.y, dTransform.height,
                sTransform.position.y, sTransform.height);

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
        //System.out.println("positions prev:" + dTransform.previousPosition.toString()+ "current: " + dTransform.position.toString());
    }

    public boolean detectAABBxAABBCollision(Vector2 positionA, float widthA, float heightA,
                                            Vector2 positionB, float widthB, float heightB){
        boolean overlapOnXAxis = false;
        boolean overlapOnYAxis = false;
        float aMinX = positionA.x - widthA/2;
        float aMaxX = positionA.x + widthA/2;
        float bMinX = positionB.x - widthB/2;
        float bMaxX = positionB.x + widthB/2;
        if ( (aMinX < bMinX && bMinX < aMaxX) || (aMinX < bMaxX && bMaxX < aMaxX) ||
             (bMinX < aMinX && aMinX < bMaxX) || (bMinX < aMaxX && aMaxX < bMaxX) ){

            overlapOnXAxis = true;
        }
        float aMinY = positionA.y - heightA/2;
        float aMaxY = positionA.y + heightA/2;
        float bMinY = positionB.y - heightB/2;
        float bMaxY = positionB.y + heightB/2;
        if ( (aMinY < bMinY && bMinY < aMaxY) || (aMinY < bMaxY && bMaxY < aMaxY) ||
             (bMinY < aMinY && aMinY < bMaxY) || (bMinY < aMaxY && aMaxY < bMaxY) ){
            overlapOnYAxis = true;
        }
        boolean detectedCollision = overlapOnXAxis && overlapOnYAxis;
        //System.out.println("col on X: "+overlapOnXAxis +" , col on Y: "+ overlapOnYAxis);
        return detectedCollision;
    }

    public float calculateXAxisOverlap(float aPositionX, float aWidth, float bPositionX, float bWidth){
        float aMinX = aPositionX - aWidth/2;
        float aMaxX = aPositionX + aWidth/2;
        float bMinX = bPositionX - bWidth/2;
        float bMaxX = bPositionX + bWidth/2;
        float minX = Math.min(aMinX,bMinX);
        float maxX = Math.max(aMaxX,bMaxX);
        float xRange = maxX - minX;
        float combinedWidth = aWidth+bWidth;
        float xAxisOverlap = combinedWidth-xRange;
        return xAxisOverlap;
    }

    public float calculateYAxisOverlap(float aPositionY, float aHeight, float bPositionY, float bHeight){
        float aMinY = aPositionY - aHeight/2;
        float aMaxY = aPositionY + aHeight/2;
        float bMinY = bPositionY - bHeight/2;
        float bMaxY = bPositionY + bHeight/2;
        float minY = Math.min(aMinY,bMinY);
        float maxY = Math.max(aMaxY,bMaxY);
        float yRange = maxY - minY;
        float combinedHeight = aHeight+bHeight;
        float yAxisOverlap = combinedHeight-yRange;
        return yAxisOverlap;
    }



}
