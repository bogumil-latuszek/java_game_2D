package io.github.JavaGame2D.Components;

import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.JavaGame2D.Enums.ComponentType;

public class DrawableComponent implements Component{
    public ComponentType componentType = ComponentType.DRAWABLE;
    @JsonIgnore
    public Texture sprite;


    public DrawableComponent(){
    }
    public DrawableComponent(Texture sprite){
        this.sprite = sprite;
    }

    @Override
    public ComponentType type() {
        return this.componentType;
    }
}
