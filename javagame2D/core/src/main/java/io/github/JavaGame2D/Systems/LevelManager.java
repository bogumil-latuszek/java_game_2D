package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Level;

public class LevelManager {
    private Level currentLevel;
    private FileSystem fileSystem;

    public LevelManager(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    // level manager can load level when given its ID
    // it uses file manager to load appropriate assets

    public void loadLevel (String levelName){
       currentLevel = fileSystem.loadLevel(levelName);
    }

    public void saveLevel (){
        fileSystem.saveLevel(currentLevel);
    }

    public void changeCurrentLevel(Level level){
        currentLevel = level;
    }
}
