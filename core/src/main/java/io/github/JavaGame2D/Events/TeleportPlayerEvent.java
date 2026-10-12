package io.github.JavaGame2D.Events;

public class TeleportPlayerEvent {
    public int targetLevelID = -1;   // -1 = same level
    public int targetEntityID = -1;  // -1 = use level's default spawn point
}
