package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ComponentType;

public class RenderingSystem {
    private SpriteBatch batch;
    private Texture image;
    private EntityManager entityManager;
    private StalkingCamera stalkingCamera;

    public RenderingSystem(EntityManager entityManager){
        image = new Texture("libgdx.png");
        batch = new SpriteBatch();
        this.entityManager = entityManager;
        stalkingCamera = new StalkingCamera();
    }

    public void setEntityFollowedByCamera(Entity entity){
        this.stalkingCamera.followEntity(entity);
    }

    public void render(){
        //clear screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        stalkingCamera.update();
        batch.setProjectionMatrix(stalkingCamera.getProjectionMatrix());

        batch.begin();

        //draw background
        batch.draw(image, -30, 30, 60, 60*(float)image.getHeight()/image.getWidth());
        batch.draw(image, 0, 0, 60, 60*(float)image.getHeight()/image.getWidth());
        batch.draw(image, 30, -30, 60, 60*(float)image.getHeight()/image.getWidth());
        //draw drawable entities

        // get list of drawable entities from EntityManager
        ComponentType[] requiredComponents = {ComponentType.DRAWABLE,
                                            ComponentType.TRANSFORM};
        Entity[] drawableEntities = entityManager.getMatchingEntities(requiredComponents);
        // draw each entity in list:
        for (Entity entity : drawableEntities){
            DrawableComponent drawableComponent = (DrawableComponent)entity.getComponent(ComponentType.DRAWABLE);
            TransformComponent transformComponent = (TransformComponent) entity.getComponent(ComponentType.TRANSFORM);
            Texture sprite = drawableComponent.getSprite();
            Vector2 position = transformComponent.position;
            float width = transformComponent.width;
            float height = transformComponent.height;
            batch.draw(sprite, position.x, position.y, width, height);
        }
        batch.end();
    }

    public void dispose(){
        batch.dispose();
        image.dispose();
    }
}
