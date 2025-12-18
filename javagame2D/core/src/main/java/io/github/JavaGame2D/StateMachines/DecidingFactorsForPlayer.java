package io.github.JavaGame2D.StateMachines;

import com.badlogic.gdx.math.Vector2;

public class DecidingFactorsForPlayer {
    // PLAYER INPUT:

    // DirectionCommand: LEFT, RIGHT, UP, DOWN
    // MovementCommand: RUN, CROUCH, JUMP
    // ActionCommand: ATTACK, BLOCK, USE_ITEM_1, USE_ITEM_2

    // STATE FROM COMPONENTS:

    boolean grounded = false;
    boolean canMove = true;
    boolean invincible = false;
    Vector2 direction = new Vector2();
    int currentHP = 1;

}
