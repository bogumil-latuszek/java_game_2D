package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
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

    // Serialize single entity
    private JsonValue serializeEntity(Entity entity) {
        JsonValue entityJson = new JsonValue(JsonValue.ValueType.object);
        entityJson.addChild("id", new JsonValue(entity.getID()));

        JsonValue componentsArray = new JsonValue(JsonValue.ValueType.array);

        Component[] components = entity.getAllComponents();
        // Serialize all components
        for (Component component : components) {
//            if (component instanceof SerializableComponent) {
//                ComponentSerializer serializer = serializers.get(component.getClass());
//                if (serializer != null) {
//                    JsonValue componentJson = serializer.serialize(component);
//                    componentJson.addChild("_type", new JsonValue(component.getClass().getSimpleName()));
//                    componentsArray.addChild(componentJson);
//                }
//            }
        }

        entityJson.addChild("components", componentsArray);
        return entityJson;
    }

//    public static <T> void serializeComponentJsonWriter(T component) {
//        json = new Json();
//        // Pretty print for readability
//        json.setOutputType(JsonWriter.OutputType.json);
//        String jsonText = json.toJson(component, component.getClass());
//        jsonText = json.prettyPrint(jsonText);
//        String path = "levels/serialized_component_json_writer.json";
//        try {
//            Gdx.files.local(path).writeString(jsonText, false);
//
//        } catch (Exception e) {
//            Gdx.app.error("GenericSerializer", "Failed to serialize component: " + component.getClass().getSimpleName(), e);
//        }
//    }

//    public static Entity deserializeEntityJackson() {
//        String jsonText = Gdx.files.local("levels/serialized_component_jackson.json").readString();
//        return json.fromJson(Entity.class, jsonText); // Convert back to object
//    }

//    public <T> T loadObjectJackson(Class<T> type, String filename) {
//        if (!Gdx.files.local(filename).exists()) {
//            return null;
//        }
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.addMixInAnnotations(Vector2.class, Vector2Mixin.class);
//        try {
//            String json = Gdx.files.local(filename).readString();
//            return mapper.readValue(json, type);
//        } catch (Exception e) {
//            Gdx.app.error("JacksonDeserializer", "Load failed: " + filename, e);
//            return null;
//        }
//    }

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
