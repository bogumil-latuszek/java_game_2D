package io.github.JavaGame2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.JavaGame2D.Enums.GameMode;
import io.github.JavaGame2D.Systems.*;

public class PlayScreen implements Screen {

    private final GameManager gameManager;
    private Stage inGameHUD; // handles and draws Hp bar, mini-menu, etc.
    private Stage gameOverlay; // handles and draws menu, dialogue, inventory, etc.
    private boolean isPaused = false;

    private RenderingSystem renderingSystem;
    private PhysicsSystem physicsSystem;
    private GameSettings gameSettings;
    private LevelManager levelManager;
    private EntityComponentManager entityComponentManager;
    private FileSystem fileSystem;

    private PlayerInterface playerInterface;
    private InputSystem inputSystem;
    private PlayerCharacterController playerCharacterController;
    private AnimationSystem animationSystem;
    private DamageSystem damageSystem;
    private PlayerDeathHandler playerDeathHandler;
    private TeleporterSystem teleporterSystem;


    public PlayScreen(GameManager gameManager){
        this.gameManager = gameManager;

        // get shared systems
        renderingSystem = gameManager.renderingSystem;
        physicsSystem = gameManager.physicsSystem;
        gameSettings = gameManager.gameSettings;
        levelManager = gameManager.levelManager;
        entityComponentManager = gameManager.entityComponentManager;
        fileSystem = gameManager.fileSystem;

        // create HUD stage
        inGameHUD = new Stage(new ScreenViewport());
        // ... add your health bar, score labels ...

        // create HUD stage
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

        levelManager.loadLevel(1);

    }

    @Override
    public void show() {
        // Set input processor (UI first, then game controls)
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameOverlay);
        multiplexer.addProcessor(inGameHUD);
        //multiplexer.addProcessor(gameInputProcessor);// since game doesn't eat the same input as overlay/hud this isn't important FOR NOW
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        if (!isPaused){
            // update Game World
            inputSystem.update();
            playerCharacterController.update(delta);
            playerInterface.update(delta);

            damageSystem.update(delta);
            physicsSystem.update(delta);
            animationSystem.update(delta);

            playerDeathHandler.update(delta);
        }
        // Render Game World
        renderingSystem.render();
        playerInterface.render();

        // Render HUD
        inGameHUD.act(delta);
        inGameHUD.draw();

        // render overlay
        gameOverlay.act(delta);
        gameOverlay.draw();

        // Load new level if changed
        levelManager.loadLevelIfChanged();
    }

    @Override
    public void resize(int width, int height) {
        inGameHUD.getViewport().update(width, height, true);
        gameOverlay.getViewport().update(width, height, true);
        this.renderingSystem.resizeViewport(width, height); //Should it be "this"? I sense a discrepency between Screen and GameManager
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
