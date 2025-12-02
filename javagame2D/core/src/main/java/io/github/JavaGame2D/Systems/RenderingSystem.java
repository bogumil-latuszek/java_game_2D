package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private SpriteBatch worldBatch;
    private SpriteBatch userInterfaceBatch;
    private EntityComponentManager entityComponentManager;
    private int signature;
    private StalkingCamera stalkingCamera;
    private OrthographicCamera uiCamera;
    private Texture missingTexture;
    private HashMap<Integer, Texture> textureIDtoTexture;
    private HashMap<Integer, String> textureIDtoTexturePath;
    private UserInterface userInterface;
    private Vector2 cameraViewSizeInGameUnits = new Vector2(14f, 9.8f);

    public RenderingSystem(EntityComponentManager entityComponentManager, UserInterface userInterface){
        worldBatch = new SpriteBatch();
        userInterfaceBatch = new SpriteBatch();
        this.entityComponentManager = entityComponentManager;
        stalkingCamera = new StalkingCamera(cameraViewSizeInGameUnits.x, cameraViewSizeInGameUnits.y);
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, cameraViewSizeInGameUnits.x, cameraViewSizeInGameUnits.y);
        EventBus.getInstance().subscribe(PlayerIDChanged.class, this::handlePlayerIDChanged);
        textureIDtoTexture = new HashMap<>();
        textureIDtoTexturePath = new HashMap<>();
        missingTexture = new Texture("missingTexture.png");
        loadTextureIDToTexturePathMapping();
        this.userInterface = userInterface;
    }

    public void loadTextureIDToTexturePathMapping(){
        textureIDtoTexturePath.put(0,"autumnBrick1.png");
        textureIDtoTexturePath.put(1,"playerSprite.png");
        textureIDtoTexturePath.put(2,"teleporter.png");
        textureIDtoTexturePath.put(3, "YOU_WIN!!!.png");
        textureIDtoTexturePath.put(4, "spikes.png");
        textureIDtoTexturePath.put(5, "health_bar.png");
        textureIDtoTexturePath.put(6, "health_bar_frame.png");
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
        TransformComponent transformComponent = entityComponentManager.getComponent(TransformComponent.class,entityID);
        this.stalkingCamera.followTransformComponent(transformComponent);
    }

    public void render(){
        //clear screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        stalkingCamera.update();
        worldBatch.setProjectionMatrix(stalkingCamera.getProjectionMatrix());

        worldBatch.begin();

        //draw background

        //draw drawable entities

        long signature = ComponentSignatures.TRANSFORM | ComponentSignatures.DRAWABLE;
        int[] drawableEntities = entityComponentManager.getEntitiesMatchingSignature(signature);

        // draw each entity in list:
        for ( int entityID : drawableEntities){
            TransformComponent transformComponent = entityComponentManager.getComponent(TransformComponent.class,entityID);
            DrawableComponent drawableComponent = entityComponentManager.getComponent(DrawableComponent.class, entityID);

            int textureID = drawableComponent.textureID;
            Texture texture = getTexture(textureID);

            float centerX = transformComponent.position.x;
            float centerY = transformComponent.position.y;
            float width = transformComponent.width;
            float height = transformComponent.height;
            // this system needs x,y coords of the lower left corner, not center!
            Vector2 lowerLeftCorner = new Vector2(centerX-width/2, centerY-height/2);
            worldBatch.draw(texture, lowerLeftCorner.x, lowerLeftCorner.y, width, height);
        }

        worldBatch.end();

        //draw User Interface
        userInterfaceBatch.setProjectionMatrix(uiCamera.combined);
        userInterfaceBatch.begin();
        this.drawHpBar();
        userInterfaceBatch.end();
    }

    public void resizeViewport(int width, int height){
        this.stalkingCamera.resizeViewport(width, height);
    }

    public void dispose(){
        worldBatch.dispose();
        userInterfaceBatch.dispose();
    }

    private void drawHpBar(){
        HpBar hpBar = this.userInterface.hpBar;
        float current_width = hpBar.width * ((float)hpBar.currentSize/hpBar.maxSize);
        float height = hpBar.height;
        Vector2 position = hpBar.position;
        Vector2 lowerLeftCorner = new Vector2(position.x, position.y);

        int hp_bar_id = hpBar.hp_bar_id;
        Texture barTexture = getTexture(hp_bar_id);
        this.userInterfaceBatch.draw(barTexture, lowerLeftCorner.x, lowerLeftCorner.y, current_width, height);

        int hp_bar_frame_id = hpBar.hp_bar_frame_id;
        Texture frameTexture = getTexture(hp_bar_frame_id);
        this.userInterfaceBatch.draw(frameTexture, lowerLeftCorner.x, lowerLeftCorner.y, hpBar.width, height);
    }
}
