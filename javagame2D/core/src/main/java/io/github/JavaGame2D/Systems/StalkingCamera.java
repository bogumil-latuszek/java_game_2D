package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ComponentType;

public class StalkingCamera {
    private Entity followedEntity;
    private OrthographicCamera camera;


    public StalkingCamera() {
        int cameraWidth = 30; // in game units
        float aspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        camera = new OrthographicCamera(cameraWidth,cameraWidth*aspectRatio);
    }

    public void resizeCamera(int width, int height){
        this.camera.viewportWidth = width;
        this.camera.viewportHeight = height;
    }

    public void followEntity(Entity entity){
        if(entity.hasComponent(ComponentType.TRANSFORM)){
            followedEntity = entity;
        }
    }

    public void update(){
        if (followedEntity == null){
            return;
        }
        TransformComponent transform = (TransformComponent) followedEntity.getComponent(ComponentType.TRANSFORM);
        Vector2 stalkedEntityPosition = transform.position;
        this.camera.position.x = stalkedEntityPosition.x;
        this.camera.position.y = stalkedEntityPosition.y;

        //very important! without this update call the projection matrix won't update!
        this.camera.update();
    }


    public Matrix4 getProjectionMatrix(){
        return this.camera.combined;
    }
}
