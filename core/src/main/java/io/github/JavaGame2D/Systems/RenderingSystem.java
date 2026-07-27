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
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.SpriteData;

import java.util.HashMap;

public class RenderingSystem {
    private SpriteBatch worldBatch;
    private EntityComponentManager entityComponentManager;
    private int signature;

    private Texture missingTexture;
    private HashMap<Integer, Texture> textureIDtoTexture;
    private HashMap<Integer, String> textureIDtoTexturePath;

    private OrthographicCamera camera;

    public RenderingSystem(EntityComponentManager entityComponentManager, OrthographicCamera camera){
        worldBatch = new SpriteBatch();
        this.entityComponentManager = entityComponentManager;
        textureIDtoTexture = new HashMap<>();
        textureIDtoTexturePath = new HashMap<>();
        missingTexture = new Texture("missingTexture.png");
        loadTextureIDToTexturePathMapping();
        this.camera = camera;
    }

    public void loadTextureIDToTexturePathMapping(){
        textureIDtoTexturePath.put(0,"tile.png");
        textureIDtoTexturePath.put(1,"playerSprite.png");
        textureIDtoTexturePath.put(2,"teleporter.png");
        textureIDtoTexturePath.put(3, "YOU_WIN!!!.png");
        textureIDtoTexturePath.put(4, "spikes.png");
        textureIDtoTexturePath.put(5, "health_bar.png");
        textureIDtoTexturePath.put(6, "health_bar_frame.png");
        textureIDtoTexturePath.put(7, "crate.png");
    }

    public Texture getTexture(int textureID){
        if(this.textureIDtoTexture.containsKey(textureID)){
            return textureIDtoTexture.get(textureID);
        }
        if(this.textureIDtoTexturePath.containsKey(textureID)){
            String texturePath = textureIDtoTexturePath.get(textureID);
            System.out.println("texture:"+ textureID+" loaded");
            Texture textureLoaded = loadTexture(texturePath);
            textureIDtoTexture.put(textureID, textureLoaded);
            return textureLoaded;
        }
        return missingTexture;
    }

    public Texture loadTexture(String texturePath){
        Texture texture = new Texture(texturePath);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
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

            if (drawableComponent.drawableSegments.isEmpty()){
                int textureID = drawableComponent.textureID;
                Texture texture = getTexture(textureID);
                float centerX = transformComponent.position.x;
                float centerY = transformComponent.position.y;
                float width = transformComponent.width;
                float height = transformComponent.height;
                // this system needs x,y coords of the lower left corner, not center!
                Vector2 lowerLeftCorner = new Vector2(centerX-width/2, centerY-height/2);

                if (drawableComponent.tiledTexture){
                    texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                    texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

                    float ppu = 32f; // pixel per world unit
                    int totalPixelWidth = (int)(width * ppu);
                    int totalPixelHeight = (int)(height * ppu);

                    TextureRegion tiledRegion = new TextureRegion(texture);
                    tiledRegion.setRegion(0, 0, totalPixelWidth, totalPixelHeight);

                    worldBatch.draw(tiledRegion,lowerLeftCorner.x, lowerLeftCorner.y, width, height);
                }
                else{
                    worldBatch.draw(texture, lowerLeftCorner.x, lowerLeftCorner.y, width, height);
                }
            }
            else{
                for (Drawable segment: drawableComponent.drawableSegments.values()){
//                    float centerX = transformComponent.position.x;
//                    float centerY = transformComponent.position.y;
                    Vector2 center = transformComponent.position;
                    float width = transformComponent.width;
                    float height = transformComponent.height;
                    // this system needs x,y coords of the lower left corner, not center!
                   // Vector2 lowerLeftCorner = new Vector2(centerX-width/2, centerY-height/2);
                    if (!segment.usesTransformWidth && !segment.usesTransformHeight){
                        drawDrawable(worldBatch, segment, center);
                    }
                    else{
                        drawDrawable(worldBatch, segment, center, width, height);
                    }
                }
            }
        }
        worldBatch.end();
    }

    private void drawDrawable(SpriteBatch spriteBatch, Drawable drawable, Vector2 center, float width, float height){
        Texture texture = drawable.texture;
        if (texture == null){
            texture = missingTexture;
        }

        int texturePixelHeight = texture.getHeight();
        int texturePixelWidth = texture.getWidth();
        float offsetHeightInUnits = spriteData.offset.y/ spriteData.pixelsPerUnit;
        float offsetWidthInUnits = spriteData.offset.x/ spriteData.pixelsPerUnit;
        Vector2 offsetCenter = new Vector2(center.x+offsetWidthInUnits, center.y+offsetHeightInUnits);
        //SpriteBatch.draw() draws from lower left corner, so correction is needed:
        Vector2 lowerLeftCorner = new Vector2(offsetCenter.x-width/2, center.y-height/2);

        boolean mirrorVertical = spriteData.mirrorVertical;
        boolean mirrorHorizontal = spriteData.mirrorHorizontal;
        //draw (Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY)
        spriteBatch.draw(texture, lowerLeftCorner.x, lowerLeftCorner.y, width, height, 0, 0, texturePixelWidth, texturePixelHeight, mirrorVertical, mirrorHorizontal);
       // spriteBatch.draw(texture, position.x, position.y, width, height);
    }

    private void drawDrawable(SpriteBatch spriteBatch, Drawable drawable, Vector2 center){
        Texture texture = drawable.texture;
        if (texture == null){
            texture = missingTexture;
        }

        int texturePixelHeight = texture.getHeight();
        int texturePixelWidth = texture.getWidth();
        float textureUnitHeight = ((float)texturePixelHeight)/ spriteData.pixelsPerUnit;
        float textureUnitWidth = ((float)texturePixelWidth)/ spriteData.pixelsPerUnit;
        float offsetHeightInUnits = spriteData.offset.y/ spriteData.pixelsPerUnit;
        float offsetWidthInUnits = spriteData.offset.x/ spriteData.pixelsPerUnit;

        boolean mirrorVertical = spriteData.mirrorVertical;
        boolean mirrorHorizontal = spriteData.mirrorHorizontal;
        if (mirrorHorizontal){
            offsetHeightInUnits = -offsetHeightInUnits;
        }
        if (mirrorVertical){
            offsetWidthInUnits = -offsetWidthInUnits;
        }

        Vector2 offsetCenter = new Vector2(center.x+offsetWidthInUnits, center.y+offsetHeightInUnits);
        //SpriteBatch.draw() draws from lower left corner, so correction is needed:
        Vector2 lowerLeftCorner = new Vector2(offsetCenter.x-textureUnitWidth/2, center.y-textureUnitHeight/2);


        //draw (Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY)
        spriteBatch.draw(spriteData.texture, lowerLeftCorner.x, lowerLeftCorner.y, textureUnitWidth, textureUnitHeight, 0, 0, texturePixelWidth, texturePixelHeight, mirrorVertical, mirrorHorizontal);
        // spriteBatch.draw(texture, position.x, position.y, width, height);
    }

    public void dispose(){
        worldBatch.dispose();
    }

    public Matrix4 getProjectionMatrix(OrthographicCamera orthographicCamera){
        return orthographicCamera.combined;
    }

//    private void drawHpBar(){
//        HpBar hpBar = this.userInterface.hpBar;
//        float current_width = hpBar.width * ((float)hpBar.currentSize/hpBar.maxSize);
//        float height = hpBar.height;
//        Vector2 position = hpBar.position;
//        Vector2 lowerLeftCorner = new Vector2(position.x, position.y);
//
//        int hp_bar_id = hpBar.hp_bar_id;
//        Texture barTexture = getTexture(hp_bar_id);
//        this.userInterfaceBatch.draw(barTexture, lowerLeftCorner.x, lowerLeftCorner.y, current_width, height);
//
//        int hp_bar_frame_id = hpBar.hp_bar_frame_id;
//        Texture frameTexture = getTexture(hp_bar_frame_id);
//        this.userInterfaceBatch.draw(frameTexture, lowerLeftCorner.x, lowerLeftCorner.y, hpBar.width, height);
//    }
}
