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
}
