package io.github.JavaGame2D;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Systems.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Level {
    // levelID
    public int levelID;
    // level name
    public String levelName;

    private ArrayList<Integer> levelEntitiesIDs;
    // list of entities
    public ArrayList<Entity> levelEntities;

    // level-specific settings:
    // boundary for camera
    public Vector2 lowerLeftCorner;
    public Vector2 upperRightCorner;
    // spawn point coordinates
    public HashMap<Integer, Vector2> validSpawnPoints;
    public int defaultSpawnPointID;

    public Level() {
        levelEntitiesIDs = new ArrayList<>();
        levelEntities = new ArrayList<>();
        validSpawnPoints = new HashMap<>();
    }

    public Level(int levelID, String levelName, ArrayList<Entity> levelEntities, Vector2 lowerLeftCorner,
                 Vector2 upperRightCorner, HashMap<Integer, Vector2> validSpawnPoints, int defaultSpawnPointID) {
        this.levelID = levelID;
        this.levelName = levelName;
        this.levelEntities = levelEntities;
        this.lowerLeftCorner = lowerLeftCorner;
        this.upperRightCorner = upperRightCorner;
        this.validSpawnPoints = validSpawnPoints;
        this.defaultSpawnPointID = defaultSpawnPointID;
    }

    public void registerLevelEntities(EntityManager entityManager){
        for (Entity entity : levelEntities){
            int ID = entityManager.addEntity(entity);
            levelEntitiesIDs.add(ID);
        }
    }
    public void deregisterLevelEntities(EntityManager entityManager){
        for (int ID : levelEntitiesIDs){
            entityManager.removeEntity(ID);
        }
    }
}
