package io.github.JavaGame2D.Components;

public class LifetimeComponent implements Component{
    public float deathTimer = 1; // In Seconds

    public static final long SIGNATURE = ComponentSignatures.register(LifetimeComponent.class);

    @Override
    public Component makeCopy() {
        LifetimeComponent temp = new LifetimeComponent();
        temp.deathTimer = this.deathTimer;
        return temp;
    }
}
