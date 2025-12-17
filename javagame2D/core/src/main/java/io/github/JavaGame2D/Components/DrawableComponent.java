package io.github.JavaGame2D.Components;

import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.JavaGame2D.Enums.BodySegmentType;

import java.util.HashMap;

public class DrawableComponent implements Component{
    public final long signature = ComponentSignatures.DRAWABLE;
    public int textureID;
    //public HashMap<BodySegmentType, Drawable> drawableSegments
    /*
    public class Drawable{
        Vector2 offset = 0;
        Texture texture = null;
        bool usesTransformWidth = true;
        float transformWidthMultiplier = 1;
        float width = 1;
        bool usesTransformHeight = true;
        float transformHeightMultiplier = 1;
        float height = 1;
    }
     */
    @Override
    public long getSignature() {
        return this.signature;
    }
}
