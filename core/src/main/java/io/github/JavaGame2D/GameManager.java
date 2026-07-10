package io.github.JavaGame2D;

import com.badlogic.gdx.Game;
import io.github.JavaGame2D.Enums.GameMode;
import io.github.JavaGame2D.Systems.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameManager extends Game {

    GameMode gameMode;

    RenderingSystem renderingSystem;
    PhysicsSystem physicsSystem;
    GameSettings gameSettings;
    LevelManager levelManager;
    EntityComponentManager entityComponentManager;
    FileSystem fileSystem;

    public GameManager(GameMode mode){
        gameMode = mode;
        System.out.println("starting game in mode: "+gameMode);
    }


    @Override
    public void create() {

        initializeSharedSystems();

        levelManager.loadLevel(1);

        setScreen(new PlayScreen(this));
    }


    private void initializeSharedSystems(){
        gameSettings = new GameSettings();
        gameSettings = gameSettings.loadSettings();
        gameSettings.saveSettings();

        fileSystem = new FileSystem();
        entityComponentManager = new EntityComponentManager();

        renderingSystem = new RenderingSystem(entityComponentManager);
        physicsSystem = new PhysicsSystem(entityComponentManager, gameSettings);
        levelManager = new LevelManager(fileSystem,entityComponentManager);
    }
}
