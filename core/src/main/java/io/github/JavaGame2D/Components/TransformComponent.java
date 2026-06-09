package io.github.JavaGame2D.Components;

import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component{

    public final long signature = ComponentSignatures.TRANSFORM;
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
    public long getSignature() {
        return this.signature;
    }
}
