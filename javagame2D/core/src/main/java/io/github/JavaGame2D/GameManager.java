package io.github.JavaGame2D;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

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

        Texture playerSprite = new Texture("playerSprite.png");
        float scale = (float)playerSprite.getHeight()/playerSprite.getWidth();
        DrawableComponent drawableComponent = new DrawableComponent(playerSprite);
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.height = 2*scale;
        transformComponent.width = 2;
        transformComponent.position = new Vector2(10f,50f);
        PhysicalBodyComponent bodyComponent = new PhysicalBodyComponent();
        bodyComponent.usesGravity = true;

        player.addComponent(drawableComponent);
        player.addComponent(transformComponent);
        player.addComponent(bodyComponent);
        return player;
    }
}
