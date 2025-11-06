package io.github.JavaGame2D.Components;

import com.badlogic.gdx.graphics.Texture;
import io.github.JavaGame2D.Enums.ComponentType;

public class DrawableComponent implements Component{
    public Texture sprite;

    public DrawableComponent(Texture sprite){
        this.sprite = sprite;
    }

    @Override
    public ComponentType type() {
        return ComponentType.DRAWABLE;
    }
}
