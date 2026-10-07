package org.game.items.base;

import com.badlogic.gdx.utils.Array;
import org.game.items.blueprints.*;
import org.game.items.other.Key;
import org.game.managers.TextureManager;

import java.util.Random;

/**
 * Static factory class responsible for generating randomized loot.
 * Uses a tiered system (Ranks 1-8) with dynamic probability based on the current dungeon floor.
 */
public class LootFactory {
    /** Database of all available item blueprints */
    private static final Array<BaseItemBlueprint> registry = new Array<>();
    private static final Random RNG = new Random();

    // Probability points for each Rank [floor, weight]
    // These are used for linear interpolation to determine drop chances.
    // Points MUST be sorted by floor (ascending) with no duplicate floors.
    // Each rank starts at its unlock floor, peaks, then fades out as better ranks take over.
    private static final float[][] R1_POINTS = {{1, 100}, {20, 40}, {30, 5}, {45, 0}};
    private static final float[][] R2_POINTS = {{1, 0}, {5, 0}, {15, 40}, {30, 10}, {50, 0}};
    private static final float[][] R3_POINTS = {{1, 0}, {15, 0}, {30, 40}, {45, 15}, {60, 0}};
    private static final float[][] R4_POINTS = {{1, 0}, {30, 0}, {50, 40}, {60, 20}, {75, 0}};
    private static final float[][] R5_POINTS = {{1, 0}, {45, 0}, {65, 40}, {70, 20}, {85, 0}};
    private static final float[][] R6_POINTS = {{1, 0}, {60, 0}, {80, 40}, {90, 25}, {100, 10}};
    private static final float[][] R7_POINTS = {{1, 0}, {75, 0}, {90, 40}, {100, 45}};
    private static final float[][] R8_POINTS = {{1, 0}, {85, 0}, {100, 60}};

    /** Minimum floor required to unlock a specific Rank */
    private static final int[] UNLOCK_FLOORS = {
        1, 5, 15, 30, 45, 60, 75, 85
    };

    /** Stat scaling multiplier for each rank */
    private static final float[] RANK_SPEEDS = {
        2.40f, 1.9f, 1.50f, 1.10f, 0.80f, 0.60f, 0.50f, 0.70f
    };

    /** Static utility class, not meant to be instantiated. */
    private LootFactory() {}

    private static final float[][][] ALL_RANKS = {
        R1_POINTS, R2_POINTS, R3_POINTS, R4_POINTS,
        R5_POINTS, R6_POINTS, R7_POINTS, R8_POINTS
    };

    /**
     * Generates a random item based on the current floor.
     * @param floor Current dungeon depth.
     * @return A newly instantiated Item (Weapon or Armor).
     */
    public static Item generateLoot(int floor) {
        // 1. Determine which tier (Rank) to drop
        int targetRank = rollTier(floor);

        // 2. Filter registry for blueprints matching the rank and floor requirements
        Array<BaseItemBlueprint> matchingItems = new Array<>();
        for (BaseItemBlueprint bp : registry) {
            if (bp.specificRank == targetRank && floor >= bp.minFloor) {
                matchingItems.add(bp);
            }
        }

        // Fallback if no matching items are found
        if (matchingItems.size == 0) return generateFallback(floor);

        // 3. Select a random blueprint from the filtered list
        BaseItemBlueprint selection = matchingItems.get(RNG.nextInt(matchingItems.size));

        // 4. Calculate the item's level based on progression
        selection.rolledLevel = calculateBalancedLevel(targetRank, floor, selection);

        // 5. Create the actual Item object using reflection
        return instantiateFromBlueprint(selection);
    }

    /**
     * Save-system helper. Builds a fresh Item by looking up the named blueprint
     * in the registry and instantiating it at the requested level.
     *
     * @param name  Display name of the item, e.g. "Iron Sword".
     * @param level Item level to apply when constructing.
     * @return The instantiated Item, or null if no blueprint matches the name.
     */
    public static Item createItemByName(String name, int level) {
        if (name == null) return null;
        // Keys are not blueprint-based, so they are rebuilt directly
        if ("Key".equals(name)) return new Key();
        for (BaseItemBlueprint bp : registry) {
            if (name.equals(bp.name)) {
                // rolledLevel feeds the item's constructor stats. We force it
                // to the saved value so reloaded gear has identical stats.
                bp.rolledLevel = Math.max(1, level);
                return instantiateFromBlueprint(bp);
            }
        }
        return null;
    }

    /**
     * Uses weighted probability and interpolation to pick an item Rank.
     */
    private static int rollTier(int floor) {
        float[] weights = new float[8];
        float totalWeight = 0;

        // Calculate weight for each rank at the current floor
        for (int i = 0; i < 8; i++) {
            weights[i] = interpolate(floor, ALL_RANKS[i]);
            totalWeight += weights[i];
        }

        float roll = RNG.nextFloat() * totalWeight;
        float cursor = 0;

        // Weighted selection logic
        for (int i = 0; i < 8; i++) {
            cursor += weights[i];
            if (roll <= cursor) return i + 1;
        }

        return 1;
    }

    /**
     * Linear interpolation (Lerp) to calculate probability weights between key points.
     */
    private static float interpolate(int floor, float[][] points) {
        if (floor <= points[0][0]) return points[0][1];
        if (floor >= points[points.length - 1][0]) return points[points.length - 1][1];

        for (int i = 0; i < points.length - 1; i++) {
            if (floor >= points[i][0] && floor <= points[i+1][0]) {
                float x0 = points[i][0];
                float y0 = points[i][1];
                float x1 = points[i+1][0];
                float y1 = points[i+1][1];

                // Standard lerp formula
                return y0 + (floor - x0) * (y1 - y0) / (x1 - x0);
            }
        }
        return 0;
    }

    /**
     * Calculates an appropriate item level (1-10) based on floor depth and rank scaling.
     */
    private static int calculateBalancedLevel(int rank, int floor, BaseItemBlueprint bp) {
        if(bp == null) return 0;
        int unlockFloor = UNLOCK_FLOORS[rank - 1];

        int effectiveStartFloor = Math.max(unlockFloor, bp.minFloor);
        int relativeFloor = Math.max(1, (floor - effectiveStartFloor) + 1);

        float rankSpeed = RANK_SPEEDS[rank - 1];

        // Formula to determine level based on floor progression
        float potential = relativeFloor * rankSpeed;
        int baseLevel = (int)(potential / 3);

        // Add a bit of randomness (variance) to the level
        int variance = RNG.nextInt(3) - 1; // -1, 0, or 1
        return Math.max(1, Math.min(10, baseLevel + variance));
    }

    /**
     * Uses Java Reflection to dynamically instantiate the correct Item subclass.
     */
    private static Item instantiateFromBlueprint(BaseItemBlueprint bp) {
        try {
            if (bp instanceof WeaponBlueprint) {
                return bp.itemClass.getConstructor(WeaponBlueprint.class).newInstance((WeaponBlueprint) bp);
            } else if (bp instanceof ProtectiveGearBlueprint) {
                return bp.itemClass.getConstructor(ProtectiveGearBlueprint.class).newInstance((ProtectiveGearBlueprint) bp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Basic fallback logic to avoid returning null in edge cases */
    private static Item generateFallback(int floor) {
        if (registry.size == 0) return null;
        BaseItemBlueprint bp = registry.get(0);
        bp.rolledLevel = 1;
        return instantiateFromBlueprint(bp);
    }


    static {
        // ==========================================
        // ITEM REGISTRY INITIALIZATION
        // ==========================================

        // --- RANK 1: WOOD & BONE (Unlock: Floor 1) ---
        registry.add(new ProtectiveGearBlueprint("Wooden Armor", 1, 50, 20, 5, 0.01f, TextureManager.WOODEN_ARMOR, ArmorBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Wooden Shield", 1, 30, 10, 3, 0.02f, TextureManager.WOODEN_SHIELD, ShieldBlueprint.class));
        registry.add(new WeaponBlueprint("Wooden Sword", 1, 40, 12, 3, TextureManager.WOODEN_SWORD, SwordBlueprint.class));
        registry.add(new WeaponBlueprint("Wooden Bow", 1, 35, 10, 3, TextureManager.WOODEN_BOW, BowBlueprint.class));
        registry.add(new WeaponBlueprint("Wooden Staff", 1, 30, 8, 2, TextureManager.WOODEN_STAFF, StaffBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Bone Shield", 1, 80, 25, 6, 0.03f, TextureManager.BONE_SHIELD, ShieldBlueprint.class, 3));
        registry.add(new WeaponBlueprint("Bone Sword", 1, 100, 28, 7, TextureManager.BONE_SWORD, SwordBlueprint.class, 3));
        registry.add(new WeaponBlueprint("Bone Bow", 1, 90, 25, 6, TextureManager.BONE_BOW, BowBlueprint.class, 3));

        // --- RANK 2: STICK, LEATHER, RUSTY (Unlock: Floor 5) ---
        registry.add(new WeaponBlueprint("Stick Staff", 2, 110, 30, 7, TextureManager.STICK_STAFF, StaffBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Leather Armor", 2, 180, 60, 12, 0.02f, TextureManager.LEATHER_ARMOR, ArmorBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Leather Shield", 2, 140, 50, 10, 0.03f, TextureManager.LEATHER_SHIELD, ShieldBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Rusty Armor", 2, 220, 75, 15, 0.03f, TextureManager.RUSTY_ARMOR, ArmorBlueprint.class, 8));
        registry.add(new ProtectiveGearBlueprint("Rusty Shield", 2, 160, 55, 12, 0.04f, TextureManager.RUSTY_SHIELD, ShieldBlueprint.class, 8));
        registry.add(new WeaponBlueprint("Rusty Sword", 2, 190, 50, 10, TextureManager.RUSTY_SWORD, SwordBlueprint.class));
        registry.add(new WeaponBlueprint("Rusty Bow", 2, 175, 45, 9, TextureManager.RUSTY_BOW, BowBlueprint.class, 8));

        // --- RANK 3: IRON & STEEL (Unlock: Floor 15) ---
        registry.add(new ProtectiveGearBlueprint("Iron Armor", 3, 400, 120, 25, 0.04f, TextureManager.IRON_ARMOR, ArmorBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Iron Shield", 3, 300, 90, 20, 0.05f, TextureManager.IRON_SHIELD, ShieldBlueprint.class));
        registry.add(new WeaponBlueprint("Iron Sword", 3, 380, 80, 18, TextureManager.IRON_SWORD, SwordBlueprint.class));
        registry.add(new WeaponBlueprint("Iron Bow", 3, 360, 75, 17, TextureManager.IRON_BOW, BowBlueprint.class));
        registry.add(new WeaponBlueprint("Iron Staff", 3, 340, 70, 16, TextureManager.IRON_STAFF, StaffBlueprint.class));
        registry.add(new WeaponBlueprint("Snake Sword", 3, 450, 100, 22, TextureManager.SNAKE_SWORD, SwordBlueprint.class, 20));
        registry.add(new ProtectiveGearBlueprint("Steel Shield", 3, 480, 140, 28, 0.06f, TextureManager.STEEL_SHIELD, ShieldBlueprint.class, 22));
        registry.add(new ProtectiveGearBlueprint("Steel Armor", 3, 580, 200, 28, 0.05f, TextureManager.STEEL_ARMOR, ArmorBlueprint.class, 20));

        // --- RANK 4: GOLD (Unlock: Floor 30) ---
        registry.add(new ProtectiveGearBlueprint("Gold Armor", 4, 750, 250, 50, 0.06f, TextureManager.GOLD_ARMOR, ArmorBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Light Gold Shield", 4, 550, 160, 35, 0.06f, TextureManager.LIGHT_GOLD_SHIELD, ShieldBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Heavy Gold Shield", 4, 700, 210, 45, 0.07f, TextureManager.HEAVY_GOLD_SHIELD, ShieldBlueprint.class, 38));
        registry.add(new WeaponBlueprint("Gold Sword", 4, 650, 150, 32, TextureManager.GOLD_SWORD, SwordBlueprint.class));
        registry.add(new WeaponBlueprint("Gold Bow", 4, 620, 140, 30, TextureManager.GOLD_BOW, BowBlueprint.class));
        registry.add(new WeaponBlueprint("Gold Staff", 4, 600, 135, 28, TextureManager.GOLD_STAFF, StaffBlueprint.class));

        // --- RANK 5: EMERALD & RUBY (Unlock: Floor 45) ---
        registry.add(new ProtectiveGearBlueprint("Emerald Armor", 5, 1200, 450, 90, 0.07f, TextureManager.EMERALD_ARMOR, ArmorBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Emerald Shield", 5, 1000, 380, 75, 0.08f, TextureManager.EMERALD_SHIELD, ShieldBlueprint.class));
        registry.add(new WeaponBlueprint("Emerald Sword", 5, 1100, 280, 60, TextureManager.EMERALD_SWORD, SwordBlueprint.class));
        registry.add(new WeaponBlueprint("Emerald Bow", 5, 1050, 270, 58, TextureManager.EMERALD_BOW, BowBlueprint.class));
        registry.add(new WeaponBlueprint("Emerald Staff", 5, 1000, 260, 55, TextureManager.EMERALD_STAFF, StaffBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Ruby Armor", 5, 1500, 600, 120, 0.09f, TextureManager.RUBY_ARMOR, ArmorBlueprint.class, 52));
        registry.add(new ProtectiveGearBlueprint("Ruby Shield", 5, 1300, 500, 100, 0.10f, TextureManager.RUBY_SHIELD, ShieldBlueprint.class, 52));
        registry.add(new WeaponBlueprint("Ruby Sword", 5, 1400, 350, 80, TextureManager.RUBY_SWORD, SwordBlueprint.class, 52));
        registry.add(new WeaponBlueprint("Ruby Bow", 5, 1350, 330, 75, TextureManager.RUBY_BOW, BowBlueprint.class, 52));
        registry.add(new WeaponBlueprint("Ruby Staff", 5, 1300, 310, 70, TextureManager.RUBY_STAFF, StaffBlueprint.class, 52));

        // --- RANK 6: DIAMOND (Unlock: Floor 60) ---
        registry.add(new ProtectiveGearBlueprint("Diamond Armor", 6, 2500, 1000, 200, 0.10f, TextureManager.DIAMOND_ARMOR, ArmorBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Diamond Shield", 6, 2100, 850, 170, 0.11f, TextureManager.DIAMOND_SHIELD, ShieldBlueprint.class));
        registry.add(new WeaponBlueprint("Diamond Sword", 6, 2300, 600, 130, TextureManager.DIAMOND_SWORD, SwordBlueprint.class));
        registry.add(new WeaponBlueprint("Diamond Bow", 6, 2200, 580, 125, TextureManager.DIAMOND_BOW, BowBlueprint.class));
        registry.add(new WeaponBlueprint("Diamond Staff", 6, 2100, 550, 120, TextureManager.DIAMOND_STAFF, StaffBlueprint.class));

        // --- RANK 7: DRAGON & MAGMA (Unlock: Floor 75) ---
        registry.add(new ProtectiveGearBlueprint("Magma Armor", 7, 5000, 1800, 350, 0.12f, TextureManager.MAGMA_ARMOR, ArmorBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Magma Shield", 7, 4500, 1500, 300, 0.13f, TextureManager.MAGMA_SHIELD, ShieldBlueprint.class));
        registry.add(new WeaponBlueprint("Magma Sword", 7, 4800, 1100, 250, TextureManager.MAGMA_SWORD, SwordBlueprint.class));
        registry.add(new WeaponBlueprint("Magma Bow", 7, 4600, 1050, 240, TextureManager.MAGMA_BOW, BowBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Dragon Armor", 7, 6500, 2400, 450, 0.13f, TextureManager.DRAGON_ARMOR, ArmorBlueprint.class, 80));
        registry.add(new ProtectiveGearBlueprint("Dragon Shield", 7, 6000, 2100, 400, 0.14f, TextureManager.DRAGON_SHIELD, ShieldBlueprint.class, 80));
        registry.add(new WeaponBlueprint("Dragon Staff", 7, 5800, 1300, 300, TextureManager.DRAGON_STAFF, StaffBlueprint.class));

        // --- RANK 8: OBSIDIAN (Unlock: Floor 85) ---
        registry.add(new ProtectiveGearBlueprint("Obsidian Armor", 8, 12000, 4500, 800, 0.14f, TextureManager.OBSIDIAN_ARMOR, ArmorBlueprint.class));
        registry.add(new ProtectiveGearBlueprint("Obsidian Shield", 8, 10000, 3800, 700, 0.15f, TextureManager.OBSIDIAN_SHIELD, ShieldBlueprint.class));
        registry.add(new WeaponBlueprint("Obsidian Sword", 8, 11000, 2500, 600, TextureManager.OBSIDIAN_SWORD, SwordBlueprint.class));
        registry.add(new WeaponBlueprint("Obsidian Bow", 8, 10500, 2400, 580, TextureManager.OBSIDIAN_BOW, BowBlueprint.class));
        registry.add(new WeaponBlueprint("Obsidian Staff", 8, 10000, 2200, 550, TextureManager.OBSIDIAN_STAFF, StaffBlueprint.class));
    }
}
