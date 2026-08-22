package io.github.JavaGame2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.JavaGame2D.Enums.GameMode;
import io.github.JavaGame2D.Systems.*;
import io.github.JavaGame2D.UserInterface.PropertyInspector;

import java.util.OptionalInt;

public class EditScreen implements Screen {

    private final GameManager gameManager;

    // shared systems
    private RenderingSystem renderingSystem;
    private LevelManager levelManager;
    private EntityComponentManager entityComponentManager;
    private Vector2 screenSizeInGameUnits;
    private OrthographicCamera camera;

    // edit-only systems
    private Stage editorStage;
    private EditorCameraController cameraController;
    private EntitySelector entitySelector;
    private PropertyInspector propertyInspector;
    private EditorInputProcessor inputProcessor;

    private Table rootLayout;
    private Table inspectorPanel;

    public EditScreen(GameManager gameManager) {
        this.gameManager = gameManager;

        this.renderingSystem = gameManager.renderingSystem;
        this.levelManager = gameManager.levelManager;
        this.entityComponentManager = gameManager.entityComponentManager;
        this.camera = gameManager.camera;
        this.screenSizeInGameUnits = gameManager.screenSizeInGameUnits;

        // 1. Set up the Editor UI Stage
        editorStage = new Stage(new ScreenViewport());

        Skin uiskin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Add the root layout to the stage
        Actor editorUI = createEditorUI(uiskin);
        editorStage.addActor(editorUI);

        // Create the camera controller
        cameraController = new EditorCameraController(camera, screenSizeInGameUnits.x, screenSizeInGameUnits.y);

        entitySelector = new EntitySelector(gameManager.entityComponentManager, gameManager.textureManager );
        inputProcessor = new EditorInputProcessor(entityComponentManager, entitySelector, propertyInspector, camera);
    }

    private Actor createEditorUI(Skin skin){
        // Create a root Table that fills the entire screen
        rootLayout = new Table();
        rootLayout.setFillParent(true);

        // Left side: Empty area for the game world (or toolbar later)
        Table leftPanel = new Table();
        //leftPanel.setBackground(uiskin.getDrawable("default-rect")); // optional

        // Right side: The Property Inspector
        propertyInspector = new PropertyInspector(entityComponentManager, skin);
        inspectorPanel = new Table();
        inspectorPanel.setBackground(skin.getDrawable("default-rect")); // optional
        inspectorPanel.add(propertyInspector.getActor()).expand().fill();
        inspectorPanel.setWidth(260); // Fixed width for the inspector

        // Add panels to the root layout
        rootLayout.add(leftPanel).expand().fill(); // Left takes all remaining space
        rootLayout.add(inspectorPanel).width(260).fillY(); // Right is 260px wide

        return rootLayout;
    }

    @Override
    public void show() {
        // Set input processor (UI first, then game controls)
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(editorStage);
        multiplexer.addProcessor(inputProcessor);
        multiplexer.addProcessor(cameraController);
        Gdx.input.setInputProcessor(multiplexer);
    }

    // this function may be called "render"
    // but this is actually one frame of our main loop
    @Override
    public void render(float delta) {

        // --- SWITCH TO PLAY MODE ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            gameManager.switchToDifferentGameMode(GameMode.STANDARD_GAMEPLAY);
            return; // Stop processing this frame (screen will be replaced)
        }

        // Render Game World
        OptionalInt selectedEntityID = inputProcessor.getSelectedEntityID();
        renderingSystem.setSelectedEntityID(selectedEntityID);
        renderingSystem.renderGameWorld(true, true, true);

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
