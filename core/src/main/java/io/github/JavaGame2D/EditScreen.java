package io.github.JavaGame2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.JavaGame2D.Systems.*;

public class EditScreen implements Screen {

    private final GameManager gameManager;

    // shared systems
    private RenderingSystem renderingSystem;
    //private PhysicsSystem physicsSystem;
    //private GameSettings gameSettings;
    private LevelManager levelManager;
    private EntityComponentManager entityComponentManager;
    private FileSystem fileSystem;
    private Vector2 screenSizeInGameUnits;
    private OrthographicCamera camera;

    // edit-only systems
    private Stage editorStage;
    private EditorCameraController cameraController;

    public EditScreen(GameManager gameManager) {
        this.gameManager = gameManager;

        this.renderingSystem = gameManager.renderingSystem;
        this.levelManager = gameManager.levelManager;
        this.entityComponentManager = gameManager.entityComponentManager;
        this.fileSystem = gameManager.fileSystem;

        this.renderingSystem = gameManager.renderingSystem;
        this.levelManager = gameManager.levelManager;
        this.entityComponentManager = gameManager.entityComponentManager;
        this.fileSystem = gameManager.fileSystem;
        this.camera = gameManager.camera;
        this.screenSizeInGameUnits = gameManager.screenSizeInGameUnits;

        // 1. Set up the Editor UI Stage
        editorStage = new Stage(new ScreenViewport());

        // TEMPORARY: Add a "Back to Play" button
//        TextButton backBtn = new TextButton("Back to Play", getUISkin());
//        backBtn.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new PlayScreen(game));
//            }
//        });
//        Table table = new Table();
//        table.setFillParent(true);
//        table.top().left();
//        table.add(backBtn);
//        editorStage.addActor(table);

        // 2. Create the camera controller
        cameraController = new EditorCameraController(camera, screenSizeInGameUnits.x, screenSizeInGameUnits.y);

        // (Optional) Add any Editor-specific systems here later...
    }

    @Override
    public void show() {
        // Set input processor (UI first, then game controls)
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(editorStage);
        multiplexer.addProcessor(cameraController);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        // Render Game World
        renderingSystem.render();

        // Render Editor UI
        editorStage.act(delta);
        editorStage.draw();

        // Load new level if changed
        levelManager.loadLevelIfChanged();
    }

    @Override
    public void resize(int width, int height) {
        // Update the UI stage viewport
        editorStage.getViewport().update(width, height, true);
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
        // Clear input to prevent leaks
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        editorStage.dispose();
    }
}
