package org.game.entities.base;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import org.game.entities.abilities.AbilityStrategy;
import org.game.entities.types.AttackType;
import org.game.items.base.BaseArmor;
import org.game.items.base.BaseShield;
import org.game.items.base.BaseWeapon;
import org.game.logic.HealthObserver;
import org.game.managers.AnimationManager;
import org.game.managers.GlobalUpgradesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Base abstract class for all combat units in the game.
 */
public abstract class Character {

    protected String name;
    protected int baseMaxHP, maxHP, currentHP, baseAttack, attack, level;
    protected int maxMana, currentMana, attackSpeed, price, unlockPrice;
    protected int healedAmount, damageTaken;

    protected float x, y, width = 64, height = 64;
    protected float visualOffsetX = 0, visualOffsetY = 0;
    protected float criticalChance;
    protected Texture texture, projectileTexture;

    private Rectangle bounds = new Rectangle();
    protected BaseArmor currentArmor;
    protected BaseShield currentShield;
    protected BaseWeapon currentWeapon;
    protected static GlobalUpgradesManager upgrades;
    private static final Random RNG = new Random();

    protected List<AbilityStrategy> abilities = new ArrayList<>();
    private Array<HealthObserver> observers = new Array<>();

    protected AttackType attackType;
    protected float defense, baseDefense;
    protected boolean isFriendly, active = false, hovered, blocking = false, isCritical = false, isStunned = false;

    // --- Getters ---
    public String getName() { return this.name; }
    public int getLevel() { return level; }
    public int getAttack() { return attack;}
    public int getAttackSpeed() { return this.attackSpeed; }
    public boolean isFriendly() { return this.isFriendly; }
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getWidth() { return this.width; }
    public float getHeight() { return this.height; }
    public Texture getTexture() { return this.texture; }
    public boolean isCritical() { return this.isCritical;}
    public AttackType getAttackType() { return this.attackType; }
    public abstract String getAttackIcon();
    public float getVisualOffsetX() { return this.visualOffsetX; }
    public float getVisualOffsetY() { return this.visualOffsetY; }
    public Texture getProjectileTexture() { return this.projectileTexture;}
    public int getDamageTaken() { return this.damageTaken;}
    public int getMaxHP() { return maxHP; }
    public int getCurrentHP() { return currentHP;}
    public BaseShield getCurrentShield() { return currentShield;}
    public int getMaxMana() { return maxMana;}
    public float getDefense() { return defense;}
    public int getPrice() { return price;}
    public int getUnlockPrice() { return unlockPrice; }
    public BaseWeapon getCurrentWeapon() { return currentWeapon; }
    public BaseArmor getCurrentArmor() { return currentArmor; }
    public int getHealedAmount() { return healedAmount; }
    public List<AbilityStrategy> getAbilities() { return abilities; }
    public float getCriticalChance() { return criticalChance; }
    public boolean isStunned() { return isStunned; }

    // --- Setters and State Management ---
    public void resetCritical() { this.isCritical = false; }
    public void resetStun() { this.isStunned = false; }
    public void stun() { this.isStunned = true; }
    public void setHovered(boolean hovered) { this.hovered = hovered; }
    public void setActive(boolean active) { this.active = active; }
    public void setBlocking(boolean blocking) { this.blocking = blocking; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setVisualOffset(float x, float y) { this.visualOffsetX = x; this.visualOffsetY = y; }
    public static void setGlobalUpgrades(GlobalUpgradesManager manager) { Character.upgrades = manager; }

    /**
     * Save-system helper. Restores currentHP, clamped to [0, maxHP].
     * Call this AFTER equipping gear, since gear shifts maxHP.
     */
    public void setCurrentHP(int hp) {
        if (hp < 0) hp = 0;
        if (hp > maxHP) hp = maxHP;
        this.currentHP = hp;
    }

    // --- Core Logic Methods ---
    public void addMana(int amount) {
        this.currentMana = Math.min(currentMana + amount, maxMana);
    }

    public void addObserver(HealthObserver observer) { observers.add(observer); }

    private void notifyObservers() {
        for (HealthObserver o : observers) o.onHealthChanged(currentHP, maxHP);
    }

    public boolean isAlive() { return currentHP > 0; }
    public boolean isBlocking() { return blocking; }

    public abstract int attack(Array<Character> enemies, Array<Character> allies, AnimationManager animation);

    public void heal(int amount) {
        if (currentHP + amount < maxHP) {
            healedAmount = amount;
            currentHP += amount;
        } else {
            healedAmount = maxHP - currentHP;
            currentHP = maxHP;
        }
        notifyObservers();
    }

    public boolean useAbility(int index, Character target, Array<Character> allies, Array<Character> enemies) {
        if (index >= 0 && index < abilities.size()) {
            AbilityStrategy ability = abilities.get(index);
            if (this.currentMana < ability.getManaCost()) {
                return false;
            }
            this.currentMana -= ability.getManaCost();
            ability.execute(this, target, allies, enemies);
            return true;
        }
        return false;
    }

    public void takeDamage(int rawDamage) {
        float calculatedDamage = rawDamage * (1f - defense);
        if (blocking) calculatedDamage *= 0.5f;
        int damage = (int) Math.ceil(calculatedDamage);
        this.currentHP -= damage;
        damageTaken = damage;
        blocking = false;
        if (this.currentHP < 0) currentHP = 0;
        notifyObservers();
    }

    public void performDirectAttack(Character target) {
        if (target != null && target.isAlive()) {
            float minMult = 0.95f;
            float maxMult = 1.05f;
            float randomMult = minMult + RNG.nextFloat() * (maxMult - minMult);
            int finalDamage = (int) Math.ceil(this.attack * randomMult);
            target.takeDamage(finalDamage);
        }
    }

    // --- Utility and Equipment Methods ---
    public boolean isHovered() { return hovered; }
    public boolean isActive() { return active; }

    public void updateBounds() { bounds.set(x, y, width, height); }
    public Rectangle getBounds() { updateBounds(); return bounds; }

    public boolean isMelee() { return this.attackType == AttackType.MELEE; }

    public BaseWeapon unequipWeapon() {
        BaseWeapon weapon = currentWeapon;
        currentWeapon = null;
        attack -= weapon.getAttack();
        return weapon;
    }

    public void equipWeapon(BaseWeapon weapon) {
        currentWeapon = weapon;
        this.attack += weapon.getAttack();
    }

    public BaseArmor unequipArmor() {
        BaseArmor armor = currentArmor;
        currentArmor = null;
        maxHP -= armor.getHP();
        currentHP -= armor.getHP();
        this.defense = roundDefense(this.defense - armor.getDefense());
        return armor;
    }

    public void equipArmor(BaseArmor armor) {
        currentArmor = armor;
        maxHP += armor.getHP();
        currentHP += armor.getHP();
        this.defense = roundDefense(this.defense + armor.getDefense());
    }

    public void equipShield(BaseShield shield) {
        currentShield = shield;
        maxHP += shield.getHP();
        currentHP += shield.getHP();
        this.defense = roundDefense(this.defense + shield.getDefense());
    }

    public BaseShield unequipShield() {
        BaseShield shield = currentShield;
        currentShield = null;
        maxHP -= shield.getHP();
        currentHP -= shield.getHP();
        this.defense = roundDefense(this.defense - shield.getDefense());
        return shield;
    }

    public void addAbility(AbilityStrategy ability) { abilities.add(ability); }

    /** Rounds defense to 2 decimals so repeated equip/unequip does not drift. */
    private static float roundDefense(float value) {
        return Math.round(value * 100f) / 100f;
    }
}
