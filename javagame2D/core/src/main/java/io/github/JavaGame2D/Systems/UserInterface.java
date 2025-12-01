package io.github.JavaGame2D.Systems;

public class UserInterface {

    public FPSCounter fpsCounter;
    public HpBar hpBar;

    public UserInterface(){
        fpsCounter = new FPSCounter();
        hpBar = new HpBar(1);
    }

    public void update(float deltaTime){
        fpsCounter.update(deltaTime);
    }

    //TODO: outsource rendering to rendering system
    public void render(){
        fpsCounter.render();
    }

    public void dispose(){
        fpsCounter.dispose();
    }
}
