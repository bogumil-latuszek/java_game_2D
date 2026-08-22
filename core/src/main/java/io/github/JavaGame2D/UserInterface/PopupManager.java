package io.github.JavaGame2D.UserInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Optional;

public class PopupManager {

    //only one shown at a time
    private Optional<Window> activePopup;
    private final Skin uiskin;
    private Stage stage;

    public PopupManager(Skin skin, Stage stage) {
        this.activePopup = Optional.empty();
        uiskin = skin;
        this.stage = stage;
    }


    public void showFullNamePopup(Label sourceLabel, String fullName) {
        // close previously opened popups (if any)
        closePopup();

        // Create new popup
        Window popup = new Window("", uiskin);
        //popup.setBackground(uiskin.getDrawable("tooltip"));
        //popup.setModal(false); // doesn't block input

        // Add the full name with optional wrapping
        String fullText = sourceLabel.getText().toString();
        Label content = new Label(fullText, uiskin);
        content.setWrap(true);

        // measure full text width
        BitmapFont font = uiskin.get(Label.LabelStyle.class).font;
        GlyphLayout measurement = new GlyphLayout();
        measurement.setText(font, fullText);
        float actualTextWidth = measurement.width;

        // set popup width to minimum value
        popup.add(content).width(Math.min(300, actualTextWidth + 20));
        popup.pack();

//        // Position it relative to the source label
//        Vector2 pos = sourceLabel.localToStageCoordinates(new Vector2(0, 0));
//        float x = pos.x;
//        float y = pos.y - popup.getHeight() - 5; // try below first (5px gap)
//
//        // If below would go off‑screen, place it above
//        if (y < 0) {
//            y = pos.y + sourceLabel.getHeight() + 5;
//        }
        // Position it over source label
        Vector2 pos = sourceLabel.localToStageCoordinates(new Vector2(0, 0));
        float x = pos.x;
        float y = pos.y;

        // Clamp horizontally to stay inside the stage
        if (x + popup.getWidth() > stage.getWidth()) {
            x = stage.getWidth() - popup.getWidth();
        }
        if (x < 0) x = 0;

        popup.setPosition(x, y);

        // Clicking on the popup itself closes it
        popup.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closePopup();
            }
        });

        stage.addActor(popup);
        activePopup = Optional.of(popup);
    }

    public void closePopup() {
        if (activePopup.isPresent()) {
            activePopup.get().remove();
            activePopup = Optional.empty();
        }
    }

}
