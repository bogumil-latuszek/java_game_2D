package io.github.JavaGame2D.Components;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Enums.ColliderType;

public class ColliderComponent implements Component {

    public int layer = 0;
    public ColliderType colliderType = ColliderType.AABB;
    public boolean usesOffset = false;
    public Vector2 offset = new Vector2();
    public float width;
    public float height;

    @Override
    public long getSignature() { return ComponentSignatures.COLLIDER; }

    @Override
    public Component makeCopy() {
        ColliderComponent temp = new ColliderComponent();
        temp.layer = this.layer;
        temp.colliderType = this.colliderType;
        temp.usesOffset = this.usesOffset;
        temp.offset = this.offset.cpy();
        temp.width = this.width;
        temp.height = this.height;
        return temp;
    }
}
