package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerIsDeadEvent;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;

public class PlayerDeathHandler {

    float respawnTimer = 5f;
    boolean playerDied = false;
    private final int respawnLevelID = 3; //TODO: this should be loaded from settings

    public PlayerDeathHandler() {
        EventBus.getInstance().subscribe(PlayerIsDeadEvent.class, this::handlePlayerDeath);
    }

    private void handlePlayerDeath(PlayerIsDeadEvent event){
        // we need to leave some time for player death animation to play out
        this.playerDied = true;
//
//        TeleportPlayerEvent teleportEvent = new TeleportPlayerEvent();
//        teleportEvent.targetLevelID = respawnLevelID;
//        EventBus.getInstance().publish(teleportEvent);
    }

    public void update(float deltaTime){
        if (playerDied && respawnTimer >= 0){
            respawnTimer -= deltaTime;
        }
        if (playerDied && respawnTimer <=0 ){
            loadRespawnLevel();
        }
    }

    private void loadRespawnLevel(){
        TeleportPlayerEvent teleportEvent = new TeleportPlayerEvent();
        teleportEvent.targetLevelID = this.respawnLevelID;
        EventBus.getInstance().publish(teleportEvent);
    }

}
