package org.game.world.dungeon;

/**
 * A simple data tracker used during the dungeon generation process.
 * It stores the count of different room types to provide analytics
 * or to help the Factory balance the distribution of encounters and rewards.
 */
public class RoomCounter {
    /** Total number of non-empty rooms in the dungeon. */
    public int totalRooms = 0;

    /** Number of rooms containing enemy encounters. */
    public int battleRooms = 0;

    /** Number of rooms containing treasure chests. */
    public int chestRooms = 0;

    /** Number of empty or transitional rooms (hallways). */
    public int hallways = 0;

    /**
     * Optional: Helper method to get a summary of the generation (for logging/debug).
     */
    @Override
    public String toString() {
        return String.format("Dungeon Stats: Total: %d | Battles: %d | Chests: %d | Hallways: %d",
            totalRooms, battleRooms, chestRooms, hallways);
    }
}
