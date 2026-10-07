package org.game.managers;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.items.base.*;
import org.game.items.other.Key;

/**
 * Manages the player's items, including the general inventory and items currently
 * equipped by characters. Handles adding, removing, and filtering items.
 */
public class InventoryManager {
    /** List of all items currently owned but not necessarily equipped */
    private final Array<Item> inventory;

    /** List of items that are currently active and worn by party members */
    private Array<Item> equipped;

    /** Current filtering mode used to display specific item types in the UI */
    private EquipmentMode mode;

    private final GameManager game;

    public InventoryManager(GameManager game){
        this.game = game;
        this.inventory = new Array<>();
        this.equipped = new Array<>();
        this.mode = EquipmentMode.DEFAULT;
    }

    /** Adds a single item to the general inventory. */
    public void addToInventory(Item item){
        inventory.add(item);
    }

    /** Removes a specific item instance from the inventory. */
    public void removeFromInventory(Item item){
        if(inventory.contains(item, true)) {
            inventory.removeValue(item, true);
        }
    }

    /** Removes ONE item from the inventory based on its name (case-insensitive). */
    public void removeFromInventory(String name){
        for (int i = 0; i < inventory.size; i++) {
            if (inventory.get(i).getName().equalsIgnoreCase(name)) {
                inventory.removeIndex(i);
                return;
            }
        }
    }

    /** Batch adds a collection of items (loot) to the inventory. */
    public void addLoot(Array<Item> loot) {
        for(Item i : loot) {
            inventory.add(i);
        }
    }

    public Array<Item> getInventory() { return inventory; }

    public Array<Item> getEquipped() { return equipped; }

    /**
     * Updates the filtering mode used by the getItems() method.
     * @param state String representation of the EquipmentMode (SHIELD, WEAPON, ARMOR).
     */
    public void updateState(String state){
        switch(state){
            case "SHIELD" -> this.mode = EquipmentMode.SHIELD;
            case "WEAPON" -> this.mode = EquipmentMode.WEAPON;
            case "ARMOR"  -> this.mode = EquipmentMode.ARMOR;
            default       -> this.mode = EquipmentMode.DEFAULT;
        }
    }

    /**
     * Returns a list of items filtered by the current 'mode'.
     * If the player is in a dungeon, it may pull from the LootManager instead.
     * @return A filtered Array of items.
     */
    public Array<Item> getItems(){
        Array<Item> list = new Array<>();
        // Determine source: standard inventory or temporary loot from the current dungeon run
        Array<Item> source = (game.isDungeonNull()) ? getInventory() : game.getLootManager().getLoot();

        for(Item item : source){
            switch(mode){
                case SHIELD -> {
                    if(item instanceof BaseShield) list.add(item);
                }
                case ARMOR -> {
                    if(item instanceof BaseArmor) list.add(item);
                }
                case WEAPON -> {
                    if(item instanceof BaseWeapon) list.add(item);
                }
                case DEFAULT -> list.add(item);
                default -> list.add(null);
            }
        }
        return list;
    }

    /** Tracks an item as being currently equipped by a character. */
    public void addToEquippedInventory(Item item) {
        equipped.add(item);
    }

    /** Removes an item from the equipped list (usually when it's unequipped). */
    public void removeFromEquippedInventory(Item item) {
        if(equipped.contains(item, true)) {
            equipped.removeValue(item, true);
        }
    }

    /** * Clears all equipped items and removes their reference to any characters.
     * Useful for full party resets or data clearing.
     */
    public void clearEquippedInventory() {
        for(Item i : equipped) {
            i.setCurrentCharacter(null);
        }
        equipped.clear();
    }

    /** Checks if an item with the specified name exists in the general inventory. */
    public boolean hasItem(String name) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /** Removes the whole inventory except keys, used in shop sellAll **/
    public void removeAll() {
        Array<Item> temporary = new Array<>();
        for(Item i : inventory){
            if(i instanceof Key)temporary.add(i);
        }
        inventory.clear();
        inventory.addAll(temporary);
    }
}
