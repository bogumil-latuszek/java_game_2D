package io.github.JavaGame2D.Events;

public class PlayerHpChanged {
    public int currentHP;
    public int maxHP;
    public PlayerHpChanged(int currentHP, int maxHP) {
        this.currentHP = currentHP;
        this.maxHP = maxHP;
    }
}
