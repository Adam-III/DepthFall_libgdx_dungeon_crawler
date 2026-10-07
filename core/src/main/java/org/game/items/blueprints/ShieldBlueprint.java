package org.game.items.blueprints;

import org.game.items.base.BaseShield;
import org.game.items.base.ProtectiveGearBlueprint;

public class ShieldBlueprint extends BaseShield {
    public ShieldBlueprint(ProtectiveGearBlueprint bp){
        super(
            bp.name,
            bp.price,
            bp.hp + (bp.rolledLevel * bp.increment),
            bp.rolledLevel,
            bp.defense,
            bp.texture
        );
        this.upgradeIncrement = bp.increment;
    }

    @Override
    public void updateStats() {
        this.HP += upgradeIncrement;
    }

    @Override
    public int getUpgradeAmount() {
        return upgradeIncrement;
    }
}

