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



    @Override
    public void create() {
        // initialize systems
        entityManager = new EntityManager();

        renderingSystem = new RenderingSystem(entityManager);
        physicsSystem = new PhysicsSystem(entityManager);
        userInterface = new UserInterface();
        inputSystem = new InputSystem();
        playerCharacterController = new PlayerCharacterController();

        Entity player = createPlayer();
        playerCharacterController.setPlayerCharacter(player);
        renderingSystem.setEntityFollowedByCamera(player);

        Entity platform1 = createPlatform(new Vector2(0f,-10f),20f,5f);
        Entity platform2 = createPlatform(new Vector2(20f, 5f), 10f,10f);
        Entity platform3 = createPlatform(new Vector2(40f, 10f), 20f,10f);
        Entity platform4 = createPlatform(new Vector2(50f, 10f), 5f,40f);
        Entity platform5 = createPlatform(new Vector2(70f, 10f), 5f,70f);
        Entity platform6 = createPlatform(new Vector2(70f, -40f), 50f,5f);
        Entity platform7 = createPlatform(new Vector2(20f, -25f), 50f,5f);
        Entity platform8 = createPlatform(new Vector2(80f, -25f), 10f,5f);
        Entity platform9 = createPlatform(new Vector2(90f, -5f), 10f,5f);
        Entity platform10 = createPlatform(new Vector2(80f, 15f), 10f,5f);
        Entity platform11 = createPlatform(new Vector2(90f, 35f), 10f,5f);
        Entity platform12 = createPlatform(new Vector2(80f, 55f), 10f,5f);
        Entity platform13 = createPlatform(new Vector2(90f, 75f), 10f,5f);
        Entity platform14 = createPlatform(new Vector2(70f, 95f), 20f,5f);
        Entity platform15 = createPlatform(new Vector2(70f, 115f), 10f,5f);
        Entity platform16 = createPlatform(new Vector2(70f, 135f), 5f,5f);

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

        inputSystem.update();
        userInterface.update(deltaTime);
        physicsSystem.update(deltaTimeInSeconds);

        renderingSystem.render();
        userInterface.render();
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
        transformComponent.height = 4*scale;
        transformComponent.width = 4;
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
