package io.github.JavaGame2D.Components;

import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component{

    // position of the center
    public Vector2 position;
    // previous frame position
    public Vector2 previousPosition;

    public TransformComponent(){
        this.position = new Vector2(0f,0f);
        this.previousPosition = new Vector2(0f,0f);
    }

    @Override
    public long getSignature() {
        return ComponentSignatures.TRANSFORM;
    }

    @Override
    public Component makeCopy() {
        TransformComponent temp = new TransformComponent();
        temp.position = this.position.cpy();
        temp.previousPosition = this.previousPosition.cpy();
        return temp;
    }
}
