package io.github.JavaGame2D;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Systems.EntityManager;
import io.github.JavaGame2D.Systems.RenderingSystem;
import io.github.JavaGame2D.Systems.UserInterface;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameManager extends ApplicationAdapter {
    RenderingSystem renderingSystem;
    EntityManager entityManager;
    UserInterface userInterface;
    Long previousTimeframe;



    @Override
    public void create() {
        // initialize systems
        entityManager = new EntityManager();

        renderingSystem = new RenderingSystem(entityManager);
        userInterface = new UserInterface();
        previousTimeframe = System.currentTimeMillis();

        createPlayer();
    }

    // this function may be called "render"
    // but this is actually one frame of our main loop
    @Override
    public void render() {
        Long currentTime = System.currentTimeMillis();
        float deltaTime = (float)(currentTime - previousTimeframe);
        previousTimeframe = currentTime;

        userInterface.update(deltaTime);
        //inputSystem.update();
        //physics2DSystem.update();

        renderingSystem.render();
        userInterface.render();
    }

    @Override
    public void dispose() {
        renderingSystem.dispose();
        userInterface.dispose();
    }


    private void createPlayer(){
        Entity player = entityManager.createEntity();

        Texture playerSprite = new Texture("playerSprite.png");
        float scale = (float)playerSprite.getHeight()/playerSprite.getWidth();
        DrawableComponent drawableComponent = new DrawableComponent(playerSprite);
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.height = 100*scale;
        transformComponent.width = 100;
        transformComponent.position = new Vector2(10f,10f);

        player.addComponent(drawableComponent);
        player.addComponent(transformComponent);
    }
}
