package io.github.JavaGame2D;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class EditorCameraController implements InputProcessor {

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private boolean isDragging = false;
    private int lastScreenX, lastScreenY;

    public EditorCameraController(OrthographicCamera camera, float minWorldWidth, float minWorldHeight) {
        this.camera = camera;
        viewport = new ExtendViewport(minWorldWidth,minWorldHeight,camera);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Only start dragging if right mouse button is pressed
        if (button == Input.Buttons.RIGHT) {
            isDragging = true;
            lastScreenX = screenX;
            lastScreenY = screenY;
            return true; // Consume the event so UI/game world don't get it
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            isDragging = false;
            return true;
        }
        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (isDragging) {
            // Calculate mouse movement delta in screen pixels
            float deltaX = screenX - lastScreenX;
            float deltaY = screenY - lastScreenY;

            // Apply movement to the camera.
            // We multiply by camera.zoom so that the pan speed feels "natural"
            // at different zoom levels (dragging 1 pixel = 1 world unit * zoom).
            camera.translate(-deltaX * camera.zoom*0.01f, deltaY * camera.zoom*0.01f);

            // Update the last known position
            lastScreenX = screenX;
            lastScreenY = screenY;

            // Mark the camera as dirty so the changes take effect
            camera.update();
            return true;
        }
        return false;
    }

    public void resizeViewport(int width, int height){
        this.viewport.update(width,height);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
