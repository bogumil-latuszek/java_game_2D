package io.github.JavaGame2D.Components;

import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.JavaGame2D.Enums.ComponentType;

public class DrawableComponent implements Component{
    public final long signature = ComponentSignatures.DRAWABLE;
    public String texturePath;
    @JsonIgnore
    public Texture sprite;

    @Override
    public long getSignature() {
        return this.signature;
    }
}
