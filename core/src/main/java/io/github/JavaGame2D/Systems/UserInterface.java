package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerHpChanged;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;

public class UserInterface {

    public FPSCounter fpsCounter;
    public HpBar hpBar;

    public UserInterface(){
        fpsCounter = new FPSCounter();
        hpBar = new HpBar(100);
        EventBus.getInstance().subscribe(PlayerHpChanged.class, this::handlePlayerHPChange);
    }

    public void update(float deltaTime){
        fpsCounter.update(deltaTime);
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
