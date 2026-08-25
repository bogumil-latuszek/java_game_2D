package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import io.github.JavaGame2D.Components.Component;
import io.github.JavaGame2D.Prefab;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Optional;

public class PrefabManager {

    private HashMap<String, Prefab> loadedPrefabs;
    private FileSystem fileSystem;
    EntityComponentManager ecm;

    public PrefabManager(FileSystem fileSystem, EntityComponentManager ecm) {
        loadedPrefabs = new HashMap<>();
        this.fileSystem = fileSystem;
        this.ecm = ecm;
    }

    public Optional<Prefab> getPrefab (String prefabName){
        if (loadedPrefabs.containsKey(prefabName)){
            Prefab prefab = loadedPrefabs.get(prefabName);
            return Optional.of(prefab);
        }
        else{
            Optional<Prefab> prefabOptional = fileSystem.loadPrefab(prefabName);
            if (prefabOptional.isPresent()){
                this.loadedPrefabs.put(prefabName,prefabOptional.get());
            }
            return prefabOptional;
        }
    }

    public int instantiateWithOverrides(int entityId, String prefabName, HashMap<String, Object> overrides, EntityComponentManager ecm) {

        // 1. load prefab
        Optional<Prefab> prefabOptional = this.getPrefab(prefabName);

        if (!prefabOptional.isPresent()){
            return -1; // no such prefab
        }

        Prefab prefab = prefabOptional.get();

        Prefab prefabCopy = prefab.makeCopy();

        HashMap<Class<?>, Object> prefabComponents = prefabCopy.prefabComponents;

        // 2. copy as many components from Prefab to entity
        for (HashMap.Entry<Class<?>, Object> entry : prefabComponents.entrySet()) {
            Class<?> componentClass = entry.getKey();
            Object componentInstance = entry.getValue();

            // copy each component?
            //Component.copy()

            // try casting a component from entry, and add it to entityId
            try {
                this.addComponentSafely(entityId, componentClass, componentInstance);
            }
            catch (ClassCastException e) {
                Gdx.app.log("PrefabManager","Prefab "+prefabName+" is corrupted, omitting Component: "+componentClass.getName());
            }
        }

        // 3. Apply overrides using the flat path map
        for (HashMap.Entry<String, Object> entry : overrides.entrySet()) {
            String path = entry.getKey();
            Object value = entry.getValue();

            // Split at the FIRST dot only (to support nested fields later)
            int dotIndex = path.indexOf('.');
            if (dotIndex == -1) {
                Gdx.app.log("PrefabManager", "Invalid override path: " + path);
                continue;
            }

            String componentClassName = path.substring(0, dotIndex);
            String fieldPath = path.substring(dotIndex + 1);

            try {
                // get component instance
                Class<?> compClass = Class.forName(componentClassName);
                Object compInstance = ecm.getComponent(compClass, entityId);

                if (compInstance == null) {
                    // component doesn't exist on this prefab version. Skip gracefully.
                    Gdx.app.log("PrefabManager", "Override skipped: " + componentClassName + " not found on " + prefabName);
                    continue;
                }

                // navigate nested fields (e.g., "position.x" -> get Field "position", then Field "x")
                Object targetObject = compInstance;
                String[] fieldParts = fieldPath.split("\\.");
                for (int i = 0; i < fieldParts.length - 1; i++) {
                    Field field = getFieldRecursive(targetObject.getClass(), fieldParts[i]);
                    targetObject = field.get(targetObject);
                }

                // set the final field
                Field finalField = getFieldRecursive(targetObject.getClass(), fieldParts[fieldParts.length - 1]);
                finalField.set(targetObject, convertValue(value, finalField.getType()));

            } catch (ClassNotFoundException e) {
                // Component class doesn't exist in code anymore. Skip this override.
                Gdx.app.log("PrefabManager", "Stale override skipped: " + componentClassName);
            } catch (Exception e) {
                Gdx.app.log("PrefabManager", "Failed to apply override " + path + ": " + e.getMessage());
            }
        }

        return entityId;
    }

    @SuppressWarnings("unchecked") // Safe because the Prefab guarantees value matches key.
    private <T> void addComponentSafely(int entityId, Class<T> componentType, Object component) {
        // The .cast() method performs a runtime check (throwing ClassCastException if wrong).
        // Since we trust our serialization, this is perfectly safe.
        T typedComponent = componentType.cast(component);
        ecm.addComponent(componentType, typedComponent, entityId);
    }

    // Helper to traverse inheritance hierarchies for fields
    private Field getFieldRecursive(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    // Basic type conversion from JSON deserialized values
    private Object convertValue(Object raw, Class<?> targetType) {
        if (targetType == int.class || targetType == Integer.class) {
            return ((Number) raw).intValue();
        } else if (targetType == float.class || targetType == Float.class) {
            return ((Number) raw).floatValue();
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return raw;
        } else if (targetType == String.class) {
            return raw.toString();
        }
        // Add more types as needed (Color, Vector2, etc.)
        return raw;
    }
}
