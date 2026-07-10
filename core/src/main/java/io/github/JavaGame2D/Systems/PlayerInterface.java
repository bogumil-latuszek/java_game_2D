package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerHpChanged;

public class PlayerInterface {

    public FPSCounter fpsCounter;
    public HpBar hpBar;

    public PlayerInterface(){
        fpsCounter = new FPSCounter();
        hpBar = new HpBar(100);
        EventBus.getInstance().subscribe(PlayerHpChanged.class, this::handlePlayerHPChange);
    }

    public void update(float deltaTimeInSeconds){
        float deltaTimeInMilliseconds = deltaTimeInSeconds * 1000;
        fpsCounter.update(deltaTimeInMilliseconds);
    }

    public void handlePlayerHPChange(PlayerHpChanged event){
        this.hpBar.update(event.currentHP);
        //System.out.println("playerHP: "+ event.currentHP);
    }

    //TODO: outsource rendering to rendering system
    public void render(){
        fpsCounter.render();
    }

    public void dispose(){
        fpsCounter.dispose();
    }
}
