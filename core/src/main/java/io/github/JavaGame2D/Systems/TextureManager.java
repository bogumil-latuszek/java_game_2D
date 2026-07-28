package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.LoadingNewLevelEvent;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;

import java.util.HashMap;

public class TextureManager {
    private final Texture missingTexture;
    private HashMap<Integer, Texture> loadedTextures;
    private HashMap<Integer, String> registeredTextures;

    public TextureManager(){
        loadedTextures = new HashMap<>();
        registeredTextures = new HashMap<>();
        missingTexture = new Texture("missingTexture.png");
        registerDefaultTextures();
        EventBus.getInstance().subscribe(LoadingNewLevelEvent.class, this::handleLevelChange);
    }

    public void handleLevelChange(LoadingNewLevelEvent event){
        this.unloadAllTextures();
    }

    public void registerTexture(int textureID, String textureFileName){
        this.registeredTextures.put(textureID, textureFileName);
    }

    public Texture getTexture(int textureID){
        if(this.loadedTextures.containsKey(textureID)){
            return loadedTextures.get(textureID);
        }
        if (this.registeredTextures.containsKey(textureID)){
            String texturePath = registeredTextures.get(textureID);
            loadTexture(textureID, texturePath);
            System.out.println("texture:"+ textureID+" loaded");
            return loadedTextures.get(textureID);
        }
        return missingTexture;
    }

    private void loadTexture(int textureID, String texturePath){
        Texture texture = new Texture(texturePath);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        loadedTextures.put(textureID, texture);

    }

    private void unloadAllTextures(){
        this.loadedTextures.clear();
    }

    private void registerDefaultTextures(){
        //TODO: this mapping should be defined in an external file
        registerTexture(0,"tile.png");
        registerTexture(1,"playerSprite.png");
        registerTexture(2,"teleporter.png");
        registerTexture(3, "YOU_WIN!!!.png");
        registerTexture(4, "spikes.png");
        registerTexture(5, "health_bar.png");
        registerTexture(6, "health_bar_frame.png");
        registerTexture(7, "crate.png");
    }
}


