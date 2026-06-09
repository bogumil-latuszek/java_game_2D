package io.github.JavaGame2D;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Enums.FacingDirection;

public class Drawable{
    public Vector2 offset = new Vector2();
    public Texture texture = null;
    public boolean usesTransformWidth = true;
    public float transformWidthMultiplier = 1;
    public float width = 1;
    public boolean usesTransformHeight = true;
    public float transformHeightMultiplier = 1;
    public float height = 1;
    public boolean mirrorVertical = false;
    public boolean mirrorHorizontal = false;
    public FacingDirection facingDirection = FacingDirection.NONE; // this field should be kept accurate to drawable visual direction
    public int pixelsPerUnit = 1;

    public Drawable(){}

    public Drawable(Texture texture, FacingDirection facingDirection){
        this.texture = texture;
        this.facingDirection = facingDirection;
    }

}
