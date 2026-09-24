package io.github.JavaGame2D.SaveData;

import com.badlogic.gdx.Gdx;
import io.github.JavaGame2D.Systems.EntityComponentManager;

import java.lang.reflect.Field;
import java.util.HashMap;

public class BakedEntity {

    // default values for serializer?
    public int entityID;
    HashMap<Class<?>, Object> components;

    // empty constructor for serializer
    public BakedEntity(){
    }

    public BakedEntity(int entityID, EntityComponentManager ecm){
        this.components = ecm.getEntityComponents(entityID);
        this.entityID = entityID;
    }

    public void load(EntityComponentManager ecm){

        // first, load entity id + signature?
        // actually, ignore that, we have loadEntitiesFromCollections() in ECM

        for (HashMap.Entry<Class<?>, Object> entry : components.entrySet()) {
            try {
                Class<?> componentClass = entry.getKey();
                Object componentInstance = entry.getValue();

                //ecm.addComponent(componentClass, componentInstance, entityID);
                addComponentSafely(ecm, componentClass, componentInstance, entityID);
            }
            catch (Exception e){
                Gdx.app.log("PrefabManager","Error while trying to apply prefab override to entityID: "+entityID);
            }
        }
    }

    // The "type capture" helper — isolates the unchecked cast to one place.
    @SuppressWarnings("unchecked")
    private <T> void addComponentSafely(EntityComponentManager ecm, Class<T> componentType, Object component, int entityID) {
        T typedComponent = componentType.cast(component); // Runtime safety check
        ecm.addComponent(componentType, typedComponent, entityID);
    }
}
