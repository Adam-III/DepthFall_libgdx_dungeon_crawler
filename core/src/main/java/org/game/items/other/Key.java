package org.game.items.other;

import org.game.items.base.Item;
import org.game.managers.TextureManager;

public class Key extends Item {

    public Key(){
        this.name = "Key";
        this.basePrice = 40;
        this.level = 1;
        this.price = basePrice * this.level;
        this.texture = TextureManager.KEY;
        this.sellPrice = 0; // Keys cannot be sold
    }

    @Override
    public void updateStats() {

    }

    @Override
    public int getUpgradeAmount() {
        return 0;
    }
}
