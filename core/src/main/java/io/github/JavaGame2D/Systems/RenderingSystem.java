package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.SpriteData;

import java.util.OptionalInt;

public class RenderingSystem {
    private SpriteBatch worldBatch;
    private EntityComponentManager entityComponentManager;
    private TextureManager textureManager;

    private OptionalInt selectedEntityID ;


    ShapeRenderer shapeRenderer; //used for drawing outlines in Edit mode


    private OrthographicCamera camera;

    public RenderingSystem(EntityComponentManager entityComponentManager, OrthographicCamera camera, TextureManager textureManager){
        worldBatch = new SpriteBatch();
        this.entityComponentManager = entityComponentManager;
        this.textureManager = textureManager;
        this.camera = camera;

        shapeRenderer = new ShapeRenderer();
        selectedEntityID = OptionalInt.empty();
    }

    public void setSelectedEntityID(OptionalInt selectedEntityID){
        this.selectedEntityID = selectedEntityID;
    }

    public void renderGameWorld(){
        this.renderGameWorld(false, false, false);
    }

    public void renderGameWorld(boolean drawSpriteOutlines, boolean drawColliders, boolean highlightSelectedEntity){

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

        // draw Sprite outlines
        if (drawSpriteOutlines){
            shapeRenderer.setProjectionMatrix(this.getProjectionMatrix(this.camera));
            this.drawSpriteOutlines(drawableEntities, shapeRenderer, Color.BLUE, false);
        }

        // draw Colliders
        if (drawColliders){

            long entitiesWithCollidersSignature = ComponentSignatures.TRANSFORM | ComponentSignatures.COLLIDER;
            int[] entitiesWithColliders = entityComponentManager.getEntitiesMatchingSignature(entitiesWithCollidersSignature);

            shapeRenderer.setProjectionMatrix(this.getProjectionMatrix(this.camera)); // is this an unncecessary duplicate?
            // Enable blending (if not already on)
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            this.drawColliders(entitiesWithColliders, shapeRenderer);
        }

        // draw Selected Entity outline
        if (highlightSelectedEntity && selectedEntityID.isPresent()){
            this.highlightSelectedEntity(selectedEntityID.getAsInt(), shapeRenderer, Color.RED, true);
        }
    }

    private void highlightSelectedEntity(int selectedEntityID, ShapeRenderer renderer, Color color, boolean fill){
        long drawableSignature = ComponentSignatures.TRANSFORM | ComponentSignatures.DRAWABLE;
        Entity selected = entityComponentManager.getEntity(selectedEntityID);
        boolean isDrawable = ( (selected.signature & drawableSignature) == drawableSignature );

        // draw sprite outline if possible
        if (isDrawable){
            TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class,selectedEntityID);
            DrawableComponent drawable = entityComponentManager.getComponent(DrawableComponent.class,selectedEntityID);
            SpriteData spriteData = drawable.spriteData;
            Vector2 center = transform.position;

            if (drawable.hasSegmentedBody){
                SegmentedDrawableComponent segmentedDrawable = entityComponentManager.getComponent(SegmentedDrawableComponent.class, selectedEntityID);
                for (SpriteData sprite : segmentedDrawable.drawableSegments.values()){
                    this.highlightSprite(renderer, sprite, center, color, fill);
                }
                return;
            }
            this.highlightSprite(renderer, spriteData, center, color, fill);
        }
        else{
            // otherwise, draw collider outline
            Long colliderSignature = ComponentSignatures.TRANSFORM | ComponentSignatures.COLLIDER;
            boolean hasCollider = ( (selected.signature & colliderSignature) == colliderSignature );
            if (hasCollider){
                TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class,selectedEntityID);
                ColliderComponent collider = entityComponentManager.getComponent(ColliderComponent.class,selectedEntityID);
                Vector2 center = transform.position;
                Vector2 offsetCenter = center.add(collider.offset);
                float width = collider.width;
                float height = collider.height;
                this.drawHighlightRectangle(renderer, offsetCenter, width, height, color, fill);
            }
            else {
                // since it has no collider or drawable, there's no way to outline it
                // maybe render a red dot?
            }
        }
    }

    private void highlightSprite(ShapeRenderer renderer, SpriteData sprite, Vector2 center, Color color, boolean fill){
        float width = sprite.width;
        float height = sprite.height;

        if (sprite.usesSizeFromTexture && !sprite.textureIsTiled){
            float pixelsPerUnit = 32f; //TODO: this should be System-wide variable loaded from settings
            int textureID = sprite.textureID;
            Texture texture = textureManager.getTexture(textureID);
            int texturePixelHeight = texture.getHeight();
            int texturePixelWidth = texture.getWidth();
            float textureUnitHeight = ((float)texturePixelHeight)/ pixelsPerUnit;
            float textureUnitWidth = ((float)texturePixelWidth)/ pixelsPerUnit;
            width = textureUnitWidth;
            height = textureUnitHeight;
        }
        Vector2 offsetCenter = center.add(sprite.offset);

        this.drawHighlightRectangle(renderer, offsetCenter, width, height, color, fill);

    }

    private void drawHighlightRectangle(ShapeRenderer renderer, Vector2 center, float width, float height, Color color, boolean fill){
        // this system needs x,y coords of the lower left corner, not center!
        Vector2 lowerLeftCorner = new Vector2(center.x-width/2, center.y-height/2);

        // Draw semi-transparent rectangle
        if (fill){
            Color halfTransparentColor = new Color();
            halfTransparentColor.set(color); // Color is a reference type so we need to copy values not reference
            halfTransparentColor.a = 0.5f;
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(halfTransparentColor);   // yellow, 50% opaque
            renderer.rect(lowerLeftCorner.x, lowerLeftCorner.y, width, height);
            renderer.end();
        }

        // Draw fully opaque outline
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(color);
        renderer.rect(lowerLeftCorner.x, lowerLeftCorner.y, width, height);
        renderer.end();
    }

    private void drawColliders(int[] entitiesWithColliders, ShapeRenderer renderer){
        for ( int entityID : entitiesWithColliders){
            TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class,entityID);
            ColliderComponent collider = entityComponentManager.getComponent(ColliderComponent.class, entityID);

            Vector2 center = transform.position;
            float width = collider.width;
            float height = collider.height;

            this.drawHighlightRectangle(renderer,center,width,height,Color.YELLOW,true);
        }
    }

    private void drawSpriteOutlines(int[] drawableEntities, ShapeRenderer renderer, Color color, boolean fill){
        // outline each entity in list:
        for ( int entityID : drawableEntities){
            TransformComponent transformComponent = entityComponentManager.getComponent(TransformComponent.class,entityID);
            DrawableComponent drawableComponent = entityComponentManager.getComponent(DrawableComponent.class, entityID);
            Vector2 center = transformComponent.position;
            SpriteData spriteData = drawableComponent.spriteData;

            if (drawableComponent.hasSegmentedBody){
                SegmentedDrawableComponent segmentedDrawable = entityComponentManager.getComponent(SegmentedDrawableComponent.class, entityID);
                for (SpriteData sprite : segmentedDrawable.drawableSegments.values()){
                    this.highlightSprite(renderer, sprite, center, color, fill);
                }
                continue;
            }
            this.highlightSprite(renderer, spriteData, center, color, fill);
        }
    }

    private void drawSegments(SpriteBatch spriteBatch, SegmentedDrawableComponent segmentedDrawable, Vector2 center){
        for (SpriteData segment: segmentedDrawable.drawableSegments.values()){
            drawSprite(spriteBatch, segment, center);
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
            if (sprite.usesSizeFromTexture){
                drawTexture(spriteBatch, texture, 32f, sprite.offset, center, mirrorHorizontal, mirrorVertical);
            }
            else{
                drawStretchingTexture(spriteBatch, texture, sprite.width, sprite.height, sprite.offset, center, mirrorHorizontal, mirrorVertical);
            }
        }
    }

    private void drawStretchingTexture(SpriteBatch spriteBatch, Texture texture, float width, float height, Vector2 offset, Vector2 center, boolean mirrorHorizontal, boolean mirrorVertical){


        float offsetHeightInUnits = offset.y;
        float offsetWidthInUnits = offset.x;

        if (mirrorHorizontal){
            offsetHeightInUnits = -offsetHeightInUnits;
        }
        if (mirrorVertical){
            offsetWidthInUnits = -offsetWidthInUnits;
        }

        Vector2 offsetCenter = new Vector2(center.x+offsetWidthInUnits, center.y+offsetHeightInUnits);
        //SpriteBatch.draw() draws from lower left corner, so correction is needed:
        Vector2 lowerLeftCorner = new Vector2(offsetCenter.x-width/2, center.y-height/2);

        spriteBatch.draw(texture, lowerLeftCorner.x, lowerLeftCorner.y, width, height);
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
        shapeRenderer.dispose();
        // also any textures, fonts, sounds you loaded here
    }

    public Matrix4 getProjectionMatrix(OrthographicCamera orthographicCamera){
        return orthographicCamera.combined;
    }
}
