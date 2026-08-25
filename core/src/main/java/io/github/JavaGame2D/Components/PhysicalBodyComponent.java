package io.github.JavaGame2D.Components;

import com.badlogic.gdx.math.Vector2;

public class PhysicalBodyComponent implements Component{

    public Vector2 velocity;
    public boolean usesGravity;
    public boolean onGround;
    public boolean dynamic; // doesn't move by itself;
    public float moveSpeed;
    public float jumpForce;
    public boolean ignoresPhysicalCollision = false;

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
        return ComponentSignatures.PHYSICAL_BODY;
    }

    @Override
    public Component makeCopy() {
        PhysicalBodyComponent temp = new PhysicalBodyComponent();
        temp.velocity = this.velocity.cpy();
        temp.usesGravity = this.usesGravity;
        temp.onGround = this.onGround;
        temp.dynamic = this.dynamic;
        temp.moveSpeed = this.moveSpeed;
        temp.jumpForce = this.jumpForce;
        temp.ignoresPhysicalCollision = this.ignoresPhysicalCollision;
        return temp;
    }
}
