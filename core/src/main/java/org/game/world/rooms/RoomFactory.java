package org.game.world.rooms;

import org.game.world.dungeon.DungeonDifficulty;
import org.game.world.dungeon.RoomCounter;

import java.util.Random;

/**
 * The RoomFactory is responsible for instantiating specific Room subclasses
 * based on the RoomType provided by the DungeonGenerator.
 * It handles the probability logic for battles, chests, and hallways.
 */
public class RoomFactory {

    private static final Random RNG = new Random();

    /**
     * Creates a Room instance and updates the global RoomCounter.
     * * @param type The abstract type from the generator (START, MAIN_PATH, etc.).
     * @param x Grid X coordinate.
     * @param y Grid Y coordinate.
     * @param diff The difficulty settings containing spawn chances.
     * @param counter Tracker for analytics and balancing.
     * @param branchId ID of the current branch (used to limit chests per branch).
     * @param chestPerBranch Array tracking how many chests are in each branch.
     * @return A concrete Room subclass (StartRoom, BattleRoom, ChestRoom, etc.).
     */
    public static Room create(RoomType type, int x, int y,
                              DungeonDifficulty diff, RoomCounter counter,
                              Integer branchId, int[] chestPerBranch) {

        counter.totalRooms++;

        // Using modern Java switch expression to return the room instance
        return switch (type) {

            case START -> new StartRoom(x, y);

            case FINAL_ROOM -> new BossRoom(x, y);

            case MAIN_PATH -> {
                // Determine if a hallway or a battle occurs on the main path
                if (RNG.nextDouble() < DungeonDifficulty.MAIN_PATH_BATTLE_CHANCE) {
                    counter.battleRooms++;
                    yield new BattleRoom(x, y);
                } else {
                    counter.hallways++;
                    yield new Hallway(x, y);
                }
            }

            case BRANCH_PATH -> {
                /* --- Chest Logic --- */

                // 1. Roll for a chest based on global chance
                if (branchId != null &&
                    chestPerBranch[branchId] == 0 &&
                    RNG.nextDouble() < DungeonDifficulty.BRANCH_CHEST_CHANCE &&
                    counter.chestRooms < diff.getMaxChests()) {

                    chestPerBranch[branchId]++;
                    counter.chestRooms++;
                    yield new ChestRoom(x, y);
                }

                // 2. Force a chest if we haven't met the minimum requirement for the floor
                if (counter.chestRooms < diff.getMinChests() &&
                    branchId != null &&
                    chestPerBranch[branchId] == 0) {

                    chestPerBranch[branchId]++;
                    counter.chestRooms++;
                    yield new ChestRoom(x, y);
                }

                /* --- Battle vs Hallway Logic --- */
                if (RNG.nextDouble() < DungeonDifficulty.BRANCH_BATTLE_CHANCE) {
                    counter.battleRooms++;
                    yield new BattleRoom(x, y);
                }

                // Default fallback if no chest or battle was rolled
                counter.hallways++;
                yield new Hallway(x, y);
            }

            case EMPTY -> null; // Should not happen in a valid dungeon generation
        };
    }
}
