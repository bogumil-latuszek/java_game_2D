package io.github.JavaGame2D.UserInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.Optional;

public class PopupManager {

    //only one shown at a time
    private Optional<Table> activePopup;
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
        Table popup = new Table(uiskin);

        // TODO: define this background in uiskin
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.2f, 0.2f, 0.2f, 1f); // dark gray
        pixmap.fill();
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        popup.setBackground(background);
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

        // Position it over source label
        Vector2 pos = sourceLabel.localToStageCoordinates(new Vector2(0, 0));
        float x = pos.x;
        float y = pos.y;

        // Clamp horizontally to stay inside the stage
        if (x + popup.getWidth() > stage.getWidth()) {
            x = stage.getWidth() - popup.getWidth();
        }
        if (x < 0) x = 0;

        // if wrapped, correct position
        if (popup.getHeight() > sourceLabel.getHeight()){
            y = y -(popup.getHeight() - sourceLabel.getHeight());
        }

        // if it went off‑screen, place it above
        if (y < 0) {
            //y = pos.y + sourceLabel.getHeight() + 5;
            y = 0;
        }

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
