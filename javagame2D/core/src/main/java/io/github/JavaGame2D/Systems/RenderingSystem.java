package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class RenderingSystem {
    private SpriteBatch batch;
    private Texture image;

    public RenderingSystem(){
        image = new Texture("libgdx.png");
        batch = new SpriteBatch();
    }

    public void render(){
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    public void dispose(){
        batch.dispose();
        image.dispose();
    }
}
