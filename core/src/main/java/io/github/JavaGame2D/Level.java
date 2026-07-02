package io.github.JavaGame2D;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Collections.*;
import io.github.JavaGame2D.Components.*;
import io.github.JavaGame2D.Systems.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Level {
    // levelID
    public int levelID;
    // level name
    public String levelName;

    // component collections:
    public ComponentCollection<TransformComponent> transformCollection;
    public ComponentCollection<PhysicalBodyComponent>  physicalBodyCollection;
    public ComponentCollection<ColliderComponent>  colliderCollection;
    public ComponentCollection<DrawableComponent>  drawableCollection;
    public ComponentCollection<TeleporterComponent>  teleporterCollection;
    public ComponentCollection<HealthComponent>  healthCollection;
    public ComponentCollection<DamageEmitterComponent>  damageEmitterCollection;
    public ComponentCollection<DestructibleComponent>  destructibleCollection;

    // level-specific settings:
    // boundary for camera
    public Vector2 lowerLeftCorner;
    public Vector2 upperRightCorner;
    // spawn point coordinates
    public HashMap<Integer, Vector2> validSpawnPoints;
    public int defaultSpawnPointID;

    public Level() {
        validSpawnPoints = new HashMap<>();
        transformCollection = new ComponentCollection<>(TransformComponent.class,ComponentSignatures.TRANSFORM);
        drawableCollection = new ComponentCollection<>(DrawableComponent.class,ComponentSignatures.DRAWABLE);
        physicalBodyCollection = new ComponentCollection<>(PhysicalBodyComponent.class,ComponentSignatures.PHYSICAL_BODY);
        colliderCollection = new ComponentCollection<>(ColliderComponent.class,ComponentSignatures.COLLIDER);
        teleporterCollection = new ComponentCollection<>(TeleporterComponent.class,ComponentSignatures.TELEPORTER);
        healthCollection = new ComponentCollection<>(HealthComponent.class,ComponentSignatures.HEALTH);
        damageEmitterCollection = new ComponentCollection<>(DamageEmitterComponent.class, ComponentSignatures.DAMAGE_EMITTER);
        destructibleCollection = new ComponentCollection<>(DestructibleComponent.class, ComponentSignatures.DESTRUCTIBLE);
    }
}
