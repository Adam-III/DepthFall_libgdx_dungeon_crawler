package org.game.entities.friendly;

import org.game.entities.types.AttackType;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.managers.TextureManager;

public class Knight extends FriendlyCharacter {

    public Knight(){
        super("Knight", 75, 10, 15, 50, 300, 50,
            0.08f, 0.06f, true, TextureManager.KNIGHT,
            AttackType.MELEE, FriendlyType.SWORDSMAN);
    }

}
