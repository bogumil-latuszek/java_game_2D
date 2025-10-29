package io.github.JavaGame2D.Components;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Enums.ColliderType;
import io.github.JavaGame2D.Enums.ComponentType;

public class ColliderComponent implements Component {
    public int layer;
    public ColliderType type;
    public boolean usesOffset;
    public Vector2 offset;
    public float width;
    public float height;
    public boolean sizeFromTransform;

    public ColliderComponent(){
        this.layer = 0;
        this.type = ColliderType.AABB;
        this.usesOffset = false;
        this.sizeFromTransform = true;
        this.offset = new Vector2(0f,0f);
        this.height = 0;
        this.width = 0;
    }

    @Override
    public ComponentType type() {
        return ComponentType.COLLIDER;
    }
}
