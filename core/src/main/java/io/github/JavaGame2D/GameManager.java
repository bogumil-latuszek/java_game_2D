package io.github.JavaGame2D;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
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
    OrthographicCamera camera;
    Vector2 screenSizeInGameUnits = new Vector2(14f, 9.8f);

    public GameManager(GameMode mode){
        gameMode = mode;
        System.out.println("starting game in mode: "+gameMode);
    }


    @Override
    public void create() {

        initializeSharedSystems();

        //legacyCreateLevels();

        levelManager.loadLevel(1);

        setScreen(new PlayScreen(this));
    }


    private void initializeSharedSystems(){
        gameSettings = new GameSettings();
        gameSettings = gameSettings.loadSettings();
        gameSettings.saveSettings();
        camera = new OrthographicCamera();

        fileSystem = new FileSystem();
        entityComponentManager = new EntityComponentManager();

        renderingSystem = new RenderingSystem(entityComponentManager, camera);
        physicsSystem = new PhysicsSystem(entityComponentManager, gameSettings);
        levelManager = new LevelManager(fileSystem,entityComponentManager);
    }

    private void legacyCreateLevels(){
        // TODO: add migrations so that this method becomes a fallback option, not the main way to update levels after major changes to component structure
        // LEVEL 1
        entityComponentManager.createPlatform(0f,    -2.5f, 5f,   1f);
        entityComponentManager.createPlatform(5f,    1.125f,2.5f, 2.5f);
        entityComponentManager.createPlatform(10f,   2.5f,  5f,   2.5f);
        entityComponentManager.createPlatform(12.5f, 2.5f,  1.25f,10f);
        entityComponentManager.createPlatform(17.5f, 2.5f,  1.25f,17.5f);
        entityComponentManager.createPlatform(17.5f, -10f,  12.5f,1.25f);
        entityComponentManager.createPlatform(-6f,   -5f,   30f,  0.5f);
        entityComponentManager.createSpikes(7f, -4f,  2f,2f);
        entityComponentManager.createSpikes(5f, -4f,  2f,2f);
        entityComponentManager.createSpikes(3f, -4f,  2f,2f);
        entityComponentManager.createSpikes(1f, -4f,  2f,2f);
        entityComponentManager.createDestructibleCrate(-7f, -4f, 2f,2f);
        entityComponentManager.createDestructibleCrate(-7f, -2f, 2f,2f);
        entityComponentManager.createDestructibleCrate(-9f, -4f, 2f,2f);
        entityComponentManager.createPlatform(20f,   -6f,   2.5f, 0.5f);
        entityComponentManager.createPlatform(22.5f, -2f,   2.5f, 0.5f);
        entityComponentManager.createPlatform(20f,   2f,    2.5f, 0.5f);
        entityComponentManager.createPlatform(22.5f, 6f,    2.5f, 0.5f);
        entityComponentManager.createPlatform(20f,   10f,   2.5f, 0.5f);
        entityComponentManager.createPlatform(22.5f, 14f,   2.5f, 0.5f);
        entityComponentManager.createPlatform(17.5f, 18f,   5f,   0.5f);
        entityComponentManager.createPlatform(17.5f, 22f,   2.5f, 0.5f);
        entityComponentManager.createPlatform(17.5f, 26f,   1.25f,0.5f);
        entityComponentManager.createTeleporter(17.5f, 27.5f, 2f,2f, 2);
        levelManager.saveLevel(1);

        // LEVEL 2

        entityComponentManager.createPlatform(5f,2f,15f,10f);
        entityComponentManager.createPlatform(35f, 0f, 40f,20f);
        entityComponentManager.createPlatform(30f, 5f, 10f,16f);
        entityComponentManager.createPlatform(40f, 14.5f, 5f,5f);
        entityComponentManager.createPlatform(50f, 12.5f, 10f,15f);
        entityComponentManager.createPlatform(65f, 15f, 30f,15f);
        entityComponentManager.createPlatform(65f, 35f, 30f,15f);
        entityComponentManager.createPlatform(82.5f, 14f, 5f,15f);
        entityComponentManager.createPlatform(87.5f, 12.5f, 5f,15f);
        entityComponentManager.createPlatform(92.5f, 13.5f, 5f,15f);
        entityComponentManager.createPlatform(97.5f, 12f, 5f,15f);
        entityComponentManager.createPlatform(87.5f, 35.5f, 5f,15f);
        entityComponentManager.createPlatform(97.5f, 32f, 5f,15f);
        entityComponentManager.createPlatform(107.5f, 18f, 5f,10f);
        entityComponentManager.createPlatform(112.5f, 37.5f, 5f,15f);
        entityComponentManager.createPlatform(117.5f, 20f, 3f,5f);
        entityComponentManager.createTeleporter(117.5f, 24f, 2f,2f, 3);
        levelManager.spawnPointIDtoPosition.put(0,new Vector2(5,10));
        levelManager.defaultSpawnPoint = 0;
        levelManager.saveLevel(2);

        // LEVEL 3

        entityComponentManager.createPlatform(10f, 25f, 20f,5f);
        entityComponentManager.createPlatform(10f, 5f, 20f,5f);
        entityComponentManager.createPlatform(0f, 15f, 5f,30f);
        entityComponentManager.createPlatform(20f, 15f, 5f,30f);
        entityComponentManager.createPlatform(10f, 11f, 2f,0.5f);
        entityComponentManager.createBackgroundElement(10f, 15f, 10f,10f);
        levelManager.spawnPointIDtoPosition.put(0,new Vector2(10,16));
        levelManager.defaultSpawnPoint = 0;
        levelManager.saveLevel(3);
    }
}
