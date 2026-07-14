package io.github.JavaGame2D;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class EditorCameraController implements InputProcessor {

    private OrthographicCamera camera;
    private ExtendViewport viewport;

    // Zoom settings
    private static final float ZOOM_SPEED = 1.15f; // Multiplier per scroll step
    private static final float MIN_ZOOM = 0.05f;   // 5% zoom (very close)
    private static final float MAX_ZOOM = 20.0f;   // 2000% zoom (very far)

    // Drag settings
    private boolean isDragging = false;
    private int lastScreenX, lastScreenY;
    float cameraSpeedModifier = 0.01f;

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
            camera.translate(-deltaX * camera.zoom*cameraSpeedModifier, deltaY * camera.zoom*cameraSpeedModifier);

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
        // amountY > 0 = scroll UP (zoom in)
        // amountY < 0 = scroll DOWN (zoom out)
        float newZoom = camera.zoom;

        if (amountY > 0) {
            newZoom = newZoom / ZOOM_SPEED; // Zoom in (smaller number = closer)
        } else if (amountY < 0) {
            newZoom = newZoom * ZOOM_SPEED; // Zoom out (larger number = farther)
        }

        // Clamp to prevent extreme zoom values
        newZoom = MathUtils.clamp(newZoom, MIN_ZOOM, MAX_ZOOM);

        // Apply and update
        camera.zoom = newZoom;
        camera.update();

        return true; // Consume the event
    }
}
