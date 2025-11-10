package io.github.JavaGame2D;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;

public class Level {
    // levelID
    public int levelID;
    // level name
    public String levelName;

    // list of entities
    public ArrayList<Entity> entitiesInside;

    // level-specific settings:
    // boundary for camera
    public Vector2 lowerLeftCorner;
    public Vector2 upperRightCorner;
    // spawn point coordinates
    public HashMap<Integer, Vector2> validSpawnPoints;
    public int defaultSpawnPointID;

    public Level() {
        this(0,
            "default",
            new ArrayList<>(),
            new Vector2(-100,-100),
            new Vector2(100,100),
            new HashMap<>(),
            0);
    }

    public Level(int levelID, String levelName, ArrayList<Entity> entitiesInside, Vector2 lowerLeftCorner,
                 Vector2 upperRightCorner, HashMap<Integer, Vector2> validSpawnPoints, int defaultSpawnPointID) {
        this.levelID = levelID;
        this.levelName = levelName;
        this.entitiesInside = entitiesInside;
        this.lowerLeftCorner = lowerLeftCorner;
        this.upperRightCorner = upperRightCorner;
        this.validSpawnPoints = validSpawnPoints;
        this.defaultSpawnPointID = defaultSpawnPointID;
    }
}
