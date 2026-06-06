package io.github.JavaGame2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class GameSettings {

    //private Json json = new Json();
    public float gravity = -9.8f;
    public float groundCheckDepth = 1f;

    // Empty constructor is IMPORTANT for JSON
    public GameSettings() {}

    public void saveSettings() {
        Json json = new Json();

        json.setTypeName(null);
        json.setUsePrototypes(false);
        json.setIgnoreUnknownFields(true);
        json.setOutputType(JsonWriter.OutputType.json);

        String jsonText = json.toJson(this, this.getClass());
        Gdx.files.local("settings.json").writeString(jsonText, false);
        Gdx.app.log("Save", "Settings saved!");
    }

    public GameSettings loadSettings() {
        if (!Gdx.files.local("settings.json").exists()) {
            return new GameSettings(); // Return defaults if no save file
        }

        String jsonText = Gdx.files.local("settings.json").readString();
        return json.fromJson(GameSettings.class, jsonText); // Convert back to object
    }

}
