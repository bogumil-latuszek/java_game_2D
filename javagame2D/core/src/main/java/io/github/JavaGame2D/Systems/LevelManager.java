package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.ColliderComponentCollection;
import io.github.JavaGame2D.Collections.DrawableComponentsCollection;
import io.github.JavaGame2D.Collections.PhysicalBodyComponentsCollection;
import io.github.JavaGame2D.Collections.TransformComponentsCollection;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.FinishedLoadingLevelEvent;
import io.github.JavaGame2D.Events.LoadingNewLevelEvent;
import io.github.JavaGame2D.Events.PlayerIDChanged;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;
import io.github.JavaGame2D.Level;

import java.util.HashMap;

public class LevelManager {
    //private Level currentLevel;
    private int currentLevelID;
    private FileSystem fileSystem;
    EntityComponentManager entityComponentManager;
    TeleportPlayerEvent teleportEventToResolve;

    public LevelManager(FileSystem fileSystem, EntityComponentManager entityComponentManager) {
        this.fileSystem = fileSystem;
        this.entityComponentManager = entityComponentManager;
        EventBus.getInstance().subscribe(TeleportPlayerEvent.class, this::handleTeleportEvent);
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
        TransformComponentsCollection transformCollection = level.transformCollection;
        PhysicalBodyComponentsCollection bodyCollection = level.physicalBodyCollection;
        ColliderComponentCollection colliderCollection = level.colliderCollection;
        DrawableComponentsCollection drawableCollection = level.drawableCollection;
        entityComponentManager.setTransformComponentCollection(transformCollection);
        entityComponentManager.setPhysicalBodyComponentCollection(bodyCollection);
        entityComponentManager.setColliderComponentCollection(colliderCollection);
        entityComponentManager.setDrawableComponentCollection(drawableCollection);
        // #3 infer Entites from collections and save them in EntityManager
        entityComponentManager.loadEntitiesFromCollections();
        // #4 load level specific data to global variable?
        // load Player?
        int playerID = entityComponentManager.createPlayer();
        EventBus.getInstance().publish(new PlayerIDChanged(playerID));

    }

    public void saveLevel (int levelID){
        Level level = new Level();
        level.levelID = levelID;
        // #1 save collections into the level
        level.transformCollection = entityComponentManager.getTransformCollection();
        level.physicalBodyCollection = entityComponentManager.getPhysicalBodyCollection();
        level.colliderCollection = entityComponentManager.getColliderCollection();
        level.drawableCollection = entityComponentManager.getDrawableCollection();
        // #2 save global variables that can change from level to level
        level.validSpawnPoints = new HashMap<>();
        level.validSpawnPoints.put(0,new Vector2(0,0));
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
        //int newLevelID = event.targetLevelID;
        //changeCurrentLevel(newLevelID);
    }
}
