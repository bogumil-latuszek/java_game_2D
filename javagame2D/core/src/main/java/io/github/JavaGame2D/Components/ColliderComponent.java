package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.Enums.ColliderType;
import io.github.JavaGame2D.Enums.ComponentType;

public class ColliderComponent implements Component {
    public int layer;
    public ColliderType type;

    public ColliderComponent(){
        this.layer = 0;
        this.type = ColliderType.AABB;
    }

    @Override
    public ComponentType type() {
        return ComponentType.COLLIDER;
    }
}
