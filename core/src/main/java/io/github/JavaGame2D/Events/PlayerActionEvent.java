package io.github.JavaGame2D.Events;

import io.github.JavaGame2D.Enums.PlayerAction;

public class PlayerActionEvent {
    public PlayerAction horizontalMovementAction = PlayerAction.NONE;
    public PlayerAction verticalMovementAction = PlayerAction.NONE;
    public PlayerAction specialAction = PlayerAction.NONE;
}
