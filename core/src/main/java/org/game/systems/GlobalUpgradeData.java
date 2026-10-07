package org.game.systems;

/**
 * Data container for a global upgrade.
 * This class stores the template information for upgrades that persist
 * across different game runs.
 */
public class GlobalUpgradeData {
    public String id;            // Unique identifier (e.g., "crit_chance")
    public String name;          // Display name in the UI (e.g., "Precision")
    public String description;   // Explanation of what the upgrade does
    public int baseCost;         // Starting price for level 1
    public float costMultiplier; // How much the cost (or effect) scales per level
    public int maxLevel;         // The cap to prevent infinite scaling

    /**
     * Constructs a new upgrade data template.
     * * @param id Unique string ID.
     * @param name Name shown to the player.
     * @param description Tooltip or flavor text.
     * @param baseCost Cost for the first purchase.
     * @param costMultiplier Scaling factor for pricing or effects.
     * @param maxLevel Maximum number of times this can be upgraded.
     */
    public GlobalUpgradeData(String id, String name, String description, int baseCost, float costMultiplier, int maxLevel) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.baseCost = baseCost;
        this.costMultiplier = costMultiplier;
        this.maxLevel = maxLevel;
    }
}
