package io.github.JavaGame2D;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Drawable{
    public Vector2 offset = new Vector2();
    public Texture texture = null;
    public boolean usesTransformWidth = true;
    public float transformWidthMultiplier = 1;
    public float width = 1;
    public boolean usesTransformHeight = true;
    public float transformHeightMultiplier = 1;
    public float height = 1;
}
