package org.game.entities.friendly;

import org.game.entities.types.AttackType;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.managers.TextureManager;

public class Archer extends FriendlyCharacter {

    public Archer(){
        super("Archer", 40, 16, 15, 40, 100, 10,
            0.02f, 0.08f, true, TextureManager.ARCHER,
            AttackType.RANGE, FriendlyType.ARCHER);
        this.projectileTexture = TextureManager.PROJECTILE;
    }

}
