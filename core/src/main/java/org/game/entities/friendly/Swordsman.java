package org.game.entities.friendly;

import org.game.entities.types.AttackType;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.managers.TextureManager;

public class Swordsman extends FriendlyCharacter {

    public Swordsman(){
        super("Swordsman", 60, 12, 15, 40, 10, 0,
            0.04f, 0.02f, true, TextureManager.SWORDSMAN,
            AttackType.MELEE, FriendlyType.SWORDSMAN);
    }

}
