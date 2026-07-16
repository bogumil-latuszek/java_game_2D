package io.github.JavaGame2D.Systems;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.EventBus;
import io.github.JavaGame2D.Events.PlayerIDChanged;

public class PlayerCameraController{

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private TransformComponent followedTransformComponent = null;
    private EntityComponentManager entityComponentManager;

    public PlayerCameraController(OrthographicCamera camera, float minWorldWidth, float minWorldHeight, EntityComponentManager entityComponentManager) {
        this.camera = camera;
        this.viewport = new ExtendViewport(minWorldWidth,minWorldHeight, camera);
        this.entityComponentManager = entityComponentManager;
        EventBus.getInstance().subscribe(PlayerIDChanged.class, this::handlePlayerIDChanged);
        this.camera.zoom = 1f; //reset camera zoom that may have changed in Editor Mode
    }

    private void handlePlayerIDChanged(PlayerIDChanged event){
        setEntityFollowedByCamera(event.playerEntityID);
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

    public void setEntityFollowedByCamera(int entityID){
        TransformComponent transformComponent = entityComponentManager.getComponent(TransformComponent.class,entityID);
        followTransformComponent(transformComponent);
    }

    public void followTransformComponent(TransformComponent transformComponent){
        this.followedTransformComponent = transformComponent;
    }

    public void resizeViewport(int width, int height){
        this.viewport.update(width,height);
    }

}
