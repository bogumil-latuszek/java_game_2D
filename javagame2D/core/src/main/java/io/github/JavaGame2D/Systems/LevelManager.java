package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.FinishedLoadingLevelEvent;
import io.github.JavaGame2D.Events.LoadingNewLevelEvent;
import io.github.JavaGame2D.Events.PlayerIDChanged;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;
import io.github.JavaGame2D.Level;

import java.util.HashMap;

public class LevelManager {
    public HashMap<Integer,Vector2> spawnPointIDtoPosition;
    public int defaultSpawnPoint;

    //private Level currentLevel;
    private int currentLevelID;
    private FileSystem fileSystem;
    EntityComponentManager entityComponentManager;
    TeleportPlayerEvent teleportEventToResolve;

    public LevelManager(FileSystem fileSystem, EntityComponentManager entityComponentManager) {
        this.fileSystem = fileSystem;
        this.entityComponentManager = entityComponentManager;
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

    public void loadLevel (int levelID){
        // #1 load and deserialize level
        Level level = fileSystem.loadLevel(levelID);
        // #2 load collections from the level
        ComponentCollection<TransformComponent> transformCollection = level.transformCollection;
        ComponentCollection<PhysicalBodyComponent> bodyCollection = level.physicalBodyCollection;
        ComponentCollection<ColliderComponent> colliderCollection = level.colliderCollection;
        ComponentCollection<DrawableComponent> drawableCollection = level.drawableCollection;
        ComponentCollection<TeleporterComponent> teleporterCollection = level.teleporterCollection;
        ComponentCollection<HealthComponent> healthCollection = level.healthCollection;
        entityComponentManager.registerComponentCollection(TransformComponent.class,transformCollection);
        entityComponentManager.registerComponentCollection(DrawableComponent.class,drawableCollection);
        entityComponentManager.registerComponentCollection(PhysicalBodyComponent.class,bodyCollection);
        entityComponentManager.registerComponentCollection(ColliderComponent.class,colliderCollection);
        entityComponentManager.registerComponentCollection(TeleporterComponent.class,teleporterCollection);
        entityComponentManager.registerComponentCollection(HealthComponent.class,healthCollection);

        // #3 infer Entites from collections and save them in EntityManager
        entityComponentManager.loadEntitiesFromCollections();
        // #4 load level specific data to global variable?
        // load spawn positions:
        this.defaultSpawnPoint = level.defaultSpawnPointID;
        this.spawnPointIDtoPosition = level.validSpawnPoints;
        // load Player?
        Vector2 playerSpawnPosition = this.spawnPointIDtoPosition.get(defaultSpawnPoint);
        int playerID = entityComponentManager.createPlayer(playerSpawnPosition);
        EventBus.getInstance().publish(new PlayerIDChanged(playerID));
    }

    public void saveLevel (int levelID){
        Level level = new Level();
        level.levelID = levelID;
        // #1 save collections into the level
        level.transformCollection = entityComponentManager.getComponentCollection(TransformComponent.class);
        level.physicalBodyCollection = entityComponentManager.getComponentCollection(PhysicalBodyComponent.class);
        level.colliderCollection = entityComponentManager.getComponentCollection(ColliderComponent.class);
        level.drawableCollection = entityComponentManager.getComponentCollection(DrawableComponent.class);
        level.teleporterCollection = entityComponentManager.getComponentCollection(TeleporterComponent.class);
        level.healthCollection = entityComponentManager.getComponentCollection(HealthComponent.class);
        // #2 save global variables that can change from level to level
        level.validSpawnPoints = this.spawnPointIDtoPosition;
        level.defaultSpawnPointID = this.defaultSpawnPoint;
        // #3 serialize level and save it
        fileSystem.saveLevel(level);
    }


    public void changeCurrentLevel(int newLevelID){
        // #1 publish an Event "changing levels"
        EventBus.getInstance().publish(new LoadingNewLevelEvent(currentLevelID, newLevelID));
        // #1 unload current level?
        // #2 load new level
        loadLevel(newLevelID);
        // #4 publish an Event "finished loading level"
        EventBus.getInstance().publish(new FinishedLoadingLevelEvent());
    }

    public int getCurrentLevelID(){
       return currentLevelID;
    }

    public void handleTeleportEvent(TeleportPlayerEvent event){
        this.teleportEventToResolve = event;
        int newLevelID = event.targetLevelID;
        changeCurrentLevel(newLevelID);
    }
}
