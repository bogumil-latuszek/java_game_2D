package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.SpriteData;
import io.github.JavaGame2D.Enums.BodySegmentType;

import java.util.EnumMap;

public class DrawableComponent implements Component{
    public SpriteData spriteData = new SpriteData();
    // every entity with SegmentedDrawableBodyComponent needs to have DrawableComponent
    // but not every entity with DrawableComponent has SegmentedDrawableBodyComponent
    public boolean hasSegmentedBody = false;


    @Override
    public long getSignature() {
        return ComponentSignatures.DRAWABLE;
    }
}
