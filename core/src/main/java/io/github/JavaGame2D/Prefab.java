package io.github.JavaGame2D;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.JavaGame2D.Components.Component;

import java.util.HashMap;

public class Prefab {
    // TODO: rename to "components"
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // necessary to avoid deserialization errors
    public HashMap<Class<?>, Object> components;

    public Prefab() {
        this.components = new HashMap<>();
    }

    public HashMap<Class<?>, Object> copyComponents(){
        HashMap<Class<?>, Object> componentsCopy = new HashMap<>();

        for(HashMap.Entry<Class<?>, Object> entry : this.components.entrySet()){
            Class<?> componentClass = entry.getKey();
            Component componentInstance = (Component) entry.getValue(); // Does this actually remove some info?
            Component componentCopy = componentInstance.makeCopy();
            componentsCopy.put(componentClass, componentCopy);
        }

        return componentsCopy;
    }

//    public Prefab makeCopy(){
//        Prefab prefabCopy = new Prefab();
//
//        for(HashMap.Entry<Class<?>, Object> entry : prefabComponents.entrySet()){
//            Class<?> componentClass = entry.getKey();
//            Component componentInstance = (Component) entry.getValue(); // Does this actually remove some info?
//            Component componentCopy = componentInstance.makeCopy();
//            prefabCopy.prefabComponents.put(componentClass, componentCopy);
//        }
//
//        return prefabCopy;
//    }

}
