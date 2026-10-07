package org.game.entities.friendly;

import org.game.entities.types.AttackType;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.managers.TextureManager;

public class Tank extends FriendlyCharacter {

    public Tank(){
        super("Tank", 100, 2, 15, 40, 1000, 200,
            0.12f, 0f, true, TextureManager.TANK,
            AttackType.MELEE, FriendlyType.SWORDSMAN);
    }

}
