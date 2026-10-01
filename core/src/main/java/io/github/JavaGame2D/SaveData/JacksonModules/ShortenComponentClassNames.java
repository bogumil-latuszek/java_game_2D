package io.github.JavaGame2D.SaveData.JacksonModules;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.JavaGame2D.SaveData.ComponentFullNameMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ShortenComponentClassNames extends SimpleModule {
    // This Module is used to shorten Component names in JSON save files. Instead of saving them as:
    // "io.github.JavaGame2D.Components.ComponentName" Jackson saves them as simple "ComponentName"
    //private final String basePackage;
    private ComponentFullNameMapper componentMapper;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ShortenComponentClassNames(ComponentFullNameMapper componentMapper) {

        this.componentMapper = componentMapper;

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
                return componentMapper.getComponentClass(p.getValueAsString());
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
                return componentMapper.getComponentClass(key);
            }
        });
    }
}
