package org.game.items.base;

import com.badlogic.gdx.graphics.Texture;

public abstract class BaseWeapon extends Item {
    protected int  attack;
    protected EquipmentMode weaponType;

    public BaseWeapon(String name, int basePrice, int attack, int level,
                      EquipmentMode weaponType, Texture texture){
        this.name = name;
        this.basePrice = basePrice;
        this.level = level;
        this.attack = attack;
        this.texture = texture;
        this.weaponType = weaponType;

        this.price = this.basePrice * this.level;
        this.sellPrice = (int)(price * SELL_RATIO);
        this.upgradePrice = price * 10;
    }


    public int getAttack() { return attack; }
    public EquipmentMode getWeaponType() { return weaponType; }

}
