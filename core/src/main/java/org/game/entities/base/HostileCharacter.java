package org.game.entities.base;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import org.game.entities.types.AttackType;
import org.game.entities.types.EnemyType;
import org.game.managers.AnimationManager;

/**
 * Base class for all enemy units.
 * Implements automated AI targeting logic for melee and ranged attacks.
 */
public abstract class HostileCharacter extends Character {
    EnemyType type;

    public HostileCharacter(String name, int maxHP, int attack, int attackSpeed,
                            float defense, int level, boolean isFriendly, Texture texture,
                            AttackType attackType, EnemyType type) {
        this.level =level;
        this.name = name;
        this.maxHP = maxHP;
        this.currentHP = this.maxHP;
        this.attack = attack;
        this.defense = defense;
        this.attackSpeed = attackSpeed;
        this.isFriendly = isFriendly;
        this.texture = texture;
        this.attackType = attackType;
        this.type = type;
        this.criticalChance = 0.02f;
    }

    @Override
    public int attack(Array<Character> enemies, Array<Character> allies, AnimationManager animation) {
        Array<Character> potentialTargets = this.isFriendly ? enemies : allies;
        Character finalTarget = null;

        // Simple AI: Melee units must hit the front row first, Ranged can hit anyone
        switch (this.attackType) {
            case MELEE:
                finalTarget = findMeleeTarget(potentialTargets);
                break;

            case RANGE:
                finalTarget = findFirstAlive(potentialTargets);
                break;
        }
        if (finalTarget != null) {
            animation.startAttackAnimation(this, finalTarget);
        }

        return (finalTarget != null) ? this.attack : 0;
    }

    /** Melee units must hit an alive front-row (melee) unit first, then anyone left. */
    private Character findMeleeTarget(Array<Character> targets) {
        for (Character c : targets) {
            if (c != null && c.isAlive() && c.isMelee()) return c;
        }
        return findFirstAlive(targets);
    }

    private Character findFirstAlive(Array<Character> targets) {
        for (Character c : targets) {
            if (c != null && c.isAlive()) return c;
        }
        return null;
    }

    @Override
    public String getAttackIcon(){ return null; }
}
