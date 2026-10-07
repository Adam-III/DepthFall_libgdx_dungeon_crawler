package org.game.managers;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.entities.abilities.AbilityStrategy;
import org.game.entities.abilities.HealingAbility;
import org.game.entities.abilities.MultishotAbility;
import org.game.entities.abilities.StunAbility;
import org.game.entities.base.Character;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.friendly.*;
import org.game.entities.types.FriendlyType;
import org.game.items.base.BaseArmor;
import org.game.items.base.BaseShield;
import org.game.items.base.BaseWeapon;
import org.game.items.base.Item;
import org.game.items.blueprints.BowBlueprint;
import org.game.items.blueprints.StaffBlueprint;
import org.game.items.blueprints.SwordBlueprint;

/**
 * Manages the player's active party, formation slots, and hero unlocks.
 * Also handles automatic gear assignment and ability synchronization.
 */
public class PartyManager {
    private Character[] meleeSlots = new Character[3];
    private Character[] rangedSlots = new Character[3];
    private Character[] party = new Character[6];

    private Array<Character> globalUnlockedHeroes = new Array<>();
    private Array<Character> unlockableHeroes = new Array<>();
    private final GameManager game;
    private int currentUnits;
    private Array<AbilityStrategy> unlockedAbilities = new Array<>();

    public PartyManager(GameManager game) {
        this.game = game;
        seedHeroPools();
    }

    /** Fill the unlockable pool with the default lineup and unlock the Swordsman. */
    private void seedHeroPools() {
        unlockableHeroes.add(new Archer());
        unlockableHeroes.add(new Knight());
        unlockableHeroes.add(new Healer());
        unlockableHeroes.add(new Tank());
        unlockableHeroes.add(new Mage());
        unlockHero(new Swordsman());
    }

    public Character[] getMeleeSlots()  { return meleeSlots; }
    public Character[] getRangedSlots() { return rangedSlots; }
    public Character[] getParty()       { return party; }

    public void unlockHero(Character c) {
        globalUnlockedHeroes.add(c);
        unlockableHeroes.removeValue(c, true);
    }

    public Array<Character> getGlobalUnlockedHeroes() { return globalUnlockedHeroes; }
    public Array<Character> getUnlockableHeroes()     { return unlockableHeroes; }
    public int getCurrentUnits()                      { return currentUnits; }

    public void addMelee(Character c) {
        for (int i = 0; i < 3; i++) {
            if (meleeSlots[i] == null) {
                meleeSlots[i] = c;
                party[i] = c;
                currentUnits++;
                equipBestGear(c);
                equipAbilities(c);
                return;
            }
        }
    }

    public void addRanged(Character c) {
        for (int i = 0; i < 3; i++) {
            if (rangedSlots[i] == null) {
                rangedSlots[i] = c;
                party[i + 3] = c;
                currentUnits++;
                equipBestGear(c);
                equipAbilities(c);
                return;
            }
        }
    }

    public void clearParty() {
        for (int i = 0; i < 3; i++) meleeSlots[i] = null;
        for (int i = 0; i < 3; i++) rangedSlots[i] = null;
        for (int i = 0; i < 6; i++) party[i] = null;
        currentUnits = 0;
    }

    public boolean isMeleeFull() {
        for (Character meleeSlot : meleeSlots) if (meleeSlot == null) return false;
        return true;
    }

    public boolean isRangedFull() {
        for (Character rangedSlot : rangedSlots) if (rangedSlot == null) return false;
        return true;
    }

    public void refillMana(float percentage) {
        for (int i = 0; i < 6; i++) {
            Character c = party[i];
            if (c != null) c.addMana((int)(c.getMaxMana() * percentage));
        }
    }

    public void addAbility(AbilityStrategy ability) {
        unlockedAbilities.add(ability);
        for (Character c : party) if (c != null) equipAbilities(c);
    }

    public void equipAbilities(Character character) {
        character.getAbilities().clear();

        if (character.getName().equals("Healer")) {
            for (AbilityStrategy ability : unlockedAbilities) {
                if (ability instanceof HealingAbility) {
                    character.addAbility(new HealingAbility());
                    break;
                }
            }
        } else if (character.getName().equals("Tank")) {
            for (AbilityStrategy ability : unlockedAbilities) {
                if (ability instanceof StunAbility) {
                    character.addAbility(new StunAbility());
                    break;
                }
            }
        } else if (character.getName().equals("Archer")) {
            for (AbilityStrategy ability : unlockedAbilities) {
                if (ability instanceof MultishotAbility) {
                    character.addAbility(new MultishotAbility());
                    break;
                }
            }
        }
    }

    private void equipBestGear(Character character) {
        FriendlyCharacter c = (FriendlyCharacter) character;
        Array<Item> inventory = game.getInventory().getInventory();

        BaseShield shield = findBestShield(inventory);
        if (shield != null) {
            c.equipShield(shield);
            shield.setCurrentCharacter(c);
            game.getInventory().removeFromInventory(shield);
            game.getInventory().addToEquippedInventory(shield);
        }

        BaseArmor armor = findBestArmor(inventory);
        if (armor != null) {
            c.equipArmor(armor);
            armor.setCurrentCharacter(c);
            game.getInventory().removeFromInventory(armor);
            game.getInventory().addToEquippedInventory(armor);
        }

        BaseWeapon weapon = findBestWeapon(inventory, c.getFriendlyType());
        if (weapon != null) {
            c.equipWeapon(weapon);
            weapon.setCurrentCharacter(c);
            game.getInventory().removeFromInventory(weapon);
            game.getInventory().addToEquippedInventory(weapon);
        }
    }

    public BaseShield findBestShield(Array<Item> inventory) {
        if (inventory == null || inventory.size == 0) return null;
        BaseShield bestShield = null;
        int maxHp = -1;
        for (Item item : inventory) {
            if (item instanceof BaseShield current) {
                if (current.getHP() > maxHp) {
                    maxHp = current.getHP();
                    bestShield = current;
                }
            }
        }
        return bestShield;
    }

    public BaseArmor findBestArmor(Array<Item> inventory) {
        if (inventory == null || inventory.size == 0) return null;
        BaseArmor bestArmor = null;
        int maxHp = -1;
        for (Item item : inventory) {
            if (item instanceof BaseArmor current) {
                if (current.getHP() > maxHp) {
                    maxHp = current.getHP();
                    bestArmor = current;
                }
            }
        }
        return bestArmor;
    }

    public BaseWeapon findBestWeapon(Array<Item> inventory, FriendlyType type) {
        if (inventory == null || inventory.size == 0) return null;
        BaseWeapon bestWeapon = null;
        int maxAttack = -1;
        for (Item item : inventory) {
            if (item instanceof BaseWeapon weapon) {
                boolean matchesType = false;
                switch (type) {
                    case SWORDSMAN -> { if (weapon instanceof SwordBlueprint) matchesType = true; }
                    case MAGE      -> { if (weapon instanceof StaffBlueprint) matchesType = true; }
                    case ARCHER    -> { if (weapon instanceof BowBlueprint)   matchesType = true; }
                }
                if (matchesType && weapon.getAttack() > maxAttack) {
                    maxAttack = weapon.getAttack();
                    bestWeapon = weapon;
                }
            }
        }
        return bestWeapon;
    }

    // ====================================================================
    // SAVE-SYSTEM HELPERS — used by SaveManager when restoring state.
    // None of these are part of the normal gameplay loop.
    // ====================================================================

    /** Wipe and re-seed the unlockable / unlocked hero pools. */
    public void resetUnlocks() {
        unlockableHeroes.clear();
        globalUnlockedHeroes.clear();
        seedHeroPools();
    }

    /** Move the named hero from the unlockable pool into the unlocked pool. */
    public void unlockHeroByName(String name) {
        for (int i = 0; i < unlockableHeroes.size; i++) {
            Character c = unlockableHeroes.get(i);
            if (c.getName().equals(name)) {
                unlockHero(c);
                return;
            }
        }
    }

    /** Forget every globally-unlocked ability. */
    public void resetAbilities() {
        unlockedAbilities.clear();
    }

    /**
     * Add an ability to the unlocked pool WITHOUT propagating it to the party.
     * Used during save-restore when the party hasn't been rebuilt yet.
     */
    public void addAbilitySilent(AbilityStrategy ability) {
        unlockedAbilities.add(ability);
    }

    /** Place a character into a specific melee slot index (0..2). */
    public void setMeleeSlot(int index, Character c) {
        if (index < 0 || index >= 3) return;
        if (meleeSlots[index] == null && c != null) currentUnits++;
        if (meleeSlots[index] != null && c == null) currentUnits--;
        meleeSlots[index] = c;
        party[index] = c;
    }

    /** Place a character into a specific ranged slot index (0..2). */
    public void setRangedSlot(int index, Character c) {
        if (index < 0 || index >= 3) return;
        if (rangedSlots[index] == null && c != null) currentUnits++;
        if (rangedSlots[index] != null && c == null) currentUnits--;
        rangedSlots[index] = c;
        party[index + 3] = c;
    }
}
