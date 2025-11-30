package io.github.JavaGame2D.Components;

import com.badlogic.gdx.math.Vector2;

public class PhysicalBodyComponent implements Component{

    public final long signature = ComponentSignatures.PHYSICAL_BODY;
    public Vector2 velocity;
    public boolean usesGravity;
    public boolean onGround;
    public boolean dynamic; // doesn't move by itself;
    public float moveSpeed;
    public float jumpForce;

    public PhysicalBodyComponent() {
        this.velocity = new Vector2(0f,0f);
        this.usesGravity = false;
        this.onGround = false;
        this.dynamic = true;
        moveSpeed = 300;
        jumpForce = 26f;
    }


    @Override
    public long getSignature() {
        return this.signature;
    }
}
