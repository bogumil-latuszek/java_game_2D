package io.github.JavaGame2D.Components;

public class HealthComponent implements Component {
    public int currentHp;
    public int maxHp;

    // optional fields:
    public boolean isInvulnerable = false;
    public float invulnerabilityTimer = 0f;
    //private float lastDamageTime = 0f;
    public boolean isDead = false;

    private HealthComponent(){
        this.maxHp = 1;
        this.currentHp = 1;
    }
    public HealthComponent(int maxHp) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    @Override
    public long getSignature() {
        return ComponentSignatures.HEALTH;
    }

    // Getters and setters...
}
