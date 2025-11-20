package io.github.JavaGame2D.Components;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Enums.ColliderType;
import io.github.JavaGame2D.Enums.ComponentType;
import io.github.JavaGame2D.OnCollisionActions.OnCollisionAction;

public class ColliderComponent implements Component {

    public ComponentType componentType = ComponentType.COLLIDER;
    public int layer;
    public ColliderType colliderType;
    public boolean usesOffset;
    public Vector2 offset;
    public float width;
    public float height;
    public boolean sizeFromTransform;
    public boolean triggersAction = false;
    //public EventType eventType = EventType.TELEPORT; //no longer used
    // CollisionEventThrower delegate; <= function to call after collision triggers event.
    //                                    it has signature: function(Entity emitter, Entity other);
    //                                    defined by it's type (CollisionEventThrower)
    //                                    but can have different internal logic
    public OnCollisionAction action;
    public ColliderComponent(){
        this.layer = 0;
        this.colliderType = ColliderType.AABB;
        this.usesOffset = false;
        this.sizeFromTransform = true;
        this.offset = new Vector2(0f,0f);
        this.height = 0;
        this.width = 0;
    }

    @Override
    public ComponentType type() {
        return this.componentType;
    }
}
