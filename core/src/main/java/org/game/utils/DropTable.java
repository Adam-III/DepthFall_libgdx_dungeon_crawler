package org.game.utils;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.items.base.Item;
import org.game.items.base.LootFactory;
import org.game.items.other.Key;
import org.game.managers.LootManager;

import java.util.Random;

/**
 * Handles the logic for generating item drops after combat or from environmental sources.
 * It scales the quality and quantity of loot based on the encounter type (Regular, Chest, or Boss).
 */
public class DropTable {
    private Array<Item> loot;
    private final GameManager game;
    private final LootManager manager;
    private final Random RNG = new Random();

    public DropTable(GameManager game, LootManager lootManager){
        this.game = game;
        this.manager = lootManager;
    }

    /**
     * Standard loot generation after a normal fight.
     * Generates 2 to 4 items and has a small chance (10%) to drop a dungeon key.
     */
    public void generateLoot(int level) {
        loot = new Array<>();
        // Generate a random amount of standard items
        int count = 2 + RNG.nextInt(3); // rolled once, not on every loop check
        for (int i = 0; i < count; i++) {
            loot.add(LootFactory.generateLoot(level));
        }

        // 10% chance for a Key drop
        if(0.1f > RNG.nextDouble()){
            loot.add(new Key());
            game.addKey(1);
        }

        manager.addLoot(loot);
    }

    /**
     * Generates higher quality loot for Treasure Chests.
     * Drops more items (5-8) and buffs their effective level for better stats.
     */
    public void generateChestLoot(int level) {
        loot = new Array<>();
        int count = 5 + RNG.nextInt(4);
        for (int i = 0; i < count; i++) {
            // Chest items are consistently higher level than the current floor level
            loot.add(LootFactory.generateLoot(level + RNG.nextInt(2) + 2));
        }

        // Slightly bigger chance for knowledge orbs than normal
        if (0.4f + game.getUpgrades().getOrbChance() > RNG.nextFloat()) {
            int baseOrbs = RNG.nextInt(Math.max(1,game.dungeonDifficulty.getDifficulty()/2+1)) +
                Math.max(1,game.dungeonDifficulty.getDifficulty()/2);
            int totalOrbs = (int) (baseOrbs * (1f + game.getUpgrades().getOrbMultiplier()));
            game.addKnowledgeOrbs(totalOrbs);
        }

        manager.addLoot(loot);
    }

    /**
     * Generates the ultimate reward for defeating a Boss.
     * Large quantity of items (8-11) with a significant level boost and high key chance (50%).
     */
    public void generateBossLoot(int level) {
        loot = new Array<>();
        int count = 8 + RNG.nextInt(4);
        for (int i = 0; i < count; i++) {
            // Bosses drop items with much higher stat potential
            loot.add(LootFactory.generateLoot(level + RNG.nextInt(4) + 2));
        }

        // 50% chance for a Key drop after a boss fight
        if(0.5f > RNG.nextFloat()){
            loot.add(new Key());
            game.addKey(1);
        }
        manager.addLoot(loot);
    }
}
