package org.game.items.base;

import com.badlogic.gdx.graphics.Texture;
import org.game.entities.base.Character;

public abstract class Item {
    protected int basePrice, price, sellPrice, upgradePrice, level, upgradeIncrement;
    protected String name;
    protected Texture texture;
    protected Character currentCharacter;

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getSellPrice() { return sellPrice; }
    public int getUpgradePrice() { return upgradePrice; }
    public int getLevel() { return level; }
    public Texture getTexture() { return texture; }
    public Character getCurrentCharacter() { return currentCharacter; }
    public abstract void updateStats();
    public abstract int getUpgradeAmount();


    /** Share of the item price the player gets back when selling. */
    public static final float SELL_RATIO = 0.6f;

    public void levelUp() {
        this.level ++;
        this.price = this.basePrice * this.level;
        this.sellPrice = (int)(price * SELL_RATIO);
        this.upgradePrice = price * 10;

        updateStats();
    }

    public void setCurrentCharacter (Character c) {
        this.currentCharacter = c;
    }

}
