package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.SegmentedDrawableComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.SpriteData;

import java.util.OptionalInt;

public class EntitySelector {

    private EntityComponentManager entityComponentManager;
    private OptionalInt selectedEntityID;
    private TextureManager textureManager;

    public OptionalInt getSelectedEntityID(){
        return  this.selectedEntityID;
    }

    public boolean nothingSelected(){
        return !this.selectedEntityID.isPresent();
    }

    public EntitySelector(EntityComponentManager entityComponentManager, TextureManager textureManager) {
        this.entityComponentManager = entityComponentManager;
        this.selectedEntityID = OptionalInt.empty();
        this.textureManager = textureManager;
    }

    public OptionalInt selectEntity(Vector2 pointer){
        OptionalInt foundEntity = findEntityTouchingPoint(pointer);
        selectedEntityID = foundEntity;
        System.out.println("selected entity: "+selectedEntityID);
        return selectedEntityID;
    }

    public OptionalInt findEntityTouchingPoint(Vector2 pointInWorldCoords){
        // 1. get all entities with drawable component
        long drawableSignature = ComponentSignatures.TRANSFORM | ComponentSignatures.DRAWABLE;
        int[] drawableEntities = entityComponentManager.getEntitiesMatchingSignature(drawableSignature);

        // 2. check collision for each one, return first found
        for (int entityID : drawableEntities){
            TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class, entityID);
            DrawableComponent drawable = entityComponentManager.getComponent(DrawableComponent.class, entityID);
            Vector2 center = transform.position;

            if (drawable.hasSegmentedBody){
                SegmentedDrawableComponent segmentedDrawable = entityComponentManager.getComponent(SegmentedDrawableComponent.class, entityID);
                for (SpriteData segmentSprite : segmentedDrawable.drawableSegments.values()){
                    if (this.pointInsideSpriteData(pointInWorldCoords, segmentSprite, center)){
                        return OptionalInt.of(entityID);
                    }
                }
            }
            else {
                SpriteData drawableSprite = drawable.spriteData;

                if (this.pointInsideSpriteData(pointInWorldCoords, drawableSprite, center)){
                    return OptionalInt.of(entityID);
                }
            }
        }

        // 3. if none found, find all without drawable component,  give them temporary circle selection zones, and do the same with them

        // no collisions found
        return OptionalInt.empty();
    }

    private boolean pointInsideSpriteData(Vector2 point, SpriteData sprite, Vector2 center){
        Vector2 offsetCenter = center.cpy();
        offsetCenter = offsetCenter.add(sprite.offset);

        float width = sprite.width;
        float height = sprite.height;

        if (sprite.usesSizeFromTexture && !sprite.textureIsTiled){
            Texture texture = textureManager.getTexture(sprite.textureID);
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
