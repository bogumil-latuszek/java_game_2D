package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Collision;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DamageEmitterComponent;
import io.github.JavaGame2D.Components.TeleporterComponent;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;

import java.util.ArrayList;

public class DamageSystem {
    private EntityComponentManager entityComponentManager;

    public DamageSystem(EntityComponentManager entityComponentManager) {
        this.entityComponentManager = entityComponentManager;
    }

    public void detectDamage(Collision[] collisions){

        // first, narrow down the search to all Collisions with Damage Emitters:

        long damageSignature = ComponentSignatures.DAMAGE_EMITTER;
        ArrayList<Collision> collisionsWithDamageEmitters = new ArrayList<>();

        for (Collision collision: collisions){
            if ((collision.entity.signature & damageSignature) == damageSignature ||
               (collision.otherEntity.signature & damageSignature) == damageSignature){
                collisionsWithDamageEmitters.add(collision);
            }
        }
        // second, check if any of these collisions were with the Player

        int playerID = entityComponentManager.getPlayerEntityID();
        ArrayList<Collision> playerCollisionsWithDamageEmitters = new ArrayList<>();

        for (Collision collision: collisionsWithDamageEmitters){
            if (collision.entity.ID == playerID || collision.otherEntity.ID == playerID){
                playerCollisionsWithDamageEmitters.add(collision);
            }
        }

        if (playerCollisionsWithDamageEmitters.isEmpty()){
            return;
        }
        // finally, resolve each player collision with damage emitter
        for(Collision c : playerCollisionsWithDamageEmitters){
            int damageEmitterID = c.entity.ID == playerID ? c.otherEntity.ID : c.entity.ID;
            //DamageEmitterComponent damageEmitter = entityComponentManager.getComponent(DamageEmitterComponent.class, damageEmitterID);

            System.out.println("player damaged by entity with id: " + damageEmitterID);
        }
    }
}
