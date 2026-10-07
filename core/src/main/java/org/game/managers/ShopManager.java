package org.game.managers;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.items.base.Item;
import org.game.items.base.ShopGenerator;
import org.game.items.other.Key;

/**
 * Manages shop transactions, including buying and selling items.
 * It tracks the current stock available for the player and handles currency exchange.
 */
public class ShopManager {
    private final GameManager game;
    private Array<Item> stock;
    private final ShopGenerator generator;

    /**
     * Initializes the shop and generates an initial set of items based on the player's level.
     * @param game The main game manager for accessing gold and inventory.
     * @param level Current progression level used to determine item quality.
     */
    public ShopManager(GameManager game, int level) {
        this.game = game;
        this.stock = new Array<>();
        this.generator = new ShopGenerator(game);

        restock(level);
    }

    /**
     * Processes the purchase of an item from the shop.
     * Checks if the player has enough permanent gold, deducts the price,
     * and moves the item from shop stock to player inventory.
     * @param item The item to be purchased.
     * @return true if the transaction was successful, false otherwise.
     */
    public boolean buyItem(Item item) {
        // Uses permanent gold (meta-currency) for shop purchases
        if (game.getPermanentGold() >= item.getPrice()) {
            game.addGold(-item.getPrice());
            stock.removeValue(item, true);
            game.getInventory().addToInventory(item);
            return true;
        }
        return false;
    }

    /**
     * Processes the sale of an item from the player's inventory back to the shop.
     * The player receives a calculated sell price, and the item is returned to the shop stock.
     * @param item The item to be sold.
     */
    public void sellItem(Item item) {
        game.addSellGold(item.getSellPrice());
        stock.add(item);
        game.getInventory().removeFromInventory(item);
    }

    /**
     * Sells all items in inventory except keys (keys are kept by removeAll()).
     */
    public void sellAll() {
        for(Item i : game.getInventory().getInventory()){
            if (i instanceof Key) continue;
            game.addSellGold(i.getSellPrice());
        }
        game.getInventory().removeAll();
    }


    /**
     * Clears current inventory and generates a fresh batch of items using the ShopGenerator.
     * @param level The current game level to scale item stats.
     */
    public void restock(int level) {
        this.stock.clear();
        this.stock = generator.generateShopStock(level);
    }

    public Array<Item> getStock() {
        return stock;
    }

    public void setStock(Array<Item> stock) { this.stock = stock; }

}
