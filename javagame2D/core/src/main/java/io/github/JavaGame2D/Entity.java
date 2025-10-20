package io.github.JavaGame2D;

import io.github.JavaGame2D.Components.Component;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Enums.ComponentType;
import io.github.JavaGame2D.Systems.EntityManager;

import java.util.HashMap;

public class Entity {
    private final int ID;
    private HashMap<ComponentType, Component> components;


    // Since Entity will only ever be created by EntityManager
    // the ID will just get passed to it's constructor
    public Entity(int ID) {
        this.ID = ID;
        this.components = new HashMap<ComponentType, Component>();
    }
    public int getID(){
        return this.ID;
    }

    public boolean hasComponent(ComponentType componentType){
        return this.components.containsKey(componentType);
    }

    public Component getComponent(ComponentType componentType){
        return this.components.get(componentType);
    }

//    public <T extends Component> getComponent(Class<T> componentClass){
//        return this.components.get(componentClass);
//    }

    public Component getDrawableComponent(ComponentType componentType){
        return this.components.get(componentType);
    }

    public void addComponent(Component component){
        ComponentType componentType = component.type();
        if (!components.containsKey(componentType)){
            components.put(componentType, component);
        }
    }
}
