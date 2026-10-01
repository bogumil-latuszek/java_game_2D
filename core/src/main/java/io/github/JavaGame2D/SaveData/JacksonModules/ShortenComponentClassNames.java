package io.github.JavaGame2D.SaveData.JacksonModules;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ShortenComponentClassNames extends SimpleModule {
    // This Module is used to shorten Component names in JSON save files. Instead of saving them as:
    // "io.github.JavaGame2D.Components.ComponentName" Jackson saves them as simple "ComponentName"
    private final String basePackage;

    public HashMap<Class<?>, Object> readComponentsMap(JsonNode componentsNode, ObjectMapper mapper)
        throws IOException {
        HashMap<Class<?>, Object> result = new HashMap<>();

        Iterator<HashMap.Entry<String, JsonNode>> fields = componentsNode.fields();
        while (fields.hasNext()) {
            HashMap.Entry<String, JsonNode> entry = fields.next();
            String shortName = entry.getKey();
            JsonNode valueNode = entry.getValue();

            try {
                // Resolve short name -> Class
                Class<?> componentClass = Class.forName(basePackage + "." + shortName);

                // Deserialize the value AS that concrete class
                Object component = mapper.treeToValue(valueNode, componentClass);

                result.put(componentClass, component);
            } catch (ClassNotFoundException e) {
                // Stale component in the file—the class no longer exists.
                // Log and skip instead of crashing.
                Gdx.app.log("PrefabLoader", "Unknown component: " + shortName);
            }
        }

        return result;
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public ShortenComponentClassNames(String basePackage) {
        this.basePackage = basePackage;

        Class rawClass = Class.class;

        // ---- Value serializer (for Class appearing as a VALUE) ----
        addSerializer(rawClass, new JsonSerializer<Class<?>>() {
            @Override
            public void serialize(Class<?> value, JsonGenerator gen, SerializerProvider sp)
                throws IOException {
                gen.writeString(value.getSimpleName());
            }
        });

        // ---- Value deserializer (for Class appearing as a VALUE) ----
        addDeserializer(rawClass, new JsonDeserializer<Class<?>>() {
            @Override
            public Class<?> deserialize(JsonParser p, DeserializationContext ctx)
                throws IOException {
                return resolveClass(p.getValueAsString());
            }
        });

        // ---- Key serializer (for Class appearing as a MAP KEY) ----
        addKeySerializer(rawClass, new JsonSerializer<Class<?>>() {
            @Override
            public void serialize(Class<?> value, JsonGenerator gen, SerializerProvider sp)
                throws IOException {
                gen.writeFieldName(value.getSimpleName());
            }
        });

        // ---- Key deserializer (for Class appearing as a MAP KEY) ----
        addKeyDeserializer(rawClass, new KeyDeserializer() {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctx)
                throws IOException {
                return resolveClass(key);
            }
        });
    }

    public Class<?> resolveClass(String shortName) throws IOException {
        try {
            return Class.forName(basePackage + "." + shortName);
        } catch (ClassNotFoundException e) {
            throw new IOException("Unknown component class: " + shortName, e);
        }
    }
}
