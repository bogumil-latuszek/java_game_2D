package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Collision;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DamageEmitterComponent;
import io.github.JavaGame2D.Components.HealthComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerHpChanged;

import java.util.ArrayList;

public class DamageSystem {
    private EntityComponentManager entityComponentManager;
    private Long damageEmitterSignature;
    private Long canBeDamagedSignature;

    public DamageSystem(EntityComponentManager entityComponentManager) {
        this.entityComponentManager = entityComponentManager;
        this.damageEmitterSignature = ComponentSignatures.DAMAGE_EMITTER;
        this.canBeDamagedSignature = ComponentSignatures.HEALTH;
    }

    public void update(){
        // #1 find all things with health component
        int[] livingEntities = entityComponentManager.getEntitiesMatchingSignature(canBeDamagedSignature);
        // #2 for those whose hp <= 0 and aren't invincible, destroy them
        for (int entityID : livingEntities){
            HealthComponent healthComponent = entityComponentManager.getComponent(HealthComponent.class, entityID);
            if (healthComponent.currentHp <= 0){
                entityComponentManager.deleteEntity(entityID);
            }
        }
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
//        int playerID = entityComponentManager.getPlayerEntityID();
//        // finally, resolve each player collision with damage emitter
//        for(Collision c : playerCollisionsWithDamageEmitters){
//            int damageEmitterID = c.entity.ID == playerID ? c.otherEntity.ID : c.entity.ID;
//            DamageEmitterComponent damageEmitter = entityComponentManager.getComponent(DamageEmitterComponent.class, damageEmitterID);
//
//            HealthComponent playerHealth = entityComponentManager.getComponent(HealthComponent.class, playerID);
//            playerHealth.currentHp -= damageEmitter.damageAmount;
//            System.out.println("player damaged by entity with id: " + damageEmitterID);
//            PlayerHpChanged event = new PlayerHpChanged(playerHealth.currentHp);
//            EventBus.getInstance().publish(event);
//        }
    }

    public boolean isDamageEmitter (Entity entity){
        if ((entity.signature & damageEmitterSignature) == damageEmitterSignature){
            return true;
        }
        return false;
    }

    public boolean canBeDamaged (Entity entity){
        if ((entity.signature & canBeDamagedSignature) == canBeDamagedSignature){
            return true;
        }
        return false;
    }

    public void resolveDamage(Entity damageEmitter, Entity damageReceiver){
        DamageEmitterComponent damageEmitterComponent = entityComponentManager.getComponent(DamageEmitterComponent.class, damageEmitter.ID);
        HealthComponent healthComponent = entityComponentManager.getComponent(HealthComponent.class, damageReceiver.ID);
        if (!healthComponent.isInvulnerable){
            healthComponent.currentHp -= damageEmitterComponent.damageAmount;
            if (healthComponent.currentHp < 0){
                healthComponent.currentHp = 0;
            }
        }
    }
}
