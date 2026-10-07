package org.game.world.dungeon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the scaling parameters for dungeon generation based on the current floor level.
 * It controls path lengths, branch complexity, chest density, and battle budgets.
 */
public class DungeonDifficulty {
    private static final Logger logger = LoggerFactory.getLogger(DungeonDifficulty.class);
    private int difficultyLevel = 1;

    // Grid and path constraints
    private int gridSize, minMainPathLength, maxMainPathLength;
    // Side branch constraints
    private int minBranchCount, maxBranchCount, minBranchLength, maxBranchLength;
    // Reward constraints
    private int minChestCount, maxChestCount;

    // Static probabilities for room contents
    public static final double MAIN_PATH_BATTLE_CHANCE = 0.80; // 80% chance for combat on main path
    public static final double BRANCH_BATTLE_CHANCE = 0.70;    // 70% chance for combat in side branches
    public static final double BRANCH_CHEST_CHANCE = 0.10;     // 10% chance to find a chest in a branch

    public DungeonDifficulty(int floorLevel){
        this.difficultyLevel = floorLevel;
        calculateScaling();
    }

    /** Increases floor level and recalculates all generation parameters. */
    public void increaseDifficultyLevel(){
        this.difficultyLevel++;
        logger.info("Dungeon level: {}", difficultyLevel);
        calculateScaling();
    }

    // --- Getters for the Dungeon Generator ---
    public int getDifficulty() { return this.difficultyLevel; }
    public int getMinChests() { return this.minChestCount; }
    public int getMaxChests() { return this.maxChestCount;}
    public int getGridSize() { return this.gridSize; }
    public int getMinMainPathLength() { return this.minMainPathLength; }
    public int getMaxMainPathLength() { return this.maxMainPathLength; }
    public int getMinBranchCount() { return this.minBranchCount; }
    public int getMaxBranchCount() { return this.maxBranchCount; }
    public int getMinBranchLength() { return this.minBranchLength; }
    public int getMaxBranchLength() { return this.maxBranchLength; }

    /**
     * Calculates the 'strength' of enemies encountered on this floor.
     * Uses a modular calculation to provide a sense of progression with difficulty spikes.
     */
    public int getBattleBudget() {
        int relativeLevel = difficultyLevel % 20;
        // Battle budget increases steadily, with jumps every 4 and 20 levels.
        return 20 + ((relativeLevel / 4) * 4 + (difficultyLevel / 20 * 2));
    }

    /**
     * Logic for scaling dungeon complexity.
     * Higher floor levels lead to larger grids, longer paths, and more frequent branches/chests.
     */
    private void calculateScaling() {
        // Base starting values for Floor 1
        int baseGrid = 40, baseMinMain = 6, baseMaxMain = 9;
        int baseMinBranchCount = 1, baseMaxBranchCount = 3, baseMinBranchLength = 2, baseMaxBranchLength = 4;
        int baseMinChest = 1, baseMaxChest = 3;

        // Scaling steps
        int fiveStep = difficultyLevel / 5;        // Increments every 5 levels
        int twentyFive = difficultyLevel / 25;    // Increments every 25 levels

        // Grid grows every 10 floors, main path every 5 floors
        this.gridSize = baseGrid + difficultyLevel / 10;
        this.minMainPathLength = baseMinMain + fiveStep;
        this.maxMainPathLength = baseMaxMain + fiveStep;

        // Branches and chests only increase complexity every 25 floors
        this.minBranchLength = baseMinBranchLength + twentyFive;
        this.maxBranchLength = baseMaxBranchLength + twentyFive;
        this.minBranchCount = baseMinBranchCount + twentyFive;
        this.maxBranchCount = baseMaxBranchCount + twentyFive;

        this.minChestCount = baseMinChest + twentyFive;
        this.maxChestCount = baseMaxChest + twentyFive;
    }

    public void setDifficulty(int i) {
        difficultyLevel = i;
        calculateScaling();
    }
}
