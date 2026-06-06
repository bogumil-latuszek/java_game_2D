package io.github.JavaGame2D.Events;

public class LoadingNewLevelEvent {
    public int newLevelID;
    public int previousLevelID;

    public LoadingNewLevelEvent(int previousLevelID, int newLevelID) {
        this.previousLevelID = previousLevelID;
        this.newLevelID = newLevelID;
    }
}
