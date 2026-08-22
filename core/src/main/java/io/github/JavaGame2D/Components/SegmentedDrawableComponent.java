package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.SpriteData;

import java.util.EnumMap;

public class SegmentedDrawableComponent implements Component{

    public EnumMap<BodySegmentType, SpriteData> drawableSegments = new EnumMap<>(BodySegmentType.class);

    @Override
    public long getSignature() {
        return ComponentSignatures.SEGMENTED_DRAWABLE;
    }
}
