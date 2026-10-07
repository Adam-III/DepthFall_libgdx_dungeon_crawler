package org.game.managers;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.entities.base.Character;
import org.game.items.base.BaseArmor;
import org.game.items.base.BaseShield;
import org.game.items.base.BaseWeapon;
import org.game.items.base.Item;
import org.game.items.other.Key;

/**
 * Manages the gear upgrade system.
 * Allows players to increase an item's level and stats by spending gold.
 */
public class GearUpgradeManager {
    private final GameManager game;

    public GearUpgradeManager(GameManager game){
        this.game = game;
        // Resets the inventory filter to default when the upgrade manager is initialized
        game.getInventory().updateState("DEFAULT");
    }

    /**
     * Attempts to upgrade a specific item.
     * Checks for sufficient gold, deducts the price, and increases the item's level.
     * * @param item The item to be upgraded.
     * @return true if the upgrade was successful, false if the player is too poor.
     */
    public boolean upgradeItem(Item item) {
        // Check if the player has enough permanent gold (meta-currency)
        if (game.getPermanentGold() >= item.getUpgradePrice()) {
            game.addGold(-(item.getUpgradePrice()));

            // --- Logic for Shields ---
            if(item instanceof BaseShield shield) {
                // If the item is currently equipped, we must temporarily unequip it
                // to correctly recalculate character stats after the level-up
                if(item.getCurrentCharacter() != null){
                    Character c = item.getCurrentCharacter();
                    c.unequipShield();
                    shield.levelUp();
                    c.equipShield(shield);
                } else {
                    shield.levelUp();
                }
            }

            // --- Logic for Armor ---
            if(item instanceof BaseArmor armor) {
                if(item.getCurrentCharacter() != null) {
                    Character c = item.getCurrentCharacter();
                    c.unequipArmor();
                    armor.levelUp();
                    c.equipArmor(armor);
                } else {
                    armor.levelUp();
                }
            }

            // --- Logic for Weapons ---
            if(item instanceof BaseWeapon weapon) {
                if(item.getCurrentCharacter() != null) {
                    Character c = item.getCurrentCharacter();
                    c.unequipWeapon();
                    weapon.levelUp();
                    c.equipWeapon(weapon);
                } else {
                    weapon.levelUp();
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Retrieves a list of all items eligible for an upgrade.
     * Combines standard inventory and currently equipped items.
     * * @return An array of items that are not keys and haven't reached max level (10).
     */
    public Array<Item> getItems() {
        Array<Item> list = new Array<>();
        Array<Item> inventory = game.getInventory().getItems();

        // Add currently equipped items to the temporary list for evaluation
        for(Item item : game.getInventory().getEquipped()) {
            inventory.add(item);
        }

        for(Item item : inventory){
            // Filter out items that cannot be upgraded (Keys or Max Level items)
            if(item instanceof Key) {
                list.add(null);
            } else if(item.getLevel() > 9) {
                list.add(null);
            } else {
                list.add(item);
            }
        }
        return list;
    }
}
