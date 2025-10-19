package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.Enums.ComponentType;

public class DrawableComponent implements Component{
    @Override
    public ComponentType type() {
        return ComponentType.DRAWABLE;
    }
}
