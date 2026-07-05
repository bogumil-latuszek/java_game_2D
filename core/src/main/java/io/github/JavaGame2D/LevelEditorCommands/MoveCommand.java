package io.github.JavaGame2D.LevelEditorCommands;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Components.TransformComponent;
import io.github.JavaGame2D.Systems.EntityComponentManager;

public class MoveCommand implements ICommand{

    private EntityComponentManager entityComponentManager;
    private Vector2 oldPosition;
    private Vector2 newPosition;
    private int entityID;

    public MoveCommand(Vector2 oldPosition, Vector2 newPosition, int entityID, EntityComponentManager entityComponentManager) {
        this.entityComponentManager = entityComponentManager;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.entityID = entityID;
    }


    @Override
    public void execute() {
        TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class, entityID);
        transform.position = newPosition;
    }

    @Override
    public void undo() {
        TransformComponent transform = entityComponentManager.getComponent(TransformComponent.class, entityID);
        transform.position = oldPosition;
    }

    @Override
    public String getDescription() {
        return "move entity";
    }
}
