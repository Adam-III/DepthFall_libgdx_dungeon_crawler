package org.game.entities.bosses;

import org.game.entities.types.AttackType;
import org.game.entities.types.EnemyType;
import org.game.entities.base.HostileCharacter;
import org.game.managers.TextureManager;

public class RedDragon extends HostileCharacter {
    public RedDragon(int level){
        super("RedDragon",
            200 * level,
            25 + (level * 15),
            1, 0.1f, level,
            false, TextureManager.RED_DRAGON,
            AttackType.MELEE, EnemyType.BOSS);
        this.width = 128;
        this.height = 128;
        this.criticalChance = 0.1f;
    }


}
