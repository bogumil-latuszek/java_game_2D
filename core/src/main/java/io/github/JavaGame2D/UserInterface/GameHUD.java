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
    private ProgressBar healthBar;
    private Label healthLabel;

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

        // Create the Health Label
        healthLabel = new Label("HP: 100/100", uiSkin);

        // Create the Health Progress Bar
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle(
            uiSkin.get("default-horizontal", ProgressBar.ProgressBarStyle.class)
        );
        healthBar = new ProgressBar(0, 100, 1, false, style);
        healthBar.setValue(100);
        healthBar.setWidth(150); // Set a fixed width
        healthBar.setHeight(20);

        // Add them to the table
        rootTable.add(healthLabel).padRight(10);
        rootTable.add(healthBar).width(150).height(20);

        // Add the table to the stage
        stage.addActor(rootTable);
    }
}
