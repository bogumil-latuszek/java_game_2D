package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerIDChanged;

import java.util.HashMap;

public class RenderingSystem {
    private SpriteBatch batch;
    private Texture image;
    private EntityComponentManager entityComponentManager;
    private int signature;
    private StalkingCamera stalkingCamera;
    private Texture missingTexture;
    private HashMap<Integer, Texture> textureIDtoTexture;
    private HashMap<Integer, String> textureIDtoTexturePath;

    public RenderingSystem(EntityComponentManager entityComponentManager){
        image = new Texture("libgdx.png");
        batch = new SpriteBatch();
        this.entityComponentManager = entityComponentManager;
        stalkingCamera = new StalkingCamera();
        EventBus.getInstance().subscribe(PlayerIDChanged.class, this::handlePlayerIDChanged);
        textureIDtoTexture = new HashMap<>();
        textureIDtoTexturePath = new HashMap<>();
        missingTexture = new Texture("missingTexture.png");
        loadTextureIDToTexturePathMapping();
    }

    public void loadTextureIDToTexturePathMapping(){
        textureIDtoTexturePath.put(0,"autumnBrick1.png");
        textureIDtoTexturePath.put(1,"playerSprite.png");
        textureIDtoTexturePath.put(2,"teleporter.png");
        textureIDtoTexturePath.put(3, "YOU_WIN!!!.png");
    }

    public void handlePlayerIDChanged(PlayerIDChanged event){
        setEntityFollowedByCamera(event.playerEntityID);
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
        return new Texture(texturePath);
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

        //draw drawable entities

        long signature = ComponentSignatures.TRANSFORM | ComponentSignatures.DRAWABLE;
        int[] drawableEntities = entityComponentManager.getEntitiesMatchingSignature(signature);

        // draw each entity in list:
        for ( int entityID : drawableEntities){
            TransformComponent transformComponent = entityComponentManager.getTransformComponent(entityID);
            DrawableComponent drawableComponent = entityComponentManager.getDrawableComponent(entityID);

            int textureID = drawableComponent.textureID;
            Texture texture = getTexture(textureID);

            float centerX = transformComponent.position.x;
            float centerY = transformComponent.position.y;
            float width = transformComponent.width;
            float height = transformComponent.height;
            // this system needs x,y coords of the lower left corner, not center!
            Vector2 lowerLeftCorner = new Vector2(centerX-width/2, centerY-height/2);
            batch.draw(texture, lowerLeftCorner.x, lowerLeftCorner.y, width, height);
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
