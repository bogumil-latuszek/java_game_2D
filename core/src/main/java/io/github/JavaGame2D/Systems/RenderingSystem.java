package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.SegmentedDrawableComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.SpriteData;

import java.util.HashMap;

public class RenderingSystem {
    private SpriteBatch worldBatch;
    private EntityComponentManager entityComponentManager;
    private TextureManager textureManager;



    private OrthographicCamera camera;

    public RenderingSystem(EntityComponentManager entityComponentManager, OrthographicCamera camera, TextureManager textureManager){
        worldBatch = new SpriteBatch();
        this.entityComponentManager = entityComponentManager;
        this.textureManager = textureManager;
        this.camera = camera;
    }

    public void renderGameWorld(){

        boolean drawSpriteOutlines = false;
        boolean drawColliderOutlines = false;

        //clear screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        worldBatch.setProjectionMatrix(this.getProjectionMatrix(this.camera));

        worldBatch.begin();

        //draw background

        //draw drawable entities

        long signature = ComponentSignatures.TRANSFORM | ComponentSignatures.DRAWABLE;
        int[] drawableEntities = entityComponentManager.getEntitiesMatchingSignature(signature);

        // draw each entity in list:
        for ( int entityID : drawableEntities){
            TransformComponent transformComponent = entityComponentManager.getComponent(TransformComponent.class,entityID);
            DrawableComponent drawableComponent = entityComponentManager.getComponent(DrawableComponent.class, entityID);

            if (drawableComponent.hasSegmentedBody){
                SegmentedDrawableComponent segmentedDrawable = entityComponentManager.getComponent(SegmentedDrawableComponent.class, entityID);
                // used for big bosses, player, some enemies, etc.
                this.drawSegments(worldBatch, segmentedDrawable, transformComponent.position);
            }
            else{
                // used for platforms, simple enemies, items, obstacles, background setpieces, etc.
                this.drawSprite(worldBatch, drawableComponent.spriteData, transformComponent.position);
            }
        }
        worldBatch.end();
    }

    private void drawSegments(SpriteBatch spriteBatch, SegmentedDrawableComponent segmentedDrawable, Vector2 center){
        for (SpriteData segment: segmentedDrawable.drawableSegments.values()){
            drawSprite(worldBatch, segment, center);
        }
    }

    private void drawSprite(SpriteBatch spriteBatch, SpriteData sprite, Vector2 center){
        this.drawSprite(spriteBatch, sprite, center, false);
    }

    private void drawSprite(SpriteBatch spriteBatch, SpriteData sprite, Vector2 center, boolean drawOutline){
        int textureID = sprite.textureID;
        boolean mirrorVertical = sprite.mirrorVertical;
        boolean mirrorHorizontal = sprite.mirrorHorizontal;

        Texture texture = textureManager.getTexture(textureID);

        // this system needs x,y coords of the lower left corner, not center!
        Vector2 lowerLeftCorner = new Vector2(center.x-sprite.width/2, center.y-sprite.height/2);

        // draw Sprite's texture
        if (sprite.textureIsTiled){
            drawTiledTexture(spriteBatch, texture, 32f, sprite.width, sprite.height, lowerLeftCorner);
        }
        else{
            drawTexture(spriteBatch, texture, 32f, sprite.offset, center, mirrorHorizontal, mirrorVertical);
            //spriteBatch.draw(texture, lowerLeftCorner.x, lowerLeftCorner.y, sprite.width, sprite.height);
        }

        // *optional: draw texture outline (used in edit mode)
        if (drawOutline){
            // draw outline
        }
    }

    private void drawTiledTexture(SpriteBatch spriteBatch, Texture texture, float pixelsPerWorldUnit,  float width, float height, Vector2 lowerLeftCorner){
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        int totalPixelWidth = (int)(width * pixelsPerWorldUnit);
        int totalPixelHeight = (int)(height * pixelsPerWorldUnit);

        TextureRegion tiledRegion = new TextureRegion(texture);
        tiledRegion.setRegion(0, 0, totalPixelWidth, totalPixelHeight);

        // TODO: check how to add mirroring
        spriteBatch.draw(tiledRegion,lowerLeftCorner.x, lowerLeftCorner.y, width, height);
    }

    private void drawTexture(SpriteBatch spriteBatch, Texture texture, float pixelsPerUnit, Vector2 offset, Vector2 center, boolean mirrorHorizontal, boolean mirrorVertical){

        int texturePixelHeight = texture.getHeight();
        int texturePixelWidth = texture.getWidth();
        float textureUnitHeight = ((float)texturePixelHeight)/ pixelsPerUnit;
        float textureUnitWidth = ((float)texturePixelWidth)/ pixelsPerUnit;
        float offsetHeightInUnits = offset.y/ pixelsPerUnit;
        float offsetWidthInUnits = offset.x/ pixelsPerUnit;

        if (mirrorHorizontal){
            offsetHeightInUnits = -offsetHeightInUnits;
        }
        if (mirrorVertical){
            offsetWidthInUnits = -offsetWidthInUnits;
        }

        Vector2 offsetCenter = new Vector2(center.x+offsetWidthInUnits, center.y+offsetHeightInUnits);
        //SpriteBatch.draw() draws from lower left corner, so correction is needed:
        Vector2 lowerLeftCorner = new Vector2(offsetCenter.x-textureUnitWidth/2, center.y-textureUnitHeight/2);

        spriteBatch.draw(texture, lowerLeftCorner.x, lowerLeftCorner.y, textureUnitWidth, textureUnitHeight, 0, 0, texturePixelWidth, texturePixelHeight, mirrorVertical, mirrorHorizontal);
    }

    public void dispose(){
        worldBatch.dispose();
    }

    public Matrix4 getProjectionMatrix(OrthographicCamera orthographicCamera){
        return orthographicCamera.combined;
    }
}
