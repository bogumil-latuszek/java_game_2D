package io.github.JavaGame2D.Components;

public class DestructibleComponent implements Component{

    public boolean isAlive = true;
    public float destructionDelay = 0; //In Seconds

    @Override
    public long getSignature() {
        return ComponentSignatures.DESTRUCTIBLE;
    }

    @Override
    public Component makeCopy() {
        DestructibleComponent temp = new DestructibleComponent();
        temp.isAlive = this.isAlive;
        temp.destructionDelay = this.destructionDelay;
        return temp;
    }

}
