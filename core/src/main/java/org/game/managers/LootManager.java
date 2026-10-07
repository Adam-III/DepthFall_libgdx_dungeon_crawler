package org.game.managers;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.items.base.Item;
import org.game.utils.DropTable;


/**
 * Manages the temporary loot collected during a dungeon run.
 * It acts as a bridge between the game's drop logic (DropTable) and the player's session inventory.
 */
public class LootManager {
    /** Temporary storage for items found in the current dungeon run before they are finalized */
    private Array<Item> currentLoot;

    /** The logic handler that decides what specific items drop based on probabilities */
    private final DropTable lootGeneration;

    private final GameManager game;

    /**
     * Initializes the LootManager for a specific dungeon session.
     * @param game The main game manager.
     */
    public LootManager(GameManager game) {
        this.game = game;
        this.currentLoot = new Array<>();
        this.lootGeneration = new DropTable(this.game, this);
    }

    /** Always reflects the current dungeon depth. */
    private int currentLevel() {
        return game.dungeonDifficulty.getDifficulty();
    }

    /** Triggers standard loot generation (e.g., after a normal battle). */
    public void generateLoot() {
        lootGeneration.generateLoot(currentLevel());
    }

    /**
     * Adds a collection of items to the current session's loot pool.
     * @param newLoot Array of items to be added.
     */
    public void addLoot(Array<Item> newLoot){
        for(Item i : newLoot) {
            currentLoot.add(i);
        }
    }

    /** Returns the list of items collected so far in the current run. */
    public Array<Item> getLoot(){
        return this.currentLoot;
    }

    /** Triggers specific loot generation logic for treasure chests. */
    public void generateChestLoot() {
        lootGeneration.generateChestLoot(currentLevel());
    }

    /** Triggers high-quality loot generation logic for boss encounters. */
    public void generateBossLoot() {
        lootGeneration.generateBossLoot(currentLevel());
    }

    /**
     * Removes ONE item from the temporary loot pool by name.
     * Useful if an item is discarded or handled separately.
     * @param name Case-insensitive name of the item to remove.
     */
    public void removeFromLootInventory(String name) {
        for (int i = 0; i < currentLoot.size; i++) {
            if (currentLoot.get(i).getName().equalsIgnoreCase(name)) {
                currentLoot.removeIndex(i);
                return;
            }
        }
    }
}
