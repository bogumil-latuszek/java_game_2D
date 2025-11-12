package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;
import io.github.JavaGame2D.Level;

public class LevelManager {
    private Level currentLevel;
    private FileSystem fileSystem;
    private EntityManager entityManager;

    public LevelManager(FileSystem fileSystem, EntityManager entityManager) {
        this.fileSystem = fileSystem;
        this.entityManager = entityManager;
        EventBus.getInstance().subscribe(TeleportPlayerEvent.class, this::handleTeleportEvent);
    }

    // level manager can load level when given its ID
    // it uses file manager to load appropriate assets

    public void loadLevel (String levelName){
        currentLevel = fileSystem.loadLevel(levelName);
        currentLevel.registerLevelEntities(entityManager);
    }

    public void saveLevel (){
        fileSystem.saveLevel(currentLevel);
    }

    public void changeCurrentLevel(Level level){
        currentLevel = level;
    }

    public String getCurrentLevelName(){
       return currentLevel.levelName;
    }

    public void handleTeleportEvent(TeleportPlayerEvent event){
        String levelName = event.targetLevelName;
        // unload current level
        currentLevel.deregisterLevelEntities(entityManager);
        // load new level
        loadLevel(levelName);
    }
}
