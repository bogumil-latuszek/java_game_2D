package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.UserInterface.EditorVisibilityState;

import java.util.OptionalInt;

public class EntitySelector {

    private EntityComponentManager ecm;
    private OptionalInt selectedEntityID;
    private TextureManager textureManager;
    private EditorVisibilityState visibilityState;

    public OptionalInt getSelectedEntityID(){
        return  this.selectedEntityID;
    }

    public boolean nothingSelected(){
        return !this.selectedEntityID.isPresent();
    }

    public EntitySelector(EntityComponentManager ecm, TextureManager textureManager, EditorVisibilityState visibilityState) {
        this.ecm = ecm;
        this.selectedEntityID = OptionalInt.empty();
        this.textureManager = textureManager;
        this.visibilityState = visibilityState;
    }

    // most likely not needed since visibilityState in EditScreen is Final
//    public void setVisibilityState(EditorVisibilityState visibilityState){
//        this.visibilityState = visibilityState;
//    }

    public void setSelectedEntityID(int entityID){
        this.selectedEntityID = OptionalInt.of(entityID);
    }

    public OptionalInt selectEntity(Vector2 pointer){
        OptionalInt foundEntity = findEntityTouchingPoint(pointer);
        selectedEntityID = foundEntity;
        System.out.println("selected entity: "+selectedEntityID);
        return selectedEntityID;
    }

    public OptionalInt findEntityTouchingPoint(Vector2 pointInWorldCoords){
        // 1. get all entities with drawable component
        long drawableSignature = ComponentSignatures.get(TransformComponent.class) | ComponentSignatures.get(DrawableComponent.class);
        int[] drawableEntities = ecm.getEntitiesMatchingSignature(drawableSignature);

        // 2. check collision for each one, return first found
        for (int entityID : drawableEntities){
            if (visibilityState.getHiddenEntities().contains(entityID) || visibilityState.getLockedEntities().contains(entityID)){
                continue; // skip hidden/locked entities
            }
            TransformComponent transform = ecm.getComponent(TransformComponent.class, entityID);
            DrawableComponent drawable = ecm.getComponent(DrawableComponent.class, entityID);
            if (visibilityState.getHiddenLayers().contains(drawable.renderingLayer) || visibilityState.getLockedLayers().contains(drawable.renderingLayer)){
                continue; // skip hidden/locked layers
            }
            Vector2 center = transform.position;

            if (this.pointInsideDrawable(pointInWorldCoords, drawable, center)){
                return OptionalInt.of(entityID);
            }
        }

        // 3. if none found, find all without drawable component,  give them temporary circle selection zones, and do the same with them

        // no collisions found
        return OptionalInt.empty();
    }

    private boolean pointInsideDrawable(Vector2 point, DrawableComponent drawable, Vector2 center){
        Vector2 offsetCenter = center.cpy();
        offsetCenter = offsetCenter.add(drawable.offset);

        float width = drawable.width;
        float height = drawable.height;

        if (drawable.usesSizeFromTexture && !drawable.textureIsTiled){
            Texture texture = textureManager.getTexture(drawable.textureID);
            int texturePixelHeight = texture.getHeight();
            int texturePixelWidth = texture.getWidth();
            width = texturePixelWidth/32f;
            height = texturePixelHeight/32f;
        }
        return pointInsideRectangle(point, offsetCenter, width, height);
    }

    private boolean pointInsideRectangle(Vector2 point, Vector2 center, float width, float height){
        if ((center.x-width/2 < point.x && point.x < center.x+width/2)&&
            (center.y-height/2 < point.y && point.y < center.y+height/2)) {
            return true;
        }
        return false;
    }
}
