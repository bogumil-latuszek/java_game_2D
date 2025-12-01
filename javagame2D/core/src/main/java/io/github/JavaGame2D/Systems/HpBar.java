package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.DrawableComponent;

public class HpBar {
    public Vector2 position = new Vector2(3.2f,9.4f);
    public int hp_bar_id = 5;
    public int hp_bar_frame_id = 6;
    public int maxSize = 1;
    public int currentSize = 1;
    public float width = 6;
    public float height = 0.5f;

    public HpBar(int maxHp) {
        this.maxSize = maxHp;
    }

    public void update(int currentHp){
        this.currentSize = currentHp <= maxSize ? currentHp : currentSize;
    }
}
