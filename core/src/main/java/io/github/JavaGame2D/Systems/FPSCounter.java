package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FPSCounter {
    private final BitmapFont font;
    private final SpriteBatch batch;
    private float fps;
    private float timeSinceLastUpdate;
    private int framesSinceLastUpdate;
    private int fpsUpdateInterval;

    public FPSCounter() {
        this.font = new BitmapFont();
        this.batch = new SpriteBatch();

        // Style the font
        font.setColor(Color.YELLOW);
        font.getData().setScale(1.2f);

        this.fps = 0f;
        this.timeSinceLastUpdate = 0f;
        this.framesSinceLastUpdate = 0;
        this.fpsUpdateInterval = 500;

    }

    public void update(float deltaTime){

        timeSinceLastUpdate += deltaTime;
        framesSinceLastUpdate ++;
        if (timeSinceLastUpdate>fpsUpdateInterval){
            float averageFrameDuration = timeSinceLastUpdate/framesSinceLastUpdate;
            fps = fpsUpdateInterval/averageFrameDuration;
            timeSinceLastUpdate = 0;
            framesSinceLastUpdate = 0;
        }
    }

    //TODO: outsource rendering to rendering system
    public void render(){
        batch.begin();
        // Draw FPS in top-left corner
        font.draw(batch, "FPS: " + fps, 20, Gdx.graphics.getHeight() - 50);
        batch.end();
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
