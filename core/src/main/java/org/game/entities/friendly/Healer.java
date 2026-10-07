package org.game.entities.friendly;

import org.game.entities.types.AttackType;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.managers.TextureManager;

public class Healer extends FriendlyCharacter {

    public Healer(){
        super("Healer", 25, 5, 15, 80, 600, 100,
            0.02f, 0f, true, TextureManager.HEALER,
            AttackType.RANGE, FriendlyType.MAGE);
        this.projectileTexture = TextureManager.PROJECTILE;
    }

}
