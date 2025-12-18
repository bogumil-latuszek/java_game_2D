package io.github.JavaGame2D.Components;

import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.JavaGame2D.Drawable;
import io.github.JavaGame2D.Enums.BodySegmentType;

import java.util.EnumMap;
import java.util.HashMap;

public class DrawableComponent implements Component{
    public final long signature = ComponentSignatures.DRAWABLE;
    public int textureID;
    public EnumMap<BodySegmentType, Drawable> drawableSegments = new EnumMap<>(BodySegmentType.class);
    @Override
    public long getSignature() {
        return this.signature;
    }
}
