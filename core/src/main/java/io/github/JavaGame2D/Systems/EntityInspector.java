package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.ComponentSignatures;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.SpriteData;

import java.beans.beancontext.BeanContextChild;
import java.util.OptionalInt;

public class EntityInspector {

    EntityComponentManager entityComponentManager;
    private OptionalInt selectedEntityID;

    public OptionalInt getSelectedEntityID(){
        return  this.selectedEntityID;
    }

    public EntityInspector(EntityComponentManager entityComponentManager) {
        this.entityComponentManager = entityComponentManager;
        this.selectedEntityID = OptionalInt.empty();
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

            SpriteData sprite = drawable.spriteData;
            if (drawable.hasSegmentedBody){
                // check for each segment
            }

            Vector2 center = transform.position.add(sprite.offset);
            if (isPointInsideRectangle(pointInWorldCoords, center, sprite.width, sprite.height)) {
                return OptionalInt.of(entityID);
            }
        }

        // 3. if none found, find all without drawable component,  give them temporary circle selection zones, and do the same with them

        // no collisions found
        return OptionalInt.empty();
    }

    private boolean isPointInsideRectangle(Vector2 point, Vector2 center, float width, float height){
        if ((center.x-width/2 < point.x && point.x < center.x+width/2)&&
            (center.y-height/2 < point.y && point.y < center.y+height/2)) {
            return true;
        }
        return false;
    }
}
