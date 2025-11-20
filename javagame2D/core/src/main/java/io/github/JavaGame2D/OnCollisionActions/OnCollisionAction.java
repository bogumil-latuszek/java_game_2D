package io.github.JavaGame2D.OnCollisionActions;

import io.github.JavaGame2D.Entity;

public interface OnCollisionAction {
    void trigger(Entity emitter, Entity other);
}
