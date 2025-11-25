package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ComponentType;

public class RenderingSystem {
    private SpriteBatch batch;
    private Texture image;
    private EntityComponentManager entityComponentManager;
    private int signature;
    private StalkingCamera stalkingCamera;
    private Texture missingTexture;

    public RenderingSystem(EntityComponentManager entityComponentManager){
        image = new Texture("libgdx.png");
        batch = new SpriteBatch();
        this.entityComponentManager = entityComponentManager;
        stalkingCamera = new StalkingCamera();
        missingTexture = new Texture("autumnBrick1.png");
    }

    public void loadTextures(){
        DrawableComponent[] drawableComponents = entityComponentManager.getAllDrawableComponents();
        for (DrawableComponent drawable : drawableComponents){
            if (drawable.sprite == null && drawable.texturePath != null) {
                drawable.sprite = new Texture(drawable.texturePath);
            }
        }
    }

    public void setEntityFollowedByCamera(int entityID){
        TransformComponent transformComponent = entityComponentManager.getTransformComponent(entityID);
        this.stalkingCamera.followTransformComponent(transformComponent);
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

        long signature = ComponentSignatures.TRANSFORM & ComponentSignatures.DRAWABLE;
        int[] drawableEntities = entityComponentManager.getEntitiesMatchingSignature(signature);

        // draw each entity in list:
        for ( int entityID : drawableEntities){
            TransformComponent transformComponent = entityComponentManager.getTransformComponent(entityID);
            DrawableComponent drawableComponent = entityComponentManager.getDrawableComponent(entityID);
            Texture sprite = drawableComponent.sprite != null ? drawableComponent.sprite : missingTexture;

            float centerX = transformComponent.position.x;
            float centerY = transformComponent.position.y;
            float width = transformComponent.width;
            float height = transformComponent.height;
            // this system needs x,y coords of the lower left corner, not center!
            Vector2 lowerLeftCorner = new Vector2(centerX-width/2, centerY-height/2);
            batch.draw(sprite, lowerLeftCorner.x, lowerLeftCorner.y, width, height);
        }
        batch.end();
    }

    public void resizeViewport(int width, int height){
        this.stalkingCamera.resizeViewport(width, height);
    }

    public void dispose(){
        batch.dispose();
        image.dispose();
    }
}
