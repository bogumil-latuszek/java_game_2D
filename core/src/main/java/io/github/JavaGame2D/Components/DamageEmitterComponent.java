package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.Enums.DamageType;

public class DamageEmitterComponent implements Component{

    public DamageType damageType = DamageType.BURST;
    public int damageAmount = 1;
    public boolean selfDestructsOnImpact = false;

    public DamageEmitterComponent() {
    }

    // optional:
    // public DamageElement damageElement = DamageElement.PHYSICAL;


    public static final long SIGNATURE = ComponentSignatures.register(DamageEmitterComponent.class);

    @Override
    public Component makeCopy() {
        DamageEmitterComponent temp = new DamageEmitterComponent();
        temp.damageType = this.damageType;
        temp.damageAmount = this.damageAmount;
        temp.selfDestructsOnImpact = this.selfDestructsOnImpact;
        return temp;
    }


}
