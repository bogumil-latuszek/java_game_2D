package io.github.JavaGame2D;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Enums.FacingDirection;

public class AnimationFrame {
    public Vector2 offset = new Vector2();
    public int textureID = -1; // missing texture by default
    public float width = 1;
    public float height = 1;
    public boolean mirrorVertical = false;
    public boolean mirrorHorizontal = false;
    public int pixelsPerUnit = 1;
    public FacingDirection facingDirection = FacingDirection.NONE; // this field should be kept accurate to drawable visual direction

    public AnimationFrame() {
    }

    public AnimationFrame(int textureID, FacingDirection facingDirection) {
        this.textureID = textureID;
        this.facingDirection = facingDirection;
    }

}
