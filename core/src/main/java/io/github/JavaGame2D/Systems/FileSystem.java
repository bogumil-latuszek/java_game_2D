package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.JavaGame2D.SpriteData;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;
import io.github.JavaGame2D.Enums.FacingDirection;
import io.github.JavaGame2D.GameSettings;
import io.github.JavaGame2D.Level;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

import java.io.File;
import java.io.IOException;

public class FileSystem {

    public GameSettings loadGameSettings(){
        if (!Gdx.files.local("game_settings.json").exists()) {
            GameSettings gameSettings = new GameSettings();
            this.saveGameSettings(gameSettings);
            return gameSettings; // Return defaults if no save file
        }

        String jsonText = Gdx.files.local("game_settings.json").readString();
        return json.fromJson(GameSettings.class, jsonText); // Convert back to object
    }

    public void saveGameSettings(GameSettings gameSettings){
        Json json = new Json();

        json.setTypeName(null);
        json.setUsePrototypes(false);
        json.setIgnoreUnknownFields(true);
        json.setOutputType(JsonWriter.OutputType.json);

        String jsonText = json.toJson(gameSettings, gameSettings.getClass());
        Gdx.files.local("game_settings.json").writeString(jsonText, false);
        Gdx.app.log("Save", "Settings saved!");
    }

    public void saveLevel(Level level){
        ObjectMapper mapper = new ObjectMapper();
        // add special rules for Vector2 class
        mapper.addMixInAnnotations(Vector2.class, Vector2Mixin.class);
        // formats json for better reading
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String path = "levels/"+ level.levelID+".json";
        try{
            mapper.writeValue(new File(path), level);
        } catch (Exception e) {
            Gdx.app.error("JacksonSerializer", "Failed to serialize component: " + level.getClass().getSimpleName(), e);
        }
    }

    public Level loadLevel(int levelID){
        if (!Gdx.files.internal("levels/"+levelID+".json").exists()) {
            return null; // Return defaults if no save file
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.addMixInAnnotations(Vector2.class, Vector2Mixin.class);

            String jsonText = Gdx.files.internal("levels/"+levelID+".json").readString();
            return mapper.readValue(jsonText, Level.class);

        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load level: "+ levelID, e);
            return null;
        }
    }

    public void loadPlayerAnimations(AnimationManager animationManager){
        try {
            String directory = "animations/Player/";
            String fileExtension = ".png";
            Texture idleFrame1Texture = new Texture(directory+"idle1"+fileExtension);
            Texture walkingFrame1Texture = new Texture(directory+"walk1"+fileExtension);
            Texture walkingFrame2Texture = new Texture(directory+"walk2"+fileExtension);
            Texture walkingFrame3Texture = new Texture(directory+"walk3"+fileExtension);
            Texture walkingFrame4Texture = new Texture(directory+"walk4"+fileExtension);
            Texture walkingFrame5Texture = new Texture(directory+"walk5"+fileExtension);
            Texture walkingFrame6Texture = new Texture(directory+"walk6"+fileExtension);
            Texture jumpingFrame1Texture = new Texture(directory+"jumping1"+fileExtension);
            Texture fallingFrame1Texture = new Texture(directory+"falling1"+fileExtension);
            Texture attackFrame1Texture = new Texture(directory+"attack1"+fileExtension);
            SpriteData idleFrame1 = new SpriteData(idleFrame1Texture, FacingDirection.RIGHT);
            SpriteData walkingFrame1 = new SpriteData(walkingFrame1Texture, FacingDirection.RIGHT);
            SpriteData walkingFrame2 = new SpriteData(walkingFrame2Texture, FacingDirection.RIGHT);
            SpriteData walkingFrame3 = new SpriteData(walkingFrame3Texture, FacingDirection.RIGHT);
            SpriteData walkingFrame4 = new SpriteData(walkingFrame4Texture, FacingDirection.RIGHT);
            SpriteData walkingFrame5 = new SpriteData(walkingFrame5Texture, FacingDirection.RIGHT);
            SpriteData walkingFrame6 = new SpriteData(walkingFrame6Texture, FacingDirection.RIGHT);
            SpriteData jumpingFrame1 = new SpriteData(jumpingFrame1Texture, FacingDirection.RIGHT);
            SpriteData fallingFrame1 = new SpriteData(fallingFrame1Texture, FacingDirection.RIGHT);
            SpriteData attackFrame1 = new SpriteData(attackFrame1Texture, FacingDirection.RIGHT);
            attackFrame1.usesTransformWidth = false;
            attackFrame1.usesTransformHeight = false;
            attackFrame1.offset = new Vector2(500, 0);
            Animation idle = new Animation(new SpriteData[]{idleFrame1});
            Animation walking = new Animation(new SpriteData[]{walkingFrame1,walkingFrame2,walkingFrame3,
                                                            walkingFrame4,walkingFrame5,walkingFrame6});
            walking.framesPerSecond = 6;
            Animation jumping = new Animation(new SpriteData[]{jumpingFrame1});
            Animation falling = new Animation(new SpriteData[]{fallingFrame1});
            Animation attack = new Animation(new SpriteData[]{attackFrame1});
            animationManager.addAnimation(CharacterType.PLAYER, BodySegmentType.UPPER_BODY, AnimationType.IDLE, idle);
            animationManager.addAnimation(CharacterType.PLAYER, BodySegmentType.UPPER_BODY, AnimationType.WALKING, walking);
            animationManager.addAnimation(CharacterType.PLAYER, BodySegmentType.UPPER_BODY, AnimationType.JUMPING, jumping);
            animationManager.addAnimation(CharacterType.PLAYER, BodySegmentType.UPPER_BODY, AnimationType.FALLING, falling);
            animationManager.addAnimation(CharacterType.PLAYER, BodySegmentType.UPPER_BODY, AnimationType.ATTACKING, attack);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load player animation");
        }
    }

    public Entity loadEntityJackson(String filename) {
        try {
            FileHandle file = Gdx.files.internal(filename);
            if (!file.exists()) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.addMixInAnnotations(Vector2.class, Vector2Mixin.class);

            String json = file.readString();
            return mapper.readValue(json, Entity.class);

        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load entity from: " + filename, e);
            return null;
        }
    }

    public static <T> void serializeEntityJackson(T entity) {
        ObjectMapper mapper = new ObjectMapper();
        // add special rules for Vector2 class
        mapper.addMixInAnnotations(Vector2.class, Vector2Mixin.class);
        // formats json for better reading
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String path = "levels/serialized_component_jackson.json";
        try{
            mapper.writeValue(new File(path), entity);
        } catch (Exception e) {
            Gdx.app.error("JacksonSerializer", "Failed to serialize component: " + entity.getClass().getSimpleName(), e);
        }
    }

    @JsonSerialize(using = Vector2Serializer.class)
    @JsonDeserialize(using = Vector2Deserializer.class)
    public abstract class Vector2Mixin {
    }

    public static class Vector2Serializer extends JsonSerializer<Vector2> {

        @Override
        public void serialize(Vector2 value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("x", value.x);
            gen.writeNumberField("y", value.y);
            gen.writeEndObject();
        }
    }

    public static class Vector2Deserializer extends JsonDeserializer<Vector2> {
        @Override
        public Vector2 deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            float x = (float) node.get("x").asDouble();
            float y = (float) node.get("y").asDouble();
            return new Vector2(x, y);
        }
    }

}
