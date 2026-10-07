package org.game.entities.factories;

import com.badlogic.gdx.utils.Array;
import org.game.entities.base.Character;
import org.game.entities.base.EnemyBlueprint;
import org.game.entities.base.EnemyBlueprintRanged;
import org.game.entities.blueprints.MeleeEnemy;
import org.game.entities.blueprints.RangedEnemy;
import org.game.entities.bosses.RedDragon;
import org.game.entities.types.AttackType;
import org.game.entities.types.EnemyType;
import org.game.managers.TextureManager;
import org.game.world.dungeon.DungeonDifficulty;

import java.util.Random;

/**
 * Factory class responsible for spawning enemies based on dungeon difficulty.
 * Manages enemy scaling, budget-based group generation, and boss encounters.
 */
public class EnemyFactory {
    private Array<Character> frontRow = new Array<>(); // Melee positions
    private Array<Character> backRow = new Array<>();  // Ranged positions
    private final Array<EnemyBlueprint> registry = new Array<>(); // Database of all possible enemies
    DungeonDifficulty diff;
    private static final Random RNG = new Random();
    private final boolean isBossBattle;


    public EnemyFactory(DungeonDifficulty diff, boolean isBossBattle) {
        this.diff = diff;
        this.isBossBattle = isBossBattle;
        if (isBossBattle) generateBoss();
        else {
            setupRegistry();
            generateEnemies();
        }

    }

    public boolean isBossBattle(){return isBossBattle;}

    private void generateBoss() {
        int level = diff.getDifficulty();
        frontRow.add(new RedDragon(level));
    }

    public Array<Character> getBoss() {
        return frontRow;
    }

    public Character[] getFrontRow() {
        Character[] front = new Character[3];
        int i = 0;
        for (Character c : frontRow) {
            front[i] = c;
            i++;
        }
        return front;
    }

    public Character[] getBackRow() {
        Character[] back = new Character[3];
        int i = 0;
        for (Character c : backRow) {
            back[i] = c;
            i++;
        }
        return back;
    }

    /**
     * Generates a group of enemies within a specific 'Battle Budget'.
     * Higher floor difficulty allows for more or stronger enemies.
     */
    private void generateEnemies() {
        int currentBudget = diff.getBattleBudget();
        int cheapestCost = 4;

        while (currentBudget >= cheapestCost && (frontRow.size + backRow.size) < 6) {
            // Logic to reserve budget for filling up empty slots
            int remainingNeeded = Math.max(0, 3 - (frontRow.size + backRow.size + 1));
            int reservedAmount = remainingNeeded * cheapestCost;

            EnemyBlueprint selection = getRandomAffordableBlueprint(currentBudget - reservedAmount);

            if (selection != null) {
                // Calculate enemy level based on the floor it appears on
                int calculatedLevel = 1 + (diff.getDifficulty() - selection.minFloor);
                int finalLevel = Math.min(10, Math.max(1, calculatedLevel));

                // Spawn and place enemy based on their attack type (Melee/Range)
                Character newEnemy = selection.constructor.apply(selection, finalLevel);
                if (selection.type == AttackType.MELEE) {
                    frontRow.add(newEnemy);
                } else {
                    backRow.add(newEnemy);
                }
                currentBudget -= selection.cost;
            } else break;
        }
    }


    private EnemyBlueprint getRandomAffordableBlueprint(int limit) {
        Array<EnemyBlueprint> affordable = new Array<>();

        for (EnemyBlueprint bp : registry) {
            if (bp.cost <= limit && diff.getDifficulty() >= bp.minFloor && diff.getDifficulty() <= bp.maxFloor) {
                if (bp.type == AttackType.MELEE && frontRow.size < 3) {
                    affordable.add(bp);
                } else if (bp.type == AttackType.RANGE && backRow.size < 3) {
                    affordable.add(bp);
                }
            }
        }

        if (affordable.size == 0) return null;
        return affordable.get(RNG.nextInt(affordable.size));
    }

    private void setupRegistry() {
        // --- SLIMES (Floors 1-19) ---
        registry.add(new EnemyBlueprint("Slime Kids", 4, 1, 12, 30, 5, 3, 1, 20, 0.02f, TextureManager.SLIME_KIDS, EnemyType.SLIME, AttackType.MELEE, MeleeEnemy::new)); // Fast
        registry.add(new EnemyBlueprint("Axe Head", 5, 1, 15, 55, 8, 5, 2, 10, 0.06f, TextureManager.SLIME_AXE_HEAD, EnemyType.SLIME, AttackType.MELEE, MeleeEnemy::new)); // Generic
        registry.add(new EnemyBlueprintRanged("Arrow Head", 8, 1, 15, 25, 4, 7, 3, 5, 0.04f, TextureManager.SLIME_ARROW_HEAD, EnemyType.SLIME, TextureManager.PROJECTILE, RangedEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Attacked Slime", 7, 5, 19, 65, 12, 9, 3, 10, 0.05f, TextureManager.SLIME_ATTACKED, EnemyType.SLIME, AttackType.MELEE, MeleeEnemy::new)); // Generic
        registry.add(new EnemyBlueprintRanged("Tree Head", 10, 8, 19, 50, 8, 15, 4, 10, 0.06f, TextureManager.SLIME_TREE, EnemyType.SLIME, TextureManager.PROJECTILE, RangedEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Blob", 8, 10, 19, 100, 15, 13, 3, 5, 0.15f, TextureManager.SLIME_BLOB, EnemyType.SLIME, AttackType.MELEE, MeleeEnemy::new)); // Tank
        registry.add(new EnemyBlueprintRanged("Floating Slime", 10, 12, 19, 85, 10, 22, 5, 10, 0.03f, TextureManager.SLIME_FLOATING, EnemyType.SLIME, TextureManager.PROJECTILE, RangedEnemy::new)); // Fast
        registry.add(new EnemyBlueprint("Slime Golem", 15, 15, 19, 220, 30, 28, 6, 5, 0.20f, TextureManager.SLIME_GOLEM, EnemyType.SLIME, AttackType.MELEE, MeleeEnemy::new)); // Tank
        registry.add(new EnemyBlueprint("Slime Family", 12, 10, 19, 150, 25, 18, 4, 10, 0.07f, TextureManager.SLIME_FAMILY, EnemyType.SLIME, AttackType.MELEE, MeleeEnemy::new)); // Generic

        // --- PLANTS (Floors 20-39) ---
        registry.add(new EnemyBlueprint("Parsley", 4, 20, 30, 120, 25, 20, 6, 10, 0.05f, TextureManager.PLANT_PARSLEY, EnemyType.PLANT, AttackType.MELEE, MeleeEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Onion", 5, 20, 32, 160, 30, 25, 7, 10, 0.08f, TextureManager.PLANT_ONION, EnemyType.PLANT, AttackType.MELEE, MeleeEnemy::new)); // Generic
        registry.add(new EnemyBlueprintRanged("Tomato", 6, 22, 35, 110, 20, 35, 9, 10, 0.04f, TextureManager.PLANT_TOMATO, EnemyType.PLANT, TextureManager.PROJECTILE, RangedEnemy::new)); // Generic
        registry.add(new EnemyBlueprintRanged("Sunflower", 8, 25, 39, 140, 25, 45, 12, 10, 0.06f, TextureManager.PLANT_SUNFLOWER, EnemyType.PLANT, TextureManager.PROJECTILE, RangedEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Pumpkin", 15, 28, 39, 450, 80, 35, 8, 5, 0.18f, TextureManager.PLANT_PUMPKIN, EnemyType.PLANT, AttackType.MELEE, MeleeEnemy::new)); // Tank
        registry.add(new EnemyBlueprintRanged("Rose", 8, 30, 39, 180, 35, 60, 10, 20, 0.02f, TextureManager.PLANT_ROSE, EnemyType.PLANT, TextureManager.PROJECTILE, RangedEnemy::new)); // Fast
        registry.add(new EnemyBlueprint("Meat Eater", 12, 32, 39, 300, 50, 75, 18, 10, 0.04f, TextureManager.PLANT_MEAT_EATER, EnemyType.PLANT, AttackType.MELEE, MeleeEnemy::new)); // Generic

        // --- MUSHROOMS (Floors 40-59) ---
        registry.add(new EnemyBlueprint("Mole Shroom", 5, 40, 50, 350, 60, 60, 15, 10, 0.07f, TextureManager.MUSHROOM_MOLE, EnemyType.MUSHROOM, AttackType.MELEE, MeleeEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Fat Shroom", 8, 40, 52, 700, 100, 50, 12, 5, 0.17f, TextureManager.MUSHROOM_FAT, EnemyType.MUSHROOM, AttackType.MELEE, MeleeEnemy::new)); // Tank
        registry.add(new EnemyBlueprintRanged("Crab Shroom", 6, 42, 55, 300, 50, 100, 20, 10, 0.15f, TextureManager.MUSHROOM_CRAB, EnemyType.MUSHROOM, TextureManager.PROJECTILE, RangedEnemy::new)); // Tank-Ranged
        registry.add(new EnemyBlueprint("Mushroom Group", 7, 45, 59, 450, 70, 90, 22, 20, 0.03f, TextureManager.MUSHROOM_GROUP, EnemyType.MUSHROOM, AttackType.MELEE, MeleeEnemy::new)); // Fast
        registry.add(new EnemyBlueprintRanged("Claw Shroom", 10, 48, 59, 380, 65, 140, 30, 10, 0.06f, TextureManager.MUSHROOM_CLAW, EnemyType.MUSHROOM, TextureManager.PROJECTILE, RangedEnemy::new)); // Generic
        registry.add(new EnemyBlueprintRanged("Scorpion Shroom", 10, 50, 59, 420, 80, 160, 35, 10, 0.08f, TextureManager.MUSHROOM_SCORPION, EnemyType.MUSHROOM, TextureManager.PROJECTILE, RangedEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Big Shroom", 15, 55, 59, 1400, 250, 120, 25, 5, 0.20f, TextureManager.MUSHROOM_BIG, EnemyType.MUSHROOM, AttackType.MELEE, MeleeEnemy::new)); // Tank

        // --- ANIMALS (Floors 60-79) ---
        registry.add(new EnemyBlueprint("Rat", 4, 60, 70, 500, 100, 140, 30, 20, 0.01f, TextureManager.ANIMAL_RAT, EnemyType.ANIMAL, AttackType.MELEE, MeleeEnemy::new)); // Fast
        registry.add(new EnemyBlueprint("Lady Bug", 5, 60, 70, 600, 120, 120, 25, 10, 0.16f, TextureManager.ANIMAL_LADY_BUG, EnemyType.ANIMAL, AttackType.MELEE, MeleeEnemy::new)); // Tank (Shell)
        registry.add(new EnemyBlueprintRanged("Bee", 7, 62, 75, 450, 80, 220, 45, 10, 0.01f, TextureManager.ANIMAL_BEE, EnemyType.ANIMAL, TextureManager.PROJECTILE, RangedEnemy::new)); // Fast
        registry.add(new EnemyBlueprintRanged("Worm", 8, 65, 75, 750, 150, 180, 40, 10, 0.07f, TextureManager.ANIMAL_WORM, EnemyType.ANIMAL, TextureManager.PROJECTILE, RangedEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Boar", 15, 68, 79, 1800, 300, 200, 50, 5, 0.18f, TextureManager.ANIMAL_BOAR, EnemyType.ANIMAL, AttackType.MELEE, MeleeEnemy::new)); // Tank
        registry.add(new EnemyBlueprintRanged("Scorpion", 8, 70, 79, 800, 180, 350, 75, 20, 0.15f, TextureManager.ANIMAL_SCORPION, EnemyType.ANIMAL, TextureManager.PROJECTILE, RangedEnemy::new)); // Tank
        registry.add(new EnemyBlueprintRanged("Shrimp Bug", 8, 72, 79, 900, 200, 320, 70, 10, 0.08f, TextureManager.ANIMAL_SHRIMP_BUG, EnemyType.ANIMAL, TextureManager.PROJECTILE, RangedEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Gecko", 8, 75, 79, 1200, 250, 300, 60, 10, 0.06f, TextureManager.ANIMAL_GECKO, EnemyType.ANIMAL, AttackType.MELEE, MeleeEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Goose", 6, 60, 75, 400, 80, 150, 35, 20, 0.02f, TextureManager.ANIMAL_GOOSE, EnemyType.ANIMAL, AttackType.MELEE, MeleeEnemy::new)); // Fast
        registry.add(new EnemyBlueprint("Spider", 7, 65, 79, 550, 110, 180, 40, 10, 0.04f, TextureManager.ANIMAL_SPIDER, EnemyType.ANIMAL, AttackType.MELEE, MeleeEnemy::new)); // Fast

        // --- DEVILS (Floors 80-100) ---
        registry.add(new EnemyBlueprint("Blue Devil", 4, 80, 90, 2000, 400, 400, 80, 10, 0.07f, TextureManager.DEVIL_BLUE, EnemyType.DEVIL, AttackType.MELEE, MeleeEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Skull Reindeer", 5, 80, 92, 2500, 500, 450, 90, 10, 0.16f, TextureManager.DEVIL_SKULL_REINDEER, EnemyType.DEVIL, AttackType.MELEE, MeleeEnemy::new)); // Tank
        registry.add(new EnemyBlueprintRanged("Fire Spirit", 7, 82, 100, 1800, 350, 700, 150, 10, 0.03f, TextureManager.DEVIL_FIRE_SPIRIT, EnemyType.DEVIL, TextureManager.PROJECTILE, RangedEnemy::new)); // Fast
        registry.add(new EnemyBlueprintRanged("Wings Devil", 8, 85, 100, 2200, 400, 650, 140, 20, 0.04f, TextureManager.DEVIL_WINGS, EnemyType.DEVIL, TextureManager.PROJECTILE, RangedEnemy::new)); // Fast
        registry.add(new EnemyBlueprint("One Eye", 6, 88, 100, 3500, 700, 600, 120, 10, 0.08f, TextureManager.DEVIL_EYE, EnemyType.DEVIL, AttackType.MELEE, MeleeEnemy::new)); // Generic
        registry.add(new EnemyBlueprintRanged("Griffin", 10, 90, 100, 3000, 600, 800, 180, 10, 0.07f, TextureManager.DEVIL_GRIFFIN, EnemyType.DEVIL, TextureManager.PROJECTILE, RangedEnemy::new)); // Generic
        registry.add(new EnemyBlueprint("Devil Red", 12, 92, 100, 6500, 1100, 850, 200, 10, 0.17f, TextureManager.DEVIL_RED, EnemyType.DEVIL, AttackType.MELEE, MeleeEnemy::new)); // Tank
        registry.add(new EnemyBlueprint("Goat Devil", 15, 95, 100, 12000, 2500, 1000, 250, 5, 0.20f, TextureManager.DEVIL_GOAT, EnemyType.DEVIL, AttackType.MELEE, MeleeEnemy::new)); // Tank
        registry.add(new EnemyBlueprint("Devil Boar", 8, 80, 95, 2800, 550, 420, 85, 10, 0.19f, TextureManager.DEVIL_BOAR, EnemyType.DEVIL, AttackType.MELEE, MeleeEnemy::new)); // Tank
    }
}

