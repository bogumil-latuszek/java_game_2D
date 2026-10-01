package io.github.JavaGame2D;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.Enums.GameMode;
import io.github.JavaGame2D.Systems.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameManager extends Game {

    GameMode gameMode;

    RenderingSystem renderingSystem;
    TextureManager textureManager;
    PhysicsSystem physicsSystem;
    GameSettings gameSettings;
    LevelManager levelManager;
    PrefabManager prefabManager;
    EntityComponentManager entityComponentManager;
    FileSystem fileSystem;
    OrthographicCamera camera;
    Vector2 screenSizeInGameUnits = new Vector2(14f, 9.8f);

    RuntimeDataOverlay runtimeDataOverlay;

    public GameManager(GameMode mode){
        gameMode = mode;
        System.out.println("starting game in mode: "+gameMode);
    }


    @Override
    public void create() {

        initializeSharedSystems();

        Skin uiskin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        runtimeDataOverlay = new RuntimeDataOverlay(uiskin);

        //createWallPrefab();
        //createPrefabLevel();

        levelManager.changeCurrentLevel(5);

        switch (gameMode){
            case STANDARD_GAMEPLAY:
                setScreen(new PlayScreen(this));
                break;
            case LEVEL_EDIT:
                setScreen(new EditScreen(this));
                break;
        }
    }


    private void initializeSharedSystems(){
        camera = new OrthographicCamera();

        fileSystem = new FileSystem();
        gameSettings = fileSystem.loadGameSettings();
        entityComponentManager = new EntityComponentManager();
        textureManager = new TextureManager();

        renderingSystem = new RenderingSystem(entityComponentManager, camera, textureManager);
        physicsSystem = new PhysicsSystem(entityComponentManager, gameSettings);
        prefabManager = new PrefabManager(fileSystem, entityComponentManager);
        levelManager = new LevelManager(fileSystem,entityComponentManager, prefabManager);
    }

    public void switchToDifferentGameMode(GameMode gameMode){
        switch (gameMode){
            case LEVEL_EDIT:
                this.setScreen(new EditScreen(this));
                break;
            case STANDARD_GAMEPLAY:
                this.setScreen(new PlayScreen(this));
                break;
            default:
                break;
        }
    }

    private void createWallPrefab(){
        int entityID = entityComponentManager.createEntity(new Vector2());

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        entityComponentManager.addComponent(PhysicalBodyComponent.class, body, entityID);

        DrawableComponent drawable = new DrawableComponent();
        drawable.spriteData.textureID = 0;
        drawable.spriteData.textureIsTiled = true;
        drawable.spriteData.width = 1;
        drawable.spriteData.height = 1;
        entityComponentManager.addComponent(DrawableComponent.class, drawable, entityID);

        ColliderComponent collider = new ColliderComponent();
        collider.width = 1;
        collider.height = 1;
        entityComponentManager.addComponent(ColliderComponent.class, collider, entityID);

        prefabManager.createPrefabFromEntity(entityID, "wall");
    }


    private void createPrefabLevel(){
        prefabManager.createEntityFromPrefab("wall", new Vector2(0,0));
        prefabManager.createEntityFromPrefab("wall", new Vector2(-2,-2));
        prefabManager.createEntityFromPrefab("wall", new Vector2(2,0));
        prefabManager.createEntityFromPrefab("wall", new Vector2(4,0));
        levelManager.saveLevel(5);
    }
}
