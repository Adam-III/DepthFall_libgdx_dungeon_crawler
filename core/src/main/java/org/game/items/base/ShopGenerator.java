package org.game.items.base;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;

import java.util.Random;

/**
 * Manager class responsible for generating and refreshing the shop's inventory.
 * It uses the LootFactory to create a diverse set of items for the player to purchase.
 */
public class ShopGenerator {
    private final Random RNG = new Random();
    private final GameManager game;

    /**
     * Constructs a ShopGenerator with access to the game manager.
     * @param game The main game manager used to access current dungeon difficulty and factories.
     */
    public ShopGenerator(GameManager game) {
        this.game = game;
    }

    /**
     * Generates a large collection of items to populate the shop's stock.
     * The number of items and their quality scale based on the current dungeon floor.
     * * @param level Base level parameter (unused here, as it pulls directly from game difficulty).
     * @return An Array of randomly generated Items for the shop UI.
     */
    public Array<Item> generateShopStock(int level) {
        Array<Item> stock = new Array<>();

        // Generates a randomized amount of items, typically between 80 and 120 (100 +/- 20).
        int stockSize = 100 + (RNG.nextInt(40) - 20);

        for (int i = 0; i < stockSize; i++) {
            // Generates loot equal to the highest floor reached
            stock.add(LootFactory.generateLoot(level));
        }

        return stock;
    }

    /**
     * Cleanup method to be called when the generator is no longer needed.
     * Currently serves as a placeholder for resource management.
     */
    public void dispose() {
        // Implementation for clearing resources if necessary
    }
}
