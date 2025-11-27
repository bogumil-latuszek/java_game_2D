package io.github.JavaGame2D;

public class Collision {
    public final Entity entity;
    public final Entity otherEntity;

    public Collision(Entity entity, Entity otherEntity) {
        this.entity = entity;
        this.otherEntity = otherEntity;
    }
}
