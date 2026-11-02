package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.EditScreen;
import io.github.JavaGame2D.Enums.SelectedTool;
import io.github.JavaGame2D.UserInterface.PropertyInspector;

import java.util.OptionalInt;

public class EditorInputProcessor implements InputProcessor {

    private EntitySelector entitySelector;
    private OrthographicCamera camera;
    private SelectedTool selectedTool;
    private boolean isDragging = false;
    private EntityComponentManager ecm;
    private PropertyInspector propertyInspector;
    private EditScreen editScreen; //TODO: remove circular dependance?

    private Vector2 entityDragOrigin = new Vector2();
    private Vector2 mouseDragOrigin;

    public EditorInputProcessor(EntityComponentManager ecm, EntitySelector entitySelector, PropertyInspector propertyInspector, OrthographicCamera camera, EditScreen editScreen) {
        this.ecm = ecm;
        this.entitySelector = entitySelector;
        this.propertyInspector = propertyInspector;
        this.camera = camera;
        this.selectedTool = SelectedTool.DRAGGING_TOOL;
        this.editScreen = editScreen;
    }

    public OptionalInt getSelectedEntityID(){
        return this.entitySelector.getSelectedEntityID();
    }

    @Override
    public boolean keyDown(int keycode) {
        //TODO: implement guards
//        // Guard 1: Don't hijack keys if a TextField/SelectBox has focus.
//        if (editorStage.getKeyboardFocus() != null) return false;
//
//        // Guard 2: Don't open while a modal window (dropdown, dialog) is up.
//        if (dropdown != null) return false;

        boolean shiftHeld = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        if (keycode == Input.Keys.A && shiftHeld) {
            // Guard 3: Don't open when the mouse is over UI.
//            Actor hit = editorStage.hit(Gdx.input.getX(), Gdx.input.getY(), true);
//            if (hit != null) return false;

            // Convert screen to world.
            Vector3 screen = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 world = camera.unproject(screen);

            editScreen.showPrefabDropdown(world.x, world.y);
            return true;
        }

        if (keycode == Input.Keys.DEL || keycode == Input.Keys.FORWARD_DEL) {
            if(!entitySelector.nothingSelected()){
                editScreen.showDeleteConfirmationDialog(entitySelector.getSelectedEntityID().getAsInt());
                return true;
            }
        }
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


            propertyInspector.inspectEntity(entitySelector.getSelectedEntityID());

            // if entity got selected, start dragging
            if (entitySelector.getSelectedEntityID().isPresent()){
                isDragging = true;
                // no need to check if selected entity has transform, as only entities with transform can be selected
                TransformComponent transform = ecm.getComponent(TransformComponent.class, entitySelector.getSelectedEntityID().getAsInt());
                this.entityDragOrigin = transform.position.cpy();
                this.mouseDragOrigin = touchWorldCoords;
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
            // reset drag variables
            this.mouseDragOrigin = new Vector2();
            this.entityDragOrigin = new Vector2();
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
        //System.out.println("touch dragging, entity origin:" + this.entityDragOrigin.toString() + " , mouse origin:" + this.mouseDragOrigin);

        if (!isDragging || !entitySelector.getSelectedEntityID().isPresent()){
            return false;
        }
        if (selectedTool != SelectedTool.DRAGGING_TOOL){
            return false;
        }
        Vector3 touchScreenCoords3D = new Vector3(screenX, screenY, 0);
        Vector3 touchWorldCoords3D = camera.unproject(touchScreenCoords3D);
        Vector2 touchWorldCoords = new Vector2(touchWorldCoords3D.x, touchWorldCoords3D.y);

        TransformComponent transform = ecm.getComponent(TransformComponent.class, entitySelector.getSelectedEntityID().getAsInt());

        Vector2 tempTouchWorldCoords = touchWorldCoords.cpy();
        Vector2 dragVector = tempTouchWorldCoords.sub(mouseDragOrigin);

        if (editScreen.snapToGrid){
            //TODO: take zoom into account
            // for now, we always snap to 0.5 unit
            transform.position.x = (float)Math.round((this.entityDragOrigin.x + dragVector.x)*2) / 2;
            transform.position.y = (float)Math.round((this.entityDragOrigin.y + dragVector.y)*2) / 2;
        }
        else {
            transform.position.x = (this.entityDragOrigin.x + dragVector.x);
            transform.position.y = (this.entityDragOrigin.y + dragVector.y);
        }

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
