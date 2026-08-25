package io.github.JavaGame2D;

import io.github.JavaGame2D.Components.Component;

import java.util.HashMap;

public class Prefab {
    public HashMap<Class<?>, Object> prefabComponents;

    public Prefab() {
        this.prefabComponents = new HashMap<>();
    }

    public Prefab makeCopy(){
        Prefab prefabCopy = new Prefab();

        for(HashMap.Entry<Class<?>, Object> entry : prefabComponents.entrySet()){
            Class<?> componentClass = entry.getKey();
            Component componentInstance = (Component) entry.getValue(); // Does this actually remove some info?
            Component componentCopy = componentInstance.makeCopy();
            prefabCopy.prefabComponents.put(componentClass, componentCopy);
        }

        return prefabCopy;
    }
}
