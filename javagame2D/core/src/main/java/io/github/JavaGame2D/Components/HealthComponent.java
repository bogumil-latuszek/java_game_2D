package io.github.JavaGame2D.Components;

public class HealthComponent {
    private int currentHp;
    private int maxHp;

    // optional fields:
    private boolean isInvulnerable = false;
    private float invulnerabilityTimer = 0f;
    //private float lastDamageTime = 0f;
    private boolean isDead = false;

    private HealthComponent(){
        this.maxHp = 1;
        this.currentHp = 1;
    }
    public HealthComponent(int maxHp) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    // Getters and setters...
}
