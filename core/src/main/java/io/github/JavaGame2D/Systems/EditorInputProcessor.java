package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class EditorInputProcessor implements InputProcessor {

    private EntityInspector entityInspector;
    private OrthographicCamera camera;

    public EditorInputProcessor(EntityInspector entityInspector, OrthographicCamera camera) {
        this.entityInspector = entityInspector;
        this.camera = camera;
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Only start dragging if right mouse button is pressed
        if (button == Input.Buttons.LEFT) {
            Vector3 touchScreenCoords3D = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 touchWorldCoords3D = camera.unproject(touchScreenCoords3D);
            Vector2 touchWorldCoords = new Vector2(touchScreenCoords3D.x, touchScreenCoords3D.y);
            entityInspector.selectEntity(touchWorldCoords);
        }
        return false;

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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
