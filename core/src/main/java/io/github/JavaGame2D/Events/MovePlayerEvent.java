package io.github.JavaGame2D.Events;

public class MovePlayerEvent {
    public int targetEntityID = -1; // -1 = use level's default spawn point

    public MovePlayerEvent(int targetEntityID) {
        this.targetEntityID = targetEntityID;
    }
}
