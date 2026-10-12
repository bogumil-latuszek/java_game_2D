package io.github.JavaGame2D;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.Enums.GameMode;
import io.github.JavaGame2D.SaveData.ComponentFullNameMapper;
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
    EntityComponentManager ecm;
    FileSystem fileSystem;
    ComponentFullNameMapper componentMapper;
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
        componentMapper = new ComponentFullNameMapper("io.github.JavaGame2D.Components");
        fileSystem = new FileSystem(componentMapper);
        gameSettings = fileSystem.loadGameSettings();
        ecm = new EntityComponentManager();
        textureManager = new TextureManager();

        renderingSystem = new RenderingSystem(ecm, camera, textureManager);
        physicsSystem = new PhysicsSystem(ecm, gameSettings);
        prefabManager = new PrefabManager(fileSystem, ecm, componentMapper);
        levelManager = new LevelManager(fileSystem, ecm, prefabManager);
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
        int entityID = ecm.createEmptyEntity();

        TransformComponent transformComponent = new TransformComponent();
        ecm.addComponent(TransformComponent.class, transformComponent, entityID);

        PhysicalBodyComponent body = new PhysicalBodyComponent();
        body.dynamic = false;
        body.usesGravity = false;
        ecm.addComponent(PhysicalBodyComponent.class, body, entityID);

        DrawableComponent drawable = new DrawableComponent();
        drawable.textureID = 0;
        drawable.textureIsTiled = true;
        drawable.width = 1;
        drawable.height = 1;
        ecm.addComponent(DrawableComponent.class, drawable, entityID);

        ColliderComponent collider = new ColliderComponent();
        collider.width = 1;
        collider.height = 1;
        ecm.addComponent(ColliderComponent.class, collider, entityID);

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
