package org.game.entities.blueprints;

import org.game.entities.base.EnemyBlueprint;
import org.game.entities.base.EnemyBlueprintRanged;
import org.game.entities.base.HostileCharacter;

public class RangedEnemy extends HostileCharacter {

    public RangedEnemy(EnemyBlueprint bp, int level) {
        super(bp.name,
            bp.hp + (level * bp.hpInc),
            bp.atk + (level * bp.atkInc),
            bp.speed, bp.defense, level,
            false, bp.texture, bp.type, bp.enemyType);

        if (bp instanceof EnemyBlueprintRanged) {
            this.projectileTexture = ((EnemyBlueprintRanged) bp).projectileTexture;
        }
    }

}
