package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.SpriteData;

import java.util.EnumMap;
import java.util.Map;

public class SegmentedDrawableComponent implements Component{

    public EnumMap<BodySegmentType, SpriteData> drawableSegments = new EnumMap<>(BodySegmentType.class);

    public static final long SIGNATURE = ComponentSignatures.register(SegmentedDrawableComponent.class);

    @Override
    public Component makeCopy() {
        SegmentedDrawableComponent temp = new SegmentedDrawableComponent();
        for (Map.Entry<BodySegmentType, SpriteData> entry : this.drawableSegments.entrySet()){
            BodySegmentType keyCopy = entry.getKey();
            SpriteData valueCopy = entry.getValue().makeCopy();
            temp.drawableSegments.put(keyCopy,valueCopy);
        }
        return temp;
    }
}
