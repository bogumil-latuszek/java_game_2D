package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.SpriteData;

import java.util.EnumMap;

public class SegmentedDrawableComponent implements Component{
    public final long signature = ComponentSignatures.SEGMENTED_DRAWABLE;
    public EnumMap<BodySegmentType, SpriteData> drawableSegments = new EnumMap<>(BodySegmentType.class);

    @Override
    public long getSignature() {
        return this.signature;
    }
}
