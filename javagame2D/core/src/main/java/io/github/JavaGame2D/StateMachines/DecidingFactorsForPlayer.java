package io.github.JavaGame2D.StateMachines;

import com.badlogic.gdx.math.Vector2;
import io.github.JavaGame2D.Enums.PlayerAction;

public class DecidingFactorsForPlayer {
    // PLAYER INPUT:
    PlayerAction horizontalMovementAction = PlayerAction.NONE;
    PlayerAction verticalMovementAction = PlayerAction.NONE;
    PlayerAction specialAction = PlayerAction.NONE;

    // STATE FROM COMPONENTS:
    boolean grounded = false;
    boolean canMove = true;
    boolean invincible = false;
    Vector2 direction = new Vector2();
    int currentHP = 1;
}
