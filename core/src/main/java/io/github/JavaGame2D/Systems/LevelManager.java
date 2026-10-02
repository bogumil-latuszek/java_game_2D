package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.FinishedLoadingLevelEvent;
import io.github.JavaGame2D.Events.LoadingNewLevelEvent;
import io.github.JavaGame2D.Events.PlayerIDChanged;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;
import io.github.JavaGame2D.Events.PlayerHpChanged;
import io.github.JavaGame2D.Level;
import io.github.JavaGame2D.SaveData.BakedEntity;
import io.github.JavaGame2D.SaveData.PrefabInstance;

import java.util.*;

public class LevelManager {
    public HashMap<Integer,Vector2> spawnPointIDtoPosition;
    public int defaultSpawnPoint;

    //private Level currentLevel;
    private int currentLevelID = -1;
    private FileSystem fileSystem;
    private PrefabManager prefabManager;
    EntityComponentManager ecm;
    TeleportPlayerEvent teleportEventToResolve;
    private int playerID = -1; // -1 for null

    public LevelManager(FileSystem fileSystem, EntityComponentManager entityComponentManager, PrefabManager prefabManager) {
        this.fileSystem = fileSystem;
        this.prefabManager = prefabManager;
        this.ecm = entityComponentManager;
        EventBus.getInstance().subscribe(TeleportPlayerEvent.class, this::handleTeleportEvent);
        this.defaultSpawnPoint = 0;
        this.spawnPointIDtoPosition = new HashMap<>();
        this.spawnPointIDtoPosition.put(0, new Vector2(0,0));
    }


    // level manager can load level when given its ID
    // it uses file manager to load appropriate assets

    public void loadLevelIfChanged(){
        if (this.teleportEventToResolve != null){
            int newLevelID = teleportEventToResolve.targetLevelID;
            changeCurrentLevel(newLevelID);
            teleportEventToResolve = null;
        }
    }

    private void loadEntityFromBakedEntity(BakedEntity bakedEntity){
        // 1. load data from BakedEntity
        int entityID = bakedEntity.entityID;
        HashMap<Class<?>,Object> components = bakedEntity.components;

        // 2. populate Entity with Components copied from BakedEntity
        for (HashMap.Entry<Class<?>, Object> entry : components.entrySet()) {
            Class<?> componentClass = entry.getKey();
            Object componentInstance = entry.getValue();

            // try casting a component from entry, and add it to entityId
            try {
                this.addComponentSafely(entityID, componentClass, componentInstance);
            }
            catch (ClassCastException e) {
                Gdx.app.log("LevelManager","BakedEntity "+entityID+" is corrupted, omitting Component: "+componentClass.getName());
            }
        }
    }

    //TODO: Move this to EntityComponentManager
    @SuppressWarnings("unchecked") // Safe because the Prefab guarantees value matches key.
    private <T> void addComponentSafely(int entityId, Class<T> componentType, Object component) {
        // The .cast() method performs a runtime check (throwing ClassCastException if wrong).
        // Since we trust our serialization, this is perfectly safe.
        T typedComponent = componentType.cast(component);
        ecm.addComponent(componentType, typedComponent, entityId);
    }


    public void loadLevel (int levelID){
        // #1 load and deserialize level
        Optional<Level> levelOptional = fileSystem.loadLevel(levelID);
        if (!levelOptional.isPresent()){
            Gdx.app.log("LevelManager","Level: "+levelID+" couldn't be loaded");
            return;
        }
        Level level = levelOptional.get();

        ArrayList<PrefabInstance> prefabInstances = level.prefabInstances;
        ArrayList<BakedEntity> bakedEntities = level.bakedEntities;

        // 2. load Entities from prefabInstances
        for (PrefabInstance prefabInstance : prefabInstances){
            this.prefabManager.loadEntityFromPrefabInstance(prefabInstance);
        }

        // 3. load Entities from bakedInstances
        for (BakedEntity bakedEntity : bakedEntities){
            this.loadEntityFromBakedEntity(bakedEntity);
        }

        // #4 infer Entity signatures from Component collections and update them
        //TODO: rename to loadEntitySignatures ?
        ecm.loadEntitiesFromCollections();


        // #5 load level specific data to global variable?
        // load spawn positions:
        this.defaultSpawnPoint = level.defaultSpawnPointID;
        this.spawnPointIDtoPosition = level.validSpawnPoints;
        // load Player?
//        Vector2 playerSpawnPosition = this.spawnPointIDtoPosition.get(defaultSpawnPoint);
//        int playerID = entityComponentManager.createPlayer(playerSpawnPosition);
//        EventBus.getInstance().publish(new PlayerIDChanged(playerID));
        this.currentLevelID = levelID;
    }

    public void loadPlayer(){
        Vector2 defaultSpawnPointPosition = this.spawnPointIDtoPosition.get(defaultSpawnPoint);
        Vector2 playerSpawnPosition = defaultSpawnPointPosition.cpy(); // avoid "pass by ref" bugs
        this.playerID = ecm.createPlayer(playerSpawnPosition);
        triggerPlayerDataUpdate();
    }

    public void triggerPlayerDataUpdate(){
        // Update existing player-oriented systems about player character's ID and Health change
        EventBus.getInstance().publish(new PlayerIDChanged(this.playerID));
        HealthComponent playerHealth = ecm.getComponent(HealthComponent.class, playerID);
        EventBus.getInstance().publish(new PlayerHpChanged(playerHealth.currentHp,playerHealth.maxHp));
    }

    public void saveLevel (int levelID){
        Level level = new Level();
        level.levelID = levelID;
        // #1 save entities into the level

        // 1.1 get all entities with prefabComponents
        long signature = ComponentSignatures.get(PrefabComponent.class);
        int[] prefabEntities = ecm.getEntitiesMatchingSignature(signature);
        // and save them to prefabInstances
        for (int entityID : prefabEntities){
            Optional<PrefabInstance> prefabInstanceOptional = this.prefabManager.createPrefabInstanceFromEntity(entityID);
            if (prefabInstanceOptional.isPresent()){
                level.prefabInstances.add(prefabInstanceOptional.get());
            }
        }

        //TODO: Omit player entity

        /*
        // 1.2 get all entities without prefabComponents
        int[] allEntities = ecm.getAllEntities();
        Set<Integer> prefabEntitiesSet = new HashSet<>();
        for (int i : prefabEntities) {
            prefabEntitiesSet.add(i);
        }
        int[] nonPrefabEntities = Arrays.stream(allEntities)
            .filter(i -> !prefabEntitiesSet.contains(i))
            .toArray();
        // and save them to BakedEntities
        for (int entityID : nonPrefabEntities){
            HashMap<Class<?>, Object> entityComponents = ecm.getEntityComponents(entityID);
            BakedEntity bakedEntity = new BakedEntity(entityID, entityComponents);
            level.bakedEntities.add(bakedEntity);
        }
        */

        // #2 save global variables that can change from level to level
        level.validSpawnPoints = this.spawnPointIDtoPosition;
        level.defaultSpawnPointID = this.defaultSpawnPoint;
        // #3 serialize level and save it
        fileSystem.saveLevel(level);
    }

    public void saveCurrentLevel(){
        // don't save if
        if (currentLevelID < 0){
            System.out.println("can't save current level, invalid Id:" + currentLevelID);
            return;
        }
        this.saveLevel(this.currentLevelID);
    }


    public void changeCurrentLevel(int newLevelID){
        // #1 publish an Event "changing levels"
        EventBus.getInstance().publish(new LoadingNewLevelEvent(currentLevelID, newLevelID));
        // #1 unload current level?
        // #2 load new level
        loadLevel(newLevelID);
        // #3 load Player
        loadPlayer();
        // #4 publish an Event "finished loading level"
        EventBus.getInstance().publish(new FinishedLoadingLevelEvent());
    }

    public int getCurrentLevelID(){
       return currentLevelID;
    }

    public void handleTeleportEvent(TeleportPlayerEvent event){
        this.teleportEventToResolve = event;
//        int newLevelID = event.targetLevelID;
//        changeCurrentLevel(newLevelID);
    }
}
