package io.github.JavaGame2D;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.JavaGame2D.Components.DrawableComponent;
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
        renderingSystem = new RenderingSystem();
        entityManager = new EntityManager();
        userInterface = new UserInterface();
        previousTimeframe = System.currentTimeMillis();
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
}
