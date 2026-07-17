package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerHpChanged;

public class FPSCounter {
    //private final BitmapFont font;
    //private final SpriteBatch batch;
    private Label healthLabel;
    public float fps;
    private float timeSinceLastUpdate;
    private int framesSinceLastUpdate;
    private int fpsUpdateInterval;

    public FPSCounter(Skin uiSkin) {
        this.fps = 0f;
        this.timeSinceLastUpdate = 0f;
        this.framesSinceLastUpdate = 0;
        this.fpsUpdateInterval = 500;
        healthLabel = new Label("fps:?", uiSkin);
        healthLabel.setColor(Color.RED);

    }

    public void update(float deltaTimeInSeconds){
        float deltaTimeInMilliseconds = deltaTimeInSeconds * 1000;
        timeSinceLastUpdate += deltaTimeInMilliseconds;
        framesSinceLastUpdate ++;
        if (timeSinceLastUpdate>fpsUpdateInterval){
            float averageFrameDuration = timeSinceLastUpdate/framesSinceLastUpdate;
            fps = fpsUpdateInterval/averageFrameDuration;
            timeSinceLastUpdate = 0;
            framesSinceLastUpdate = 0;
            healthLabel.setText("fps: "+fps);
        }

    }

    public void attachToTable(Table table){
        table.add(healthLabel).padRight(10).padTop(20);
    }

    //TODO: outsource rendering to rendering system
//    public void render(){
//        batch.begin();
//        // Draw FPS in top-left corner
//        font.draw(batch, "FPS: " + fps, 20, Gdx.graphics.getHeight() - 50);
//        batch.end();
//    }

//    public void dispose() {
//        font.dispose();
//        batch.dispose();
//    }
}
