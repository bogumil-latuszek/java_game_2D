package io.github.JavaGame2D;

import com.badlogic.gdx.ApplicationAdapter;
import io.github.JavaGame2D.Events.PlayerIDChanged;
import io.github.JavaGame2D.Systems.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameManager extends ApplicationAdapter {
    RenderingSystem renderingSystem;
    PhysicsSystem physicsSystem;
    UserInterface userInterface;
    InputSystem inputSystem;
    PlayerCharacterController playerCharacterController;
    Long previousTimeframe;
    GameSettings gameSettings;
    LevelManager levelManager;
    EntityComponentManager entityComponentManager;



    @Override
    public void create() {

        //TODO: change to static method
        gameSettings = new GameSettings();
        gameSettings = gameSettings.loadSettings();
        gameSettings.saveSettings();

        FileSystem fileSystem = new FileSystem();
        // initialize systems
        entityComponentManager = new EntityComponentManager();

        renderingSystem = new RenderingSystem(entityComponentManager);
        physicsSystem = new PhysicsSystem(entityComponentManager, gameSettings);
        userInterface = new UserInterface();
        inputSystem = new InputSystem();
        playerCharacterController = new PlayerCharacterController(entityComponentManager);
        levelManager = new LevelManager(fileSystem,entityComponentManager);



        // LEVEL 1
//        entityComponentManager.createPlatform(0f,    -2.5f, 5f,   1f);
//        entityComponentManager.createPlatform(5f,    1.125f,2.5f, 2.5f);
//        entityComponentManager.createPlatform(10f,   2.5f,  5f,   2.5f);
//        entityComponentManager.createPlatform(12.5f, 2.5f,  1.25f,10f);
//        entityComponentManager.createPlatform(17.5f, 2.5f,  1.25f,17.5f);
//        entityComponentManager.createPlatform(17.5f, -10f,  12.5f,1.25f);
//        entityComponentManager.createPlatform(-6f,   -5f,   30f,  0.5f);
//        entityComponentManager.createPlatform(20f,   -6f,   2.5f, 0.5f);
//        entityComponentManager.createPlatform(22.5f, -2f,   2.5f, 0.5f);
//        entityComponentManager.createPlatform(20f,   2f,    2.5f, 0.5f);
//        entityComponentManager.createPlatform(22.5f, 6f,    2.5f, 0.5f);
//        entityComponentManager.createPlatform(20f,   10f,   2.5f, 0.5f);
//        entityComponentManager.createPlatform(22.5f, 14f,   2.5f, 0.5f);
//        entityComponentManager.createPlatform(17.5f, 18f,   5f,   0.5f);
//        entityComponentManager.createPlatform(17.5f, 22f,   2.5f, 0.5f);
//        entityComponentManager.createPlatform(17.5f, 26f,   1.25f,0.5f);
//        entityComponentManager.createTeleporter(17.5f, 27.5f, 2f,2f, 2);
//        levelManager.saveLevel(1);

//        // LEVEL 2
//
//        entityComponentManager.createPlatform(5f,2f,15f,10f);
//        entityComponentManager.createPlatform(35f, 0f, 40f,20f);
//        entityComponentManager.createPlatform(30f, 5f, 10f,16f);
//        entityComponentManager.createPlatform(40f, 14.5f, 5f,5f);
//        entityComponentManager.createPlatform(50f, 12.5f, 10f,15f);
//        entityComponentManager.createPlatform(65f, 15f, 30f,15f);
//        entityComponentManager.createPlatform(65f, 35f, 30f,15f);
//        entityComponentManager.createPlatform(82.5f, 14f, 5f,15f);
//        entityComponentManager.createPlatform(87.5f, 12.5f, 5f,15f);
//        entityComponentManager.createPlatform(92.5f, 13.5f, 5f,15f);
//        entityComponentManager.createPlatform(97.5f, 12f, 5f,15f);
//        entityComponentManager.createPlatform(87.5f, 35.5f, 5f,15f);
//        entityComponentManager.createPlatform(97.5f, 32f, 5f,15f);
//        entityComponentManager.createPlatform(107.5f, 18f, 5f,10f);
//        entityComponentManager.createPlatform(112.5f, 37.5f, 5f,15f);
//        entityComponentManager.createPlatform(117.5f, 20f, 3f,5f);
//        entityComponentManager.createTeleporter(117.5f, 24f, 2f,2f, 3);
//
//        levelManager.saveLevel(2);

//         LEVEL 3
//
//        entityComponentManager.createPlatform(10f, 25f, 20f,5f);
//        entityComponentManager.createPlatform(10f, 5f, 20f,5f);
//        entityComponentManager.createPlatform(0f, 15f, 5f,30f);
//        entityComponentManager.createPlatform(20f, 15f, 5f,30f);
//        entityComponentManager.createBackgroundElement(10f, 15f, 10f,10f);
//        levelManager.saveLevel(3);

        levelManager.loadLevel(1);

//        int playerID = entityComponentManager.createPlayer();
//        EventBus.getInstance().publish(new PlayerIDChanged(playerID));

        previousTimeframe = System.currentTimeMillis();
    }

    // this function may be called "render"
    // but this is actually one frame of our main loop
    @Override
    public void render() {
        Long currentTime = System.currentTimeMillis();
        float deltaTime = (float)(currentTime - previousTimeframe);
        // TODO: make delta time uses consistent across the system!
        float deltaTimeInSeconds = deltaTime/1000;
        previousTimeframe = currentTime;

        inputSystem.update(deltaTimeInSeconds);
        userInterface.update(deltaTime);
        physicsSystem.update(deltaTimeInSeconds);

        renderingSystem.render();
        userInterface.render();

        levelManager.loadLevelIfChanged();
    }

    @Override
    public void resize(int width, int height) {
        this.renderingSystem.resizeViewport(width, height);
        System.out.println("Resize called: " + width + "x" + height); // Debug
    }

    @Override
    public void dispose() {
        renderingSystem.dispose();
        userInterface.dispose();
    }
}
