package org.game.entities.blueprints;

import org.game.entities.base.EnemyBlueprint;
import org.game.entities.base.HostileCharacter;

/**
 * Concrete implementation of a melee-type enemy.
 * This class maps data from an EnemyBlueprint and scales stats based on the enemy's level.
 */
public class MeleeEnemy extends HostileCharacter {

    /**
     * Constructs a new MeleeEnemy by applying level-based scaling to blueprint values.
     * * @param bp The blueprint containing base stats and textures.
     * @param level The target level of the enemy, used to calculate final HP and Attack.
     */
    public MeleeEnemy(EnemyBlueprint bp, int level) {
        super(
            bp.name,
            // Dynamic HP scaling: base health + (level * health increment)
            bp.hp + (level * bp.hpInc),
            // Dynamic Attack scaling: base attack + (level * attack increment)
            bp.atk + (level * bp.atkInc),
            bp.speed,
            bp.defense,
            level,
            false, // isFriendly is always false for HostileCharacter subclasses
            bp.texture,
            bp.type,
            bp.enemyType
        );
    }
}
