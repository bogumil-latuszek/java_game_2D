package io.github.JavaGame2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
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
    private PrefabManager prefabManager;

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
    private Window dropdown; //TODO: rename to prefabPicker

    public EditScreen(GameManager gameManager) {
        this.gameManager = gameManager;

        this.renderingSystem = gameManager.renderingSystem;
        this.levelManager = gameManager.levelManager;
        this.entityComponentManager = gameManager.entityComponentManager;
        this.camera = gameManager.camera;
        this.screenSizeInGameUnits = gameManager.screenSizeInGameUnits;
        this.prefabManager = gameManager.prefabManager;

        // 1. Set up the Editor UI Stage
        editorStage = new Stage(new ScreenViewport());

        uiskin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Add the root layout to the stage
        Actor editorUI = createEditorUI(uiskin);
        editorStage.addActor(editorUI);

        // Create the camera controller
        cameraController = new EditorCameraController(camera, screenSizeInGameUnits.x, screenSizeInGameUnits.y);

        entitySelector = new EntitySelector(gameManager.entityComponentManager, gameManager.textureManager );
        inputProcessor = new EditorInputProcessor(entityComponentManager, entitySelector, propertyInspector, camera, this);
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

    public void showPrefabDropdown(float worldX, float worldY) {
        Array<String> prefabNames = prefabManager.getAllPrefabNames();

        if (prefabNames.size == 0) {
            showToast("No prefabs found in assets/prefabs/");
            return;
        }

        // Build the list of buttons.
        Table content = new Table();
        content.top().defaults().pad(2).fillX();
        for (String name : prefabNames) {
            TextButton btn = new TextButton(name, uiskin);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //prefabManager.placePrefab(name, worldX, worldY);
                    prefabManager.createEntityFromPrefab(name, new Vector2(worldX, worldY));
                    closePrefabDropdown();
                }
            });
            content.add(btn).row();
        }

        // Scroll pane caps the height for very long lists.
        ScrollPane scroll = new ScrollPane(content, uiskin);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);

        // Build the window.
        dropdown = new Window("Pick a Prefab", uiskin);
        dropdown.setModal(true);
        dropdown.setMovable(false);
        dropdown.setResizable(false);
        dropdown.defaults().pad(4);
        dropdown.add(scroll).width(220).maxHeight(400);
        dropdown.pack();

        // Position at cursor. Scene2D Y is bottom-up; input Y is top-down.
        int stageX = Gdx.input.getX();
        int stageY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Clamp to screen bounds so it never goes off-screen.
        float dx = Math.min(stageX, Gdx.graphics.getWidth() - dropdown.getWidth());
        float dy = Math.max(0, stageY - dropdown.getHeight());
        dropdown.setPosition(dx, dy);

        // Escape closes.
        dropdown.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    closePrefabDropdown();
                    return true;
                }
                return false;
            }
        });

        editorStage.addActor(dropdown);
        editorStage.setKeyboardFocus(dropdown); // capture keyboard for Escape
    }

    private void closePrefabDropdown() {
        if (dropdown != null) {
            dropdown.remove();
            dropdown = null;
            editorStage.setKeyboardFocus(null);
        }
    }

    private void showToast(String message) {
        final Dialog toast = new Dialog("", uiskin);
        toast.text(message);
        toast.pad(20);
        toast.pack();
        toast.setPosition(
            (Gdx.graphics.getWidth()  - toast.getWidth())  / 2f,
            Gdx.graphics.getHeight() * 0.1f
        );
        editorStage.addActor(toast);

        // Auto-dismiss after 2 seconds via an Action.
        toast.addAction(Actions.sequence(
            Actions.delay(2f),
            Actions.fadeOut(0.3f),
            Actions.removeActor()
        ));
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
        renderingSystem.renderGameWorld(true, true, true, false);

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
