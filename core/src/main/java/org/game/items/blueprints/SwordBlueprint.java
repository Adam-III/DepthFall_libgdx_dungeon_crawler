package org.game.items.blueprints;

import org.game.items.base.BaseWeapon;
import org.game.items.base.EquipmentMode;
import org.game.items.base.WeaponBlueprint;

public class SwordBlueprint extends BaseWeapon {
    public SwordBlueprint(WeaponBlueprint bp){
        super(
            bp.name,
            bp.price,
            bp.attack + (bp.rolledLevel * bp.increment),
            bp.rolledLevel,
            EquipmentMode.SWORD,
            bp.texture
        );
        this.upgradeIncrement = bp.increment;
    }

    @Override
    public void updateStats() {
        this.attack += upgradeIncrement;
    }

    @Override
    public int getUpgradeAmount() {
        return upgradeIncrement;
    }
}
