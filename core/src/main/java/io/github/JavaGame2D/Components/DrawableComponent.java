package io.github.JavaGame2D.Components;

import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DrawableComponent implements Component{
    public final long signature = ComponentSignatures.DRAWABLE;
    public int textureID;

    @Override
    public long getSignature() {
        return this.signature;
    }
}
