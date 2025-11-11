package io.github.JavaGame2D;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Systems.EntityManager;
import io.github.JavaGame2D.Systems.FileSystem;

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
        entitiesInside = new ArrayList<>();
        validSpawnPoints = new HashMap<>();
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

    public void registerLevelEntities(EntityManager entityManager){
        for (Entity entity : entitiesInside){
            entityManager.addEntity(entity);
        }
    }
}
