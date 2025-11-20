package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.EventType;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.CollisionEvent;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;

public class SpecialEventManager {
    private final EntityManager entityManager;

    public SpecialEventManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        EventBus.getInstance().subscribe(CollisionEvent.class, this::handleCollisionEvent);
    }

    public void handleCollisionEvent(CollisionEvent event){
        resolveSpecialCollisionEvent(event.emitterEntity, event.otherEntity);
    }

    // resolve special collision event
    public void resolveSpecialCollisionEvent(Entity emitter, Entity other){
//        EventType eventType = emitter.colliderComponent.eventType;
//        switch (eventType){
//            case TELEPORT:
//                if (other.getID() == entityManager.getPlayerID()){
//                    teleportPlayer();
//                }
//                break;
//        }
    }

    public void teleportPlayer(){
        TeleportPlayerEvent teleportEvent = new TeleportPlayerEvent();
        teleportEvent.targetLevelName = "level2";
        EventBus.getInstance().publish(teleportEvent);
    }



}
