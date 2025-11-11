package io.github.JavaGame2D.Components;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Enums.ComponentType;

public class TransformComponent implements Component{

    public ComponentType componentType = ComponentType.TRANSFORM;
    // position of the center
    public Vector2 position;
    // previous frame position
    public Vector2 previousPosition;
    public float width;
    public float height;

    public TransformComponent(){
        this.position = new Vector2(0f,0f);
        this.previousPosition = new Vector2(0f,0f);
        this.width = 1;
        this.height = 1;
    }

    @Override
    public ComponentType type() {
        return this.componentType;
    }
}
