package io.github.JavaGame2D;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.SaveData.BakedEntity;
import io.github.JavaGame2D.SaveData.PrefabInstance;

import java.util.ArrayList;
import java.util.HashMap;

public class Level {
    // levelID
    public int levelID;
    // level name
    public String levelName = "unnamed"; //fill with filename

    public ArrayList<PrefabInstance> prefabInstances;
    public ArrayList<BakedEntity> bakedEntities;

    public Level(){
        prefabInstances = new ArrayList<>();
        bakedEntities = new ArrayList<>();
    }

    // level-specific settings:
    // boundary for camera
//    public Vector2 lowerLeftCorner;
//    public Vector2 upperRightCorner;
    // spawn point coordinates
    public HashMap<Integer, Vector2> validSpawnPoints;
    public int defaultSpawnPointID;

}
