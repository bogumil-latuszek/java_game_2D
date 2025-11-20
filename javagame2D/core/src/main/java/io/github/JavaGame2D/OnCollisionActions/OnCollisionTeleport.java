package io.github.JavaGame2D.OnCollisionActions;

import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;
import io.github.JavaGame2D.Systems.EntityManager;

public class OnCollisionTeleport implements OnCollisionAction {
    public String targetLevelName = "default";
    @Override
    public void trigger(Entity emitter, Entity other) {
        //TODO: Change EntityManager to Singleton with "getInstance", or set playerId as a global variable
//        if (other.getID() == EntityManager.getInstance.getPlayerID()){
//            TeleportPlayerEvent event = new TeleportPlayerEvent();
//            event.targetLevelName = this.targetLevelName;
//            EventBus.getInstance().publish(event);
//        }
        TeleportPlayerEvent event = new TeleportPlayerEvent();
        event.targetLevelName = this.targetLevelName;
        EventBus.getInstance().publish(event);
    }
}
