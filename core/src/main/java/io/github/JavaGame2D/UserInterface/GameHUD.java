package io.github.JavaGame2D.UserInterface;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameHUD{

    private Stage stage;
    private Skin uiSkin;

    PlayerHealthIndicator hpIndicator;

    public GameHUD(Skin uiSkin) {
        this.uiSkin = uiSkin;
        ScreenViewport viewport = new ScreenViewport();
        this.stage = new Stage(viewport);
        buildHUD();
    }

    public Stage getStage() {
        return stage;
    }


    public void update(float deltaTimeInSeconds){
        stage.act();
    }

    public void render(){
        stage.draw();
    }

    public void resizeViewport(int screenWidth, int screenHeight, boolean centerCamera){
        stage.getViewport().update(screenWidth,screenHeight,centerCamera);
    }

    public void dispose(){
        stage.dispose();
    }

    private void buildHUD() {
        // Create a root table that fills the entire screen
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().left(); // Align to top-left corner
        rootTable.pad(10);      // Add 10px padding from screen edges

        hpIndicator = new PlayerHealthIndicator(uiSkin);
        //TODO: hpIndicator shouldn't attach itself, change it so that other widgets can use it any way THEY like
        hpIndicator.attachToTable(rootTable);

        // Add the table to the stage
        stage.addActor(rootTable);
    }
}
