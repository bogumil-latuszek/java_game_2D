package io.github.JavaGame2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private Skin uiskin;

    private EditorCameraController cameraController;
    private EntitySelector entitySelector;
    private PropertyInspector propertyInspector;
    private EditorInputProcessor inputProcessor;

    private Table rootLayout;
    private Table inspectorPanel;
    private Table leftPanel;
    private Table topLeftPanel;
    private Table bottomLeftPanel;
    private Table gameWorld;
    private Table toolPicker;

    public EditScreen(GameManager gameManager) {
        this.gameManager = gameManager;

        this.renderingSystem = gameManager.renderingSystem;
        this.levelManager = gameManager.levelManager;
        this.entityComponentManager = gameManager.entityComponentManager;
        this.camera = gameManager.camera;
        this.screenSizeInGameUnits = gameManager.screenSizeInGameUnits;

        // 1. Set up the Editor UI Stage
        editorStage = new Stage(new ScreenViewport());

        uiskin = new Skin(Gdx.files.internal("ui/uiskin.json"));

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
        leftPanel = new Table();

        topLeftPanel = new Table();
        topLeftPanel.setBackground(skin.getDrawable("default-rect")); // optional
        topLeftPanel.setHeight(100);

        TextButton saveButton = new TextButton("Save Level", skin);
        saveButton.setHeight(40);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showSaveConfirmationDialog();
            }
        });
        topLeftPanel.add(saveButton).left();


        bottomLeftPanel = new Table();

        leftPanel.add(topLeftPanel).fillX().height(50);
        leftPanel.row();
        leftPanel.add(bottomLeftPanel).expand().fill();

        gameWorld = new Table();

        toolPicker = new Table();
        toolPicker.setBackground(skin.getDrawable("default-rect"));
        toolPicker.setWidth(200);

        bottomLeftPanel.add(toolPicker).width(200).fillY();
        bottomLeftPanel.add(gameWorld).expand().fillY();

        //leftPanel.setBackground(uiskin.getDrawable("default-rect")); // optional

        // Right side: The Property Inspector
        propertyInspector = new PropertyInspector(this.editorStage, entityComponentManager, skin);
        inspectorPanel = new Table();
        inspectorPanel.setBackground(skin.getDrawable("default-rect")); // optional
        inspectorPanel.add(propertyInspector.getActor()).expand().fill();
        inspectorPanel.setWidth(260); // Fixed width for the inspector

        // Add panels to the root layout
        rootLayout.add(leftPanel).expand().fill(); // Left takes all remaining space
        rootLayout.add(inspectorPanel).width(260).fillY(); // Right is 260px wide



        return rootLayout;
    }

    private void showSaveConfirmationDialog() {
        Dialog saveDialog = new Dialog("Save Level", uiskin) {
            @Override
            protected void result(Object object) {
                // 'true' is returned if "Yes" is clicked, 'false' for "No"
                if ((boolean) object) {
                    levelManager.saveCurrentLevel();
                    // show a small toast/notification that save succeeded
                    System.out.println("Level saved successfully!");
                } else {
                    System.out.println("Save cancelled.");
                }
            }
        };

        saveDialog.text("Are you sure you want to save this level?");

        // Adding buttons. The second parameter is the 'Object' passed to result()
        saveDialog.button("Yes", true);
        saveDialog.button("No", false);

        // Show the dialog and make it modal (blocks input to the rest of the Stage)
        saveDialog.show(editorStage);
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
