package io.github.JavaGame2D;

import io.github.JavaGame2D.Components.*;

import java.util.BitSet;

public class Entity {
    public int ID;
    public long signature;

    public Entity(int ID) {
        this.ID = ID;
        //TODO: MAKE SURE THE SIZE OF BITSET IS EQUAL TO AN AMOUNT OF COMPONENTS USED IN GAME
        signature = 0L;
    }
}
