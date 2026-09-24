package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.Component;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.PrefabComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Prefab;
import io.github.JavaGame2D.SaveData.PrefabInstance;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;
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

    public void createEntityFromPrefab(String prefabName, Vector2 position){

        int entityID = ecm.createEntity(position);

        // 2. assign PrefabComponent

        PrefabComponent prefabComp = new PrefabComponent(prefabName);
        ecm.addComponent(PrefabComponent.class, prefabComp, entityID);
        ecm.addSignature(entityID, ComponentSignatures.PREFAB);

        // 3. populate Entity with Components copied from Prefab

        populateEntity(entityID, prefabName);
    }

    public void loadEntityFromPrefabInstance(PrefabInstance prefabInstance){

        // 1. load data from PrefabInstance
        int entityID = prefabInstance.entityID;
        String prefabName = prefabInstance.prefabName;
        HashMap<String,Object> overrides = prefabInstance.overrides;

        // 2. assign PrefabComponent

        PrefabComponent prefabComp = new PrefabComponent(prefabName);
        ecm.addComponent(PrefabComponent.class, prefabComp, entityID);
        ecm.addSignature(entityID, ComponentSignatures.PREFAB);

        // 3. populate Entity with Components copied from Prefab
        populateEntity(entityID, prefabName);

        // 4. apply overrides
        applyPrefabOverrides(overrides, entityID);


    }

    public Optional<PrefabInstance> createPrefabInstanceFromEntity(int entityID){
        Entity entity = ecm.getEntity(entityID);
        // TODO: create entityHasComponent(ComponentSignature) in ecm
        if ((entity.signature & ComponentSignatures.PREFAB) == ComponentSignatures.PREFAB){
            PrefabInstance prefabInstance = new PrefabInstance(entityID, this.ecm, this);
            return Optional.of(prefabInstance);
        }
        return Optional.empty();
    }

    private void populateEntity(int entityID, String prefabName){
        // 1. load prefab
        Optional<Prefab> prefabOptional = this.getPrefab(prefabName);

        if (!prefabOptional.isPresent()){
            return; // no such prefab
        }

        Prefab prefab = prefabOptional.get();
        HashMap<Class<?>, Object> prefabComponents = prefab.copyComponents();

        // 2. assign copied prefab components to entityID
        for (HashMap.Entry<Class<?>, Object> entry : prefabComponents.entrySet()) {
            Class<?> componentClass = entry.getKey();
            Object componentInstance = entry.getValue();

            // copy each component?
            //Component.copy()

            // try casting a component from entry, and add it to entityId
            try {
                this.addComponentSafely(entityID, componentClass, componentInstance);
            }
            catch (ClassCastException e) {
                Gdx.app.log("PrefabManager","Prefab "+prefabName+" is corrupted, omitting Component: "+componentClass.getName());
            }
        }
    }

    public void applyPrefabOverrides(HashMap<String,Object> overrides, int entityID){
        // For each override entry, find the component and set the field.
        // Example override: {"HealthComponent.maxHp" : 500}
        for (HashMap.Entry<String, Object> entry : overrides.entrySet()) {
            try {
                String[] parts = entry.getKey().split("\\.");
                String className = parts[0];
                String fieldName = parts[1];

                // get component type (class)
                Class<?> compClass = Class.forName(className);

                // get component instance
                Object component = ecm.getComponent(compClass, entityID);

                // Use reflection to set the field value for that instance
                Field field = compClass.getField(fieldName);
                field.set(component, entry.getValue());
            }
            catch (Exception e){
                Gdx.app.log("PrefabManager","Error while trying to apply prefab override to entityID: "+entityID);
            }
        }
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



    public void savePrefab(String prefabName, Prefab prefab){
        this.fileSystem.savePrefab(prefabName, prefab);
    }

//    public int instantiateWithOverrides(int entityId, String prefabName, HashMap<String, Object> overrides, EntityComponentManager ecm) {
//
//        // 1. load prefab
//        Optional<Prefab> prefabOptional = this.getPrefab(prefabName);
//
//        if (!prefabOptional.isPresent()){
//            return -1; // no such prefab
//        }
//
//        Prefab prefab = prefabOptional.get();
//
//        Prefab prefabCopy = prefab.makeCopy();
//
//        HashMap<Class<?>, Object> prefabComponents = prefabCopy.prefabComponents;
//
//        // 2. copy as many components from Prefab to entity
//        for (HashMap.Entry<Class<?>, Object> entry : prefabComponents.entrySet()) {
//            Class<?> componentClass = entry.getKey();
//            Object componentInstance = entry.getValue();
//
//            // copy each component?
//            //Component.copy()
//
//            // try casting a component from entry, and add it to entityId
//            try {
//                this.addComponentSafely(entityId, componentClass, componentInstance);
//            }
//            catch (ClassCastException e) {
//                Gdx.app.log("PrefabManager","Prefab "+prefabName+" is corrupted, omitting Component: "+componentClass.getName());
//            }
//        }
//
//        // 3. Apply overrides using the flat path map
//        for (HashMap.Entry<String, Object> entry : overrides.entrySet()) {
//            String path = entry.getKey();
//            Object value = entry.getValue();
//
//            // Split at the FIRST dot only (to support nested fields later)
//            int dotIndex = path.indexOf('.');
//            if (dotIndex == -1) {
//                Gdx.app.log("PrefabManager", "Invalid override path: " + path);
//                continue;
//            }
//
//            String componentClassName = path.substring(0, dotIndex);
//            String fieldPath = path.substring(dotIndex + 1);
//
//            try {
//                // get component instance
//                Class<?> compClass = Class.forName(componentClassName);
//                Object compInstance = ecm.getComponent(compClass, entityId);
//
//                if (compInstance == null) {
//                    // component doesn't exist on this prefab version. Skip gracefully.
//                    Gdx.app.log("PrefabManager", "Override skipped: " + componentClassName + " not found on " + prefabName);
//                    continue;
//                }
//
//                // navigate nested fields (e.g., "position.x" -> get Field "position", then Field "x")
//                Object targetObject = compInstance;
//                String[] fieldParts = fieldPath.split("\\.");
//                for (int i = 0; i < fieldParts.length - 1; i++) {
//                    Field field = getFieldRecursive(targetObject.getClass(), fieldParts[i]);
//                    targetObject = field.get(targetObject);
//                }
//
//                // set the final field
//                Field finalField = getFieldRecursive(targetObject.getClass(), fieldParts[fieldParts.length - 1]);
//                finalField.set(targetObject, convertValue(value, finalField.getType()));
//
//            } catch (ClassNotFoundException e) {
//                // Component class doesn't exist in code anymore. Skip this override.
//                Gdx.app.log("PrefabManager", "Stale override skipped: " + componentClassName);
//            } catch (Exception e) {
//                Gdx.app.log("PrefabManager", "Failed to apply override " + path + ": " + e.getMessage());
//            }
//        }
//
//        return entityId;
//    }

    @SuppressWarnings("unchecked") // Safe because the Prefab guarantees value matches key.
    private <T> void addComponentSafely(int entityId, Class<T> componentType, Object component) {
        // The .cast() method performs a runtime check (throwing ClassCastException if wrong).
        // Since we trust our serialization, this is perfectly safe.
        T typedComponent = componentType.cast(component);
        ecm.addComponent(componentType, typedComponent, entityId);
    }

    // Helper to traverse inheritance hierarchies for fields
//    private Field getFieldRecursive(Class<?> clazz, String fieldName) throws NoSuchFieldException {
//        while (clazz != null) {
//            try {
//                return clazz.getDeclaredField(fieldName);
//            } catch (NoSuchFieldException e) {
//                clazz = clazz.getSuperclass();
//            }
//        }
//        throw new NoSuchFieldException(fieldName);
//    }

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
