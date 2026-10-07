package org.game.entities.base;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import org.game.entities.types.AttackType;
import org.game.entities.types.FriendlyType;
import org.game.managers.AnimationManager;

import java.util.Random;

public abstract class FriendlyCharacter extends Character {
    private static final Random RNG = new Random();
    protected FriendlyType type;

    public FriendlyCharacter(String name, int maxHP, int attack, int attackSpeed, int maxMana,
                             int price, int unlockPrice, float defense, float criticalChance,
                             boolean isFriendly, Texture texture,
                             AttackType attackType, FriendlyType type) {
        this.name = name;
        this.baseMaxHP = maxHP;
        this.maxHP = baseMaxHP;
        this.currentHP = maxHP;
        this.baseAttack = attack;
        this.attack = baseAttack;
        this.maxMana = maxMana;
        this.currentMana = maxMana;
        this.price = price;
        this.unlockPrice = unlockPrice;
        this.baseDefense = defense;
        this.defense = baseDefense;
        this.attackSpeed = attackSpeed;
        this.isFriendly = isFriendly;
        this.texture = texture;
        this.attackType = attackType;
        this.criticalChance = criticalChance;
        this.type = type;
    }

    public FriendlyType getFriendlyType() { return type; }
    public int getCurrentMana()           { return currentMana; }

    /**
     * Save-system helper. Restores currentMana, clamped to [0, maxMana].
     */
    public void setCurrentMana(int mana) {
        if (mana < 0) mana = 0;
        if (mana > maxMana) mana = maxMana;
        this.currentMana = mana;
    }

    @Override
    public void performDirectAttack(Character target) {
        if (target != null && target.isAlive()) {
            float minMult = 0.9f;
            float maxMult = 1.1f;
            float randomMult = minMult + RNG.nextFloat() * (maxMult - minMult);
            int finalDamage = (int) Math.ceil(this.attack * randomMult);

            if (RNG.nextFloat() < criticalChance + upgrades.getCriticalHitChance()) {
                finalDamage = (int) (finalDamage * (1.5f + upgrades.getCriticalHitMultiplier()));
                isCritical = true;
            }
            target.takeDamage(finalDamage);
        }
    }

    @Override
    public String getAttackIcon() {
        switch (this.type) {
            case ARCHER: return "ui/battle/attackRanged.png";
            case MAGE:   return "ui/battle/attackMage.png";
            default:     return "ui/battle/attackMelee.png";
        }
    }

    @Override
    public int attack(Array<Character> enemies, Array<Character> allies, AnimationManager animation) {
        return 0;
    }
}
