package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Collision;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.TeleporterComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;

import java.util.ArrayList;

public class TeleporterSystem {
    private EntityComponentManager entityComponentManager;

    public TeleporterSystem(EntityComponentManager entityComponentManager) {
        this.entityComponentManager = entityComponentManager;
    }

    public void detectTeleporterActivation(Collision[] collisions){

        // first, narrow down the search to all Collisions with Teleporters:

        long teleportSignature = ComponentSignatures.TELEPORTER;
        ArrayList<Collision> collisionsWithTeleporters = new ArrayList<>();

        for (Collision collision: collisions){
            if ((collision.entity.signature & teleportSignature) == teleportSignature ||
               (collision.otherEntity.signature & teleportSignature) == teleportSignature){
                collisionsWithTeleporters.add(collision);
            }
        }
        // second, check if any of these collisions were with the Player

        int playerID = entityComponentManager.getPlayerEntityID();
        ArrayList<Collision> playerCollisionsWithTeleporters = new ArrayList<>();

        for (Collision collision: collisionsWithTeleporters){
            if (collision.entity.ID == playerID || collision.otherEntity.ID == playerID){
                playerCollisionsWithTeleporters.add(collision);
            }
        }

        if (playerCollisionsWithTeleporters.isEmpty()){
            return;
        }
        // finally, resolve first detected player collision with Teleporter

        Collision c = playerCollisionsWithTeleporters.get(0);
        int teleporterID = c.entity.ID == playerID ? c.otherEntity.ID : c.entity.ID;
        TeleporterComponent teleporter = entityComponentManager.getTeleporterComponent(teleporterID);

        TeleportPlayerEvent teleportEvent = new TeleportPlayerEvent();
        teleportEvent.targetLevelID = teleporter.targetLevelID;
        EventBus.getInstance().publish(teleportEvent);

    }
}
