package org.game.items.base;

import com.badlogic.gdx.graphics.Texture;

public abstract class BaseShield extends Item {
    protected int HP;
    protected float defense;

    public BaseShield(String name, int basePrice, int HP, int level, float defense, Texture texture){
        this.name = name;
        this.basePrice = basePrice;
        this.HP = HP;
        this.defense = defense;
        this.level = level;
        this.texture = texture;

        this.price = this.basePrice * this.level;
        this.sellPrice = (int)(price * SELL_RATIO);
        this.upgradePrice = price * 10;
    }

    public int getHP() { return HP; }
    public float getDefense() { return defense; }

}
