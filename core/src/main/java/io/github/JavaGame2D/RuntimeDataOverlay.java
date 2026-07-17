package io.github.JavaGame2D;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.JavaGame2D.Systems.FPSCounter;
import io.github.JavaGame2D.UserInterface.PlayerHealthIndicator;

public class RuntimeDataOverlay {

    public FPSCounter fpsCounter;
    private Stage stage;
    private Skin uiSkin;

    public RuntimeDataOverlay(Skin uiSkin){
        this.uiSkin = uiSkin;
        ScreenViewport viewport = new ScreenViewport();
        this.stage = new Stage(viewport);
        buildOverlay();
    }

    public void render(){
        stage.draw();
    }

    public void resizeViewport(int screenWidth, int screenHeight, boolean centerCamera){
        stage.getViewport().update(screenWidth,screenHeight,centerCamera);
    }

    private void buildOverlay() {
        // Create a root table that fills the entire screen
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().left(); // Align to top-left corner
        rootTable.pad(10);      // Add 10px padding from screen edges

        fpsCounter = new FPSCounter(uiSkin);
        fpsCounter.attachToTable(rootTable);

        // Add the table to the stage
        stage.addActor(rootTable);
    }

    public void update(float deltaTimeInSeconds){
        fpsCounter.update(deltaTimeInSeconds);
    }
}
