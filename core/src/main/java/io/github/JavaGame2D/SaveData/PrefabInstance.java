package io.github.JavaGame2D.SaveData;

import com.badlogic.gdx.Gdx;
import io.github.JavaGame2D.Components.PrefabComponent;
import io.github.JavaGame2D.Prefab;
import io.github.JavaGame2D.Systems.EntityComponentManager;
import io.github.JavaGame2D.Systems.PrefabManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class PrefabInstance {
    public int entityID;
    public String prefabName;
    public HashMap<String, Object> overrides = new HashMap<>();

    public PrefabInstance(int entityID, EntityComponentManager ecm, PrefabManager prefabManager) {
        this.entityID = entityID;

        PrefabComponent prefabComponent = ecm.getComponent(PrefabComponent.class, entityID);
        this.prefabName = prefabComponent.prefabName;

        Optional<Prefab> optionalPrefab = prefabManager.getPrefab(prefabName);
        if (optionalPrefab.isPresent()){
            Prefab prefab = optionalPrefab.get();
            this.overrides = createPrefabOverrides(this.entityID, prefab, ecm);
        }
        else{
            Gdx.app.log("PrefabOverride", "Prefab not loaded: " + prefabName);
        }
    }

    public HashMap<String, Object> createPrefabOverrides(int entityID, Prefab prefab, EntityComponentManager ecm){
        // Key: "ComponentName.fieldName"  Value: the component field value
        HashMap<String, Object> prefabOverrides = new HashMap<>();

        HashMap<Class<?>, Object> entityComponents = ecm.getEntityComponents(entityID);
        HashMap<Class<?>, Object> prefabComponents = prefab.copyComponents();

        // find all overrides
        for (HashMap.Entry<Class<?>, Object> prefabEntry : prefabComponents.entrySet()) {
            Class<?> compClass = prefabEntry.getKey();
            Object prefabComp = prefabEntry.getValue();

            // find the same type of component for entity
            Object entityComp = entityComponents.get(compClass);
            // If the entity is missing this component, skip it (out of scope for now).
            if (entityComp == null) continue;

            HashMap<String, Object> componentOverrides = createComponentOverrides(compClass, prefabComp, entityComp);
            prefabOverrides.putAll(componentOverrides);
        }
        return prefabOverrides;
    }

    private HashMap<String, Object> createComponentOverrides(Class<?> compClass, Object prefabComp, Object entityComp) {
        HashMap<String, Object> componentOverrides = new HashMap<>();
        String componentName = compClass.getSimpleName();

        for (Field field : compClass.getFields()) { // getFields() = public fields only
            try {
                Object prefabValue = field.get(prefabComp);
                Object entityValue = field.get(entityComp);

                // Objects.equals handles nulls safely and uses .equals() for objects
                // (Vector2, String, Color, etc. all implement .equals() correctly in LibGDX).
                if (!Objects.equals(prefabValue, entityValue)) {
                    String key = componentName + "." + field.getName();
                    componentOverrides.put(key, entityValue);
                }
            } catch (IllegalAccessException e) {
                Gdx.app.log("PrefabOverride", "Cannot read field " + field.getName() + ": " + e.getMessage());
            }
        }
        return componentOverrides;
    }
}
