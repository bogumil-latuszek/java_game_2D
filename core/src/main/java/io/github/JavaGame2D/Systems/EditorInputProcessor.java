package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Enums.SelectedTool;

import java.util.OptionalInt;

public class EditorInputProcessor implements InputProcessor {

    private EntitySelector entitySelector;
    private OrthographicCamera camera;
    private SelectedTool selectedTool;
    private boolean isDragging = false;
    private Vector2 lastDragPosition;
    private EntityComponentManager entityComponentManager;

    public EditorInputProcessor(EntityComponentManager entityComponentManager, EntitySelector entitySelector, OrthographicCamera camera) {
        this.entityComponentManager = entityComponentManager;
        this.entitySelector = entitySelector;
        this.camera = camera;
        this.selectedTool = SelectedTool.DRAGGING_TOOL;
        lastDragPosition = new Vector2(0,0);
    }

    public OptionalInt getSelectedEntityID(){
        return this.entitySelector.getSelectedEntityID();
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
            if (selectedTool != SelectedTool.INSPECTION_TOOL &&
                selectedTool != SelectedTool.DRAGGING_TOOL){
                // currently used tool can't select
                return false;
            }
//            Vector3 touchScreenCoords3D = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
//            Vector3 touchWorldCoords3D = camera.unproject(touchScreenCoords3D);
//            Vector2 touchWorldCoords = new Vector2(touchScreenCoords3D.x, touchScreenCoords3D.y);
            Vector3 touchScreenCoords3D = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 touchWorldCoords3D = camera.unproject(touchScreenCoords3D);
            Vector2 touchWorldCoords = new Vector2(touchWorldCoords3D.x, touchWorldCoords3D.y);
            entitySelector.selectEntity(touchWorldCoords);

            // if entity got selected, start dragging
            if (entitySelector.getSelectedEntityID().isPresent()){
                isDragging = true;
                lastDragPosition = touchWorldCoords;
            }
            else {
                isDragging = false;
            }

            return  true;
        }
        return false;

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && isDragging) {
            isDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!isDragging || !entitySelector.getSelectedEntityID().isPresent()){
            return false;
        }
        if (selectedTool != SelectedTool.DRAGGING_TOOL){
            return false;
        }
        Vector3 touchScreenCoords3D = new Vector3(screenX, screenY, 0);
        Vector3 touchWorldCoords3D = camera.unproject(touchScreenCoords3D);
        Vector2 touchWorldCoords = new Vector2(touchWorldCoords3D.x, touchWorldCoords3D.y);

        TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class, entitySelector.getSelectedEntityID().getAsInt());

        Vector2 tempTouchWorldCoords = touchWorldCoords.cpy();
        Vector2 dragVector = tempTouchWorldCoords.sub(lastDragPosition);
        transform.position = transform.position.add(dragVector);

        // optionally, add Snapping to grid
//        int gridSize = 16; // pixels
//        transform.x = Math.round((worldX - dragOffsetX) / gridSize) * gridSize;
//        transform.y = Math.round((worldY - dragOffsetY) / gridSize) * gridSize;


        lastDragPosition = touchWorldCoords;

        return true;
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
