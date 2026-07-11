package io.github.JavaGame2D.UserInterface;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerHpChanged;
import io.github.JavaGame2D.Events.TeleportPlayerEvent;

public class PlayerHealthIndicator {
    ProgressBar healthBar;
    private Label healthLabel;

    public PlayerHealthIndicator(Skin uiSkin) {
        // create health bar
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle(
            uiSkin.get("default-horizontal", ProgressBar.ProgressBarStyle.class)
        );
        this.healthBar = new ProgressBar(0, 100, 1, false, style);
        healthBar.setValue(100);
        healthBar.setWidth(150);
        healthBar.setHeight(20);
        // create health numerical value label
        healthLabel = new Label("HP: 100/?", uiSkin);
        // register method to follow player's hp
        EventBus.getInstance().subscribe(PlayerHpChanged.class, this::handlePlayerHpChange);
    }

    private void handlePlayerHpChange(PlayerHpChanged event){
        healthBar.setValue(event.currentHP);
        healthLabel.setText("HP: "+event.currentHP+"/?");
    }

    public void attachToTable(Table table){
        table.add(healthBar).width(150).height(20);
        table.add(healthLabel).padRight(10);
    }

}

