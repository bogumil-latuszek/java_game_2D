package io.github.JavaGame2D.Components;

public class HealthComponent implements Component {
    public int currentHp;
    public int maxHp;

    // optional fields:
    public boolean isInvulnerable = false;
    public float iframeTimer = 0f;
    public float iframeDuration = 1f;
    public boolean damageTriggersiframes = false;
    //private float lastDamageTime = 0f;

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
