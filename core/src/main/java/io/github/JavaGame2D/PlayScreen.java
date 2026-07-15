package io.github.JavaGame2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.JavaGame2D.Enums.GameMode;
import io.github.JavaGame2D.Systems.*;
import io.github.JavaGame2D.UserInterface.GameHUD;

public class PlayScreen implements Screen {

    private final GameManager gameManager;
    //private Stage inGameHUD; // handles and draws Hp bar, mini-menu, etc.
    private GameHUD inGameHUD;
    private Stage gameOverlay; // handles and draws menu, dialogue, inventory, etc.
    private boolean isPaused = false;

    // shared systems
    private RenderingSystem renderingSystem;
    private PhysicsSystem physicsSystem;
    private GameSettings gameSettings;
    private LevelManager levelManager;
    private EntityComponentManager entityComponentManager;
    private FileSystem fileSystem;
    private OrthographicCamera camera;
    private Vector2 screenSizeInGameUnits;

    // play-only systems
    private PlayerInterface playerInterface;
    private InputSystem inputSystem;
    private PlayerCharacterController playerCharacterController;
    private AnimationSystem animationSystem;
    private DamageSystem damageSystem;
    private PlayerDeathHandler playerDeathHandler;
    private TeleporterSystem teleporterSystem;
    private PlayerCameraController cameraController;


    public PlayScreen(GameManager gameManager){
        this.gameManager = gameManager;

        // get shared systems
        renderingSystem = gameManager.renderingSystem;
        physicsSystem = gameManager.physicsSystem;
        gameSettings = gameManager.gameSettings;
        levelManager = gameManager.levelManager;
        entityComponentManager = gameManager.entityComponentManager;
        fileSystem = gameManager.fileSystem;
        camera = gameManager.camera;
        screenSizeInGameUnits = gameManager.screenSizeInGameUnits;


        // create HUD stage
        // ... add your health bar, score labels ...
        Skin uiskin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        inGameHUD = new GameHUD(uiskin);

        // create overlay stage
        gameOverlay = new Stage(new ScreenViewport());
        // ... add your menu, inventory, dialogue...

        // Instantiate play-only systems
        playerInterface = new PlayerInterface();
        playerCharacterController = new PlayerCharacterController(entityComponentManager);
        animationSystem = new AnimationSystem(entityComponentManager, fileSystem, playerCharacterController);
        inputSystem = new InputSystem();
        damageSystem = new DamageSystem(entityComponentManager);
        playerDeathHandler = new PlayerDeathHandler();
        teleporterSystem = new TeleporterSystem(entityComponentManager);
        cameraController = new PlayerCameraController(camera, screenSizeInGameUnits.x, screenSizeInGameUnits.y, entityComponentManager);


        levelManager.loadLevel(1);

    }

    @Override
    public void show() {
        // Set input processor (UI first, then game controls)
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameOverlay);
        multiplexer.addProcessor(inGameHUD.getStage());
        //multiplexer.addProcessor(gameInputProcessor);// since game doesn't eat the same input as overlay/hud this isn't important FOR NOW
        Gdx.input.setInputProcessor(multiplexer);
    }

    // this function may be called "render"
    // but this is actually one frame of our main loop
    @Override
    public void render(float delta) {

        // --- SWITCH TO EDIT MODE ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            gameManager.switchToDifferentGameMode(GameMode.LEVEL_EDIT);
            return; // Stop processing this frame (screen will be replaced)
        }

        if (!isPaused){
            // update Game World
            inputSystem.update();
            playerCharacterController.update(delta);
            playerInterface.update(delta);

            damageSystem.update(delta);
            physicsSystem.update(delta);
            animationSystem.update(delta);
            cameraController.update();

            playerDeathHandler.update(delta);
        }
        // Render Game World
        renderingSystem.render();
        playerInterface.render();

        // Render HUD
        inGameHUD.update(delta);
        inGameHUD.render();

        // render overlay
        gameOverlay.act(delta);
        gameOverlay.draw();

        // Load new level if changed
        levelManager.loadLevelIfChanged();
    }

    @Override
    public void resize(int width, int height) {
        inGameHUD.resizeViewport(width, height, true);
        gameOverlay.getViewport().update(width, height, true);
        cameraController.resizeViewport(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        // CRITICAL: Remove play-only systems so they don't run in EditMode
        //engine.removeSystem(playerControl);
        //engine.removeSystem(enemyAI);
        Gdx.input.setInputProcessor(null); // prevent input leaks
    }

    @Override
    public void dispose() {
        //TODO: check why this is done
        inGameHUD.dispose();
        gameOverlay.dispose();

        renderingSystem.dispose();
        playerInterface.dispose();
    }
}
