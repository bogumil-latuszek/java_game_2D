package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.JavaGame2D.Components.Component;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.GameSettings;
import io.github.JavaGame2D.Level;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

import java.io.File;
import java.io.IOException;

public class FileSystem {

    public void saveLevel(Level level){
    }
    public Level loadLevel(String levelName){
        if (!Gdx.files.local("levels/"+levelName+".json").exists()) {
            return new Level(); // Return defaults if no save file
        }
        String jsonText = Gdx.files.local("levels/"+levelName+".json").readString();
        return json.fromJson(Level.class, jsonText); // Convert back to object
    }

    public Entity loadEntityJackson(String filename) {
        try {
            FileHandle file = Gdx.files.local(filename);
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
