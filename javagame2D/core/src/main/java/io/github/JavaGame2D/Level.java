package io.github.JavaGame2D;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Systems.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Level {
    // levelID
    public int levelID;
    // level name
    public String levelName;

    // component collections:
    public TransformComponentsCollection transformCollection;
    public PhysicalBodyComponentsCollection physicalBodyCollection;
    public ColliderComponentCollection colliderCollection;
    public DrawableComponentsCollection drawableCollection;
    public TeleporterComponentsCollection teleporterCollection;

    // level-specific settings:
    // boundary for camera
    public Vector2 lowerLeftCorner;
    public Vector2 upperRightCorner;
    // spawn point coordinates
    public HashMap<Integer, Vector2> validSpawnPoints;
    public int defaultSpawnPointID;

    public Level() {
        validSpawnPoints = new HashMap<>();
    }
}
