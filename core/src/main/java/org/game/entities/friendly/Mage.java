package org.game.entities.friendly;

import org.game.entities.types.AttackType;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.managers.TextureManager;

public class Mage extends FriendlyCharacter {

    public Mage(){
        super("Mage", 30, 20, 15, 80, 1500, 300,
            0f, 0.05f, true, TextureManager.MAGE,
            AttackType.RANGE, FriendlyType.MAGE);
        this.projectileTexture = TextureManager.PROJECTILE;
    }

}
