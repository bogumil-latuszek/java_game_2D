package io.github.JavaGame2D;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import io.github.JavaGame2D.Components.ColliderComponent;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.PhysicalBodyComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Systems.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameManager extends ApplicationAdapter {
    RenderingSystem renderingSystem;
    PhysicsSystem physicsSystem;
    EntityManager entityManager;
    UserInterface userInterface;
    InputSystem inputSystem;
    PlayerCharacterController playerCharacterController;
    Long previousTimeframe;
    GameSettings gameSettings;



    @Override
    public void create() {

        gameSettings = new GameSettings();
        gameSettings = gameSettings.loadSettings();
        gameSettings.saveSettings();

        // initialize systems
        entityManager = new EntityManager();

        renderingSystem = new RenderingSystem(entityManager);
        physicsSystem = new PhysicsSystem(entityManager, gameSettings);
        userInterface = new UserInterface();
        inputSystem = new InputSystem();
        playerCharacterController = new PlayerCharacterController();

        Entity player = createPlayer();
        playerCharacterController.setPlayerCharacter(player);
        renderingSystem.setEntityFollowedByCamera(player);

//        Entity platform1 = createPlatform(new Vector2(0f,-2.5f),5f,1f);
//        Entity platform2 = createPlatform(new Vector2(5f, 1.125f), 2.5f,2.5f);
        Entity platform3 = createPlatform(new Vector2(10f, 2.5f), 5f,2.5f);
//        Entity platform4 = createPlatform(new Vector2(12.5f, 2.5f), 1.25f,10f);
//        Entity platform5 = createPlatform(new Vector2(17.5f, 2.5f), 1.25f,17.5f);
//        Entity platform6 = createPlatform(new Vector2(17.5f, -10f), 12.5f,1.25f);
//        Entity platform7 = createPlatform(new Vector2(-6f, -5f), 30f,0.5f);
//        Entity platform8 = createPlatform(new Vector2(20f, -6f), 2.5f,0.5f);
//        Entity platform9 = createPlatform(new Vector2(22.5f, -2f), 2.5f,0.5f);
//        Entity platform10 = createPlatform(new Vector2(20f, 2f), 2.5f,0.5f);
//        Entity platform11 = createPlatform(new Vector2(22.5f, 6f), 2.5f,0.5f);
//        Entity platform12 = createPlatform(new Vector2(20f, 10f), 2.5f,0.5f);
//        Entity platform13 = createPlatform(new Vector2(22.5f, 14f), 2.5f,0.5f);
//        Entity platform14 = createPlatform(new Vector2(17.5f, 18f), 5f,0.5f);
//        Entity platform15 = createPlatform(new Vector2(17.5f, 22f), 2.5f,0.5f);
//        Entity platform16 = createPlatform(new Vector2(17.5f, 26f), 1.25f,0.5f);

//        Level newLevel = new Level();
//        newLevel.entitiesInside.add(platform1);
//        newLevel.entitiesInside.add(platform2);
//        newLevel.entitiesInside.add(platform3);
//        newLevel.entitiesInside.add(platform4);
//        FileSystem fileSystem = new FileSystem();
//        LevelManager levelManager = new LevelManager(fileSystem);
//        levelManager.changeCurrentLevel(newLevel);
//        levelManager.saveLevel();
//        levelManager.loadLevel("leveldefault");
//          FileSystem.serializeEntityJackson(platform3);
//          FileSystem.serializeComponentJsonWriter(platform3);

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

    private Entity createPlayer(){
        Entity player = entityManager.createEntity();

        //Texture playerSprite = new Texture("playerSprite.png");
        Texture playerSprite = new Texture("playerSprite.png");
        float scale = (float)playerSprite.getHeight()/playerSprite.getWidth();
        DrawableComponent drawableComponent = new DrawableComponent(playerSprite);
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.height = 1*scale;
        transformComponent.width = 1;
        transformComponent.position = new Vector2(10f,50f);
        PhysicalBodyComponent bodyComponent = new PhysicalBodyComponent();
        //bodyComponent.usesGravity = true;
        bodyComponent.usesGravity = true;
        ColliderComponent collider = new ColliderComponent();
        collider.sizeFromTransform = false;
        collider.width = transformComponent.width/2;
        collider.height = transformComponent.height;

        player.addComponent(drawableComponent);
        player.addComponent(transformComponent);
        player.addComponent(bodyComponent);
        player.addComponent(collider);
        return player;
    }

    private Entity createPlatform(Vector2 position, float width, float height){
        Entity platform = entityManager.createEntity();
        Texture texture = new Texture("autumnBrick1.png");
        float scale = (float)texture.getHeight()/texture.getWidth();
        DrawableComponent drawableComponent = new DrawableComponent(texture);
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.position = position;
        transformComponent.height = height*scale;
        transformComponent.width = width;
        PhysicalBodyComponent bodyComponent = new PhysicalBodyComponent();
        bodyComponent.dynamic = false;
        bodyComponent.usesGravity = false;
        ColliderComponent collider = new ColliderComponent();

        platform.addComponent(drawableComponent);
        platform.addComponent(transformComponent);
        platform.addComponent(bodyComponent);
        platform.addComponent(collider);
        return platform;
    }
}
