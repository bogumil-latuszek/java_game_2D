package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Collision;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DamageEmitterComponent;
import io.github.JavaGame2D.Components.DestructibleComponent;
import io.github.JavaGame2D.Components.HealthComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.DetectedCollisionsEvent;
import io.github.JavaGame2D.Events.PlayerHpChanged;

import java.util.ArrayList;

public class DamageSystem {
    private EntityComponentManager entityComponentManager;
    private Long damageEmitterSignature;
    private Long canBeDamagedSignature;

    public DamageSystem(EntityComponentManager entityComponentManager) {
        this.entityComponentManager = entityComponentManager;
        this.damageEmitterSignature = ComponentSignatures.DAMAGE_EMITTER;
        this.canBeDamagedSignature = ComponentSignatures.DESTRUCTIBLE | ComponentSignatures.HEALTH;
        EventBus.getInstance().subscribe(DetectedCollisionsEvent.class, this::handlePotentialDamage);
    }

    private void handlePotentialDamage(DetectedCollisionsEvent event){
        detectAndResolveDamage(event.collisions);
    }

    public void update(float deltaTimeInSeconds){
        // #1 find all things with health component
        int[] livingEntities = entityComponentManager.getEntitiesMatchingSignature(canBeDamagedSignature);
        // #2 update iframes
        updateIframes(livingEntities, deltaTimeInSeconds);
        // #3 for those whose hp <= 0 and aren't invincible, destroy them
        destroyEntitiesWithNoHpLeft(livingEntities);
    }

    public void detectAndResolveDamage(Collision[] collisions){

        for (Collision collision: collisions){
            Entity entity = collision.entity;
            Entity otherEntity = collision.otherEntity;
            if ( isDamageEmitter(entity) && canBeDamaged(otherEntity) ){
                resolveDamage(entity, otherEntity);
            }
            if ( isDamageEmitter(otherEntity) && canBeDamaged(entity) ){
                resolveDamage(otherEntity, entity);
            }
        }
    }

    private boolean isDamageEmitter (Entity entity){
        if ((entity.signature & damageEmitterSignature) == damageEmitterSignature){
            return true;
        }
        return false;
    }

    private boolean canBeDamaged (Entity entity){
        if ((entity.signature & canBeDamagedSignature) == canBeDamagedSignature){
            return true;
        }
        return false;
    }

    private void resolveDamage(Entity damageEmitter, Entity damageReceiver){
        DamageEmitterComponent damageEmitterComponent = entityComponentManager.getComponent(DamageEmitterComponent.class, damageEmitter.ID);
        HealthComponent damageReceiverHealth = entityComponentManager.getComponent(HealthComponent.class, damageReceiver.ID);
        if (damageReceiverHealth.isInvulnerable){
            return;
        }
        if (damageReceiverHealth.iframeTimer >= 0){
            return;
        }
        calculateAndApplyDamage(damageEmitterComponent, damageReceiverHealth);
        if (damageReceiverHealth.damageTriggersiframes){
            damageReceiverHealth.iframeTimer = damageReceiverHealth.iframeDuration;
        }
        if (damageReceiver.ID == entityComponentManager.getPlayerEntityID()){
            PlayerHpChanged event = new PlayerHpChanged(damageReceiverHealth.currentHp, damageReceiverHealth.maxHp);
            EventBus.getInstance().publish(event);
        }
    }

    private void calculateAndApplyDamage(DamageEmitterComponent emitter, HealthComponent receiver){
        receiver.currentHp -= emitter.damageAmount;
        receiver.currentHp = Math.max(0, receiver.currentHp);
    }

    private void updateIframes(int[] livingEntities, float deltaTime){
        for (int entityID : livingEntities){
            HealthComponent healthComponent = entityComponentManager.getComponent(HealthComponent.class, entityID);
            if (healthComponent.iframeTimer >= 0){
                healthComponent.iframeTimer -= deltaTime;
            }
        }
    };

    private void destroyEntitiesWithNoHpLeft(int[] livingEntities){
        for (int entityID : livingEntities){
            HealthComponent healthComponent = entityComponentManager.getComponent(HealthComponent.class, entityID);
            DestructibleComponent destructible = entityComponentManager.getComponent(DestructibleComponent.class, entityID);
            if (destructible.isAlive && healthComponent.currentHp <= 0){
                destructible.isAlive = false;
            }
        }
    };
}
