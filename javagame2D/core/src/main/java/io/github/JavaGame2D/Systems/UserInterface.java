package io.github.JavaGame2D.Systems;

public class UserInterface {

    private FPSCounter fpsCounter;

    public UserInterface(){
        fpsCounter = new FPSCounter();
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
