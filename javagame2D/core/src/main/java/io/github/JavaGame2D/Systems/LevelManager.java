package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Level;

public class LevelManager {
    private Level currentLevel;
    private FileSystem fileSystem;
    private EntityManager entityManager;

    public LevelManager(FileSystem fileSystem, EntityManager entityManager) {
        this.fileSystem = fileSystem;
        this.entityManager = entityManager;
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
}
