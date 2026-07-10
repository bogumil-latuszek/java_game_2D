package io.github.JavaGame2D.Events;

import io.github.JavaGame2D.Collision;

public class DetectedCollisionsEvent {
    public Collision[] collisions;

    public DetectedCollisionsEvent(Collision[] collisions) {
        this.collisions = collisions;
    }
}
