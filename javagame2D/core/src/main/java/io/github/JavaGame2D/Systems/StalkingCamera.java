package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.ComponentType;

public class StalkingCamera {
    private TransformComponent followedTransformComponent;
    private OrthographicCamera camera;
    private ExtendViewport viewport;


    public StalkingCamera() {
        camera = new OrthographicCamera();
        //viewport = new ExtendViewport(50,35, camera);
        viewport = new ExtendViewport(14,9.8f, camera);
    }

    public void followTransformComponent(TransformComponent transformComponent){
        this.followedTransformComponent = transformComponent;
    }

    public void update(){
        this.viewport.apply();

        if (followedTransformComponent == null){
            return;
        }
        this.camera.position.x =  followedTransformComponent.position.x;
        this.camera.position.y =  followedTransformComponent.position.y;

        //very important! without this update call the projection matrix won't update!
        this.camera.update();
    }

    public void resizeViewport(int width, int height){
        this.viewport.update(width,height);
    }

    public Matrix4 getProjectionMatrix(){
        return this.camera.combined;
    }
}
