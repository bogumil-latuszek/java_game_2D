package io.github.JavaGame2D;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Enums.FacingDirection;

// It's called "SpriteData" to avoid confusion with LibGDX's own "Sprite" class
public class SpriteData {
    public Vector2 offset = new Vector2();
    public int textureID = -1; // missing texture by default

    public boolean textureIsTiled = false;
    public boolean usesSizeFromTexture = true; // true for pixel-perfect graphics, false when texture has to stretch
    public int pixelsPerUnit = 1; //used for pixel-perfect texture tiling

    public float width = 1;
    public float height = 1;
    public boolean mirrorVertical = false;
    public boolean mirrorHorizontal = false;
    public FacingDirection facingDirection = FacingDirection.NONE; // this field should be kept accurate to drawable visual direction


    public SpriteData(){}

    public SpriteData(int textureID, FacingDirection facingDirection){
        this.textureID = textureID;
        this.facingDirection = facingDirection;
    }

    public SpriteData makeCopy(){
        SpriteData temp = new SpriteData();
        temp.offset = this.offset.cpy();
        temp.textureID = this.textureID;
        temp.textureIsTiled = this.textureIsTiled;
        temp.usesSizeFromTexture = this.usesSizeFromTexture;
        temp.pixelsPerUnit = this.pixelsPerUnit;
        temp.width = this.width;
        temp.height = this.height;
        temp.mirrorVertical = this.mirrorVertical;
        temp.mirrorHorizontal = this.mirrorHorizontal;
        temp.facingDirection = this.facingDirection; // enums are passed by ref, but that's ok since it's a ref to a singleton
        return temp;
    }
}
