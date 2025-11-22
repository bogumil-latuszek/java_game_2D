package io.github.JavaGame2D.Components;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Enums.ColliderType;
import io.github.JavaGame2D.Enums.ComponentType;

public class ColliderComponent implements Component {

    public ComponentType componentType = ComponentType.COLLIDER;
    public int layer;
    public ColliderType colliderType;
    public boolean usesOffset;
    public Vector2 offset;
    public float width;
    public float height;
    public boolean sizeFromTransform;

    @Override
    public ComponentType type() {
        return this.componentType;
    }
}
