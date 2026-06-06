package io.github.JavaGame2D.Components;

import io.github.JavaGame2D.Enums.DamageType;

public class DamageEmitterComponent implements Component{

    public DamageType damageType = DamageType.BURST;
    public int damageAmount = 1;

    public DamageEmitterComponent() {
    }

    // optional:
    // public DamageElement damageElement = DamageElement.PHYSICAL;


    @Override
    public long getSignature() {
        return ComponentSignatures.DAMAGE_EMITTER;
    }
}
