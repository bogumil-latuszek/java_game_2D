package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.JavaGame2D.*;
import io.github.JavaGame2D.Enums.AnimationType;
import io.github.JavaGame2D.Enums.BodySegmentType;
import io.github.JavaGame2D.Enums.CharacterType;
import io.github.JavaGame2D.Enums.FacingDirection;
import io.github.JavaGame2D.SaveData.JacksonModules.ShortenComponentClassNames;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class FileSystem {

    private ObjectMapper mapper;
    private ShortenComponentClassNames shortNameModule;

    public FileSystem(){
        mapper = new ObjectMapper();
        // add special rules for Vector2 class
        mapper.addMixInAnnotations(Vector2.class, Vector2Mixin.class);
        // formats json for better reading
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // make sure it deserializes as much as possible without failing
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // removes package names from Components in json
        this.shortNameModule = new ShortenComponentClassNames("io.github.JavaGame2D.Components");
        mapper.registerModule(shortNameModule);
        //mapper.registerModule(new ShortenComponentClassNames("io.myorg.MyGame.Components"));

        // only serialize public fields, ignore non-field variables such as variables inside getters, setters, etc.
        // mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);

        // use this line if you want non-public fields to be serialized:
        //mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

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

    public void savePrefab(String prefabName, Prefab prefab){
        String path = "assets/prefabs/" + prefabName + ".json";
        FileHandle fileHandle = Gdx.files.local(path);
        // Ensure the parent directory exists
        fileHandle.file().getParentFile().mkdirs();

        try{
            mapper.writeValue(new File(path), prefab);
        } catch (Exception e) {
            Gdx.app.error("JacksonSerializer", "Failed to serialize prefab: " + prefabName, e);
        }
    }

    public Optional<Prefab> loadPrefab(String prefabName) {
        FileHandle file = Gdx.files.internal("prefabs/" + prefabName + ".json");
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            String jsonText = file.readString();
            JsonNode root = mapper.readTree(jsonText);

            // 1. Deserialize the prefab normally. The `components` map will be
            //    populated with LinkedHashMap values — we overwrite it in step 2.
            Prefab prefab = mapper.treeToValue(root, Prefab.class);

            // 2. Rebuild the components map with proper concrete component types.
            JsonNode componentsNode = root.get("prefabComponents");
            if (componentsNode != null && componentsNode.isObject()) {
                prefab.prefabComponents = shortNameModule.readComponentsMap(componentsNode, this.mapper);
            } else {
                prefab.prefabComponents = new HashMap<>();
            }
            return Optional.of(prefab);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load prefab: " + prefabName, e);
            return Optional.empty();
        }
    }


    public void saveLevel(Level level){
        // Use LibGDX's local storage path (works on all platforms)
        // Also, remember that assets are READ-ONLY when distributing the game
        // so if we want to have custom level creation as a feature in the final game,
        // then we need to move levels to saves/levels/
        String path = "assets/levels/" + level.levelID + ".json";

        FileHandle fileHandle = Gdx.files.local(path);
        // Ensure the parent directory exists
        fileHandle.file().getParentFile().mkdirs();

        try{
            mapper.writeValue(new File(path), level);
        } catch (Exception e) {
            Gdx.app.error("JacksonSerializer", "Failed to serialize component: " + level.getClass().getSimpleName(), e);
        }
    }

//    public Level loadLevel(int levelID){
//        if (!Gdx.files.internal("levels/"+levelID+".json").exists()) {
//            return null; // Return defaults if no save file
//        }
//        try {
//            // 1. load json as string
//            String jsonText = Gdx.files.internal("levels/"+levelID+".json").readString();
//            // 2. create an empty level
//            return mapper.readValue(jsonText, Level.class);
//
//        } catch (Exception e) {
//            Gdx.app.error("SaveManager", "Failed to load level: "+ levelID, e);
//            return null;
//        }
//    }

    private static final String COMPONENT_BASE_PACKAGE = "io.myorg.MyGame.Components";

    public Optional<Level> loadLevel(int levelID) {
        FileHandle file = Gdx.files.internal("levels/" + levelID + ".json");
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            String jsonText = file.readString();
            JsonNode root = mapper.readTree(jsonText);

            // 1. Deserialize the level normally. Everything works EXCEPT
            //    each BakedEntity's `components` map, which becomes LinkedHashMaps.
            Level level = mapper.treeToValue(root, Level.class);

            // 2. Fix up the baked entities' component maps.
            JsonNode bakedEntitiesNode = root.get("bakedEntities");
            if (bakedEntitiesNode != null && bakedEntitiesNode.isArray()) {
                for (int i = 0; i < bakedEntitiesNode.size(); i++) {
                    JsonNode bakedNode = bakedEntitiesNode.get(i);
                    JsonNode componentsNode = bakedNode.get("components");

                    if (componentsNode != null && componentsNode.isObject()) {
                        level.bakedEntities.get(i).components = shortNameModule.readComponentsMap(componentsNode, mapper);
                    } else {
                        level.bakedEntities.get(i).components = new HashMap<>();
                    }
                }
            }
            return Optional.of(level);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load level: " + levelID, e);
            return Optional.empty();
        }
    }


    public void loadPlayerAnimations(AnimationManager animationManager, TextureManager textureManager){
        try {
            String directory = "animations/Player/";
            String fileExtension = ".png";
            textureManager.registerTexture(8, directory+"idle1"+fileExtension);
            textureManager.registerTexture(9, directory+"walk1"+fileExtension);
            textureManager.registerTexture(10, directory+"walk2"+fileExtension);
            textureManager.registerTexture(11, directory+"walk3"+fileExtension);
            textureManager.registerTexture(12, directory+"walk4"+fileExtension);
            textureManager.registerTexture(13, directory+"walk5"+fileExtension);
            textureManager.registerTexture(14, directory+"walk6"+fileExtension);
            textureManager.registerTexture(15, directory+"jumping1"+fileExtension);
            textureManager.registerTexture(16, directory+"falling1"+fileExtension);
            textureManager.registerTexture(17, directory+"attack1"+fileExtension);
            SpriteData idleFrame1 = new SpriteData(8, FacingDirection.RIGHT);
            SpriteData walkingFrame1 = new SpriteData(9, FacingDirection.RIGHT);
            SpriteData walkingFrame2 = new SpriteData(10, FacingDirection.RIGHT);
            SpriteData walkingFrame3 = new SpriteData(11, FacingDirection.RIGHT);
            SpriteData walkingFrame4 = new SpriteData(12, FacingDirection.RIGHT);
            SpriteData walkingFrame5 = new SpriteData(13, FacingDirection.RIGHT);
            SpriteData walkingFrame6 = new SpriteData(14, FacingDirection.RIGHT);
            SpriteData jumpingFrame1 = new SpriteData(15, FacingDirection.RIGHT);
            SpriteData fallingFrame1 = new SpriteData(16, FacingDirection.RIGHT);
            SpriteData attackFrame1 = new SpriteData(17, FacingDirection.RIGHT);
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

//    public Entity loadEntityJackson(String filename) {
//        try {
//            FileHandle file = Gdx.files.internal(filename);
//            if (!file.exists()) {
//                return null;
//            }
//            String json = file.readString();
//            return mapper.readValue(json, Entity.class);
//
//        } catch (Exception e) {
//            Gdx.app.error("SaveManager", "Failed to load entity from: " + filename, e);
//            return null;
//        }
//    }

//    public static <T> void serializeEntityJackson(T entity) {
//        String path = "levels/serialized_component_jackson.json";
//        try{
//            mapper.writeValue(new File(path), entity);
//        } catch (Exception e) {
//            Gdx.app.error("JacksonSerializer", "Failed to serialize component: " + entity.getClass().getSimpleName(), e);
//        }
//    }

}
