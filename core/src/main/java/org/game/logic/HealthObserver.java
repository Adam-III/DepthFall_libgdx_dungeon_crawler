package org.game.logic;

public interface HealthObserver {
    void onHealthChanged(int currentHP, int maxHP);
}
