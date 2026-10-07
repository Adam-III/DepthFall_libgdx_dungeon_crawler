package org.game.world.rooms;

/**
 * The base class for all dungeon rooms.
 * Defines position, discovery status, and directional connectivity.
 */
public abstract class Room {
    protected int x;
    protected int y;
    protected boolean discovered = false;
    protected RoomType type;

    // Connection flags (determined by the DungeonGenerator)
    public boolean north = false, south = false, east = false, west = false;

    public Room(int x, int y, RoomType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Triggered when the player steps into the room.
     * Subclasses (like BattleRoom) implement this to start encounters.
     */
    public abstract void onEnter();

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public boolean wasDiscovered() {
        return discovered;
    }

    public RoomType getType() {
        return type;
    }

    /**
     * Generates a unique string identifier for selecting the correct texture.
     * Example: A BattleRoom with exits North and South returns "BattleRoom_NS".
     * * @return A string key matching your texture asset naming convention.
     */
    public String getTextureKey() {
        StringBuilder key = new StringBuilder();

        // Get the specific class name (e.g., "ChestRoom", "BattleRoom")
        key.append(this.getClass().getSimpleName());
        key.append("_");

        // Add suffixes for each valid direction
        if (north) key.append("N");
        if (south) key.append("S");
        if (east)  key.append("E");
        if (west)  key.append("W");

        return key.toString();
    }
}
