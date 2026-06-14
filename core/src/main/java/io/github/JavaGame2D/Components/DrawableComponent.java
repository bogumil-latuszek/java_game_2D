package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.Drawable;
import io.github.JavaGame2D.Enums.BodySegmentType;

import java.util.EnumMap;

public class DrawableComponent implements Component{
    public final long signature = ComponentSignatures.DRAWABLE;
    public int textureID;
    public boolean tiledTexture = false;
    public EnumMap<BodySegmentType, Drawable> drawableSegments = new EnumMap<>(BodySegmentType.class);
    @Override
    public long getSignature() {
        return this.signature;
    }
}
