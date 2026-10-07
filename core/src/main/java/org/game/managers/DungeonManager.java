package org.game.managers;

import com.badlogic.gdx.graphics.Texture;
import org.game.coreGame.GameManager;
import org.game.world.dungeon.DungeonDifficulty;
import org.game.world.dungeon.DungeonGenerator;
import org.game.world.rooms.*;

import java.util.HashMap;

/**
 * Manages the dungeon state, player navigation between rooms,
 * and background asset loading.
 */
public class DungeonManager {
    /** 2D grid representing the dungeon layout */
    Room[][] dungeon;
    DungeonDifficulty currentDifficulty;
    private GameManager game;

    /** Player's current grid coordinates */
    private int playerY, playerX;

    /** The texture of the current room's background */
    private Texture currentBG;

    /** Cache for room textures to prevent redundant loading from disk */
    private HashMap<String, Texture> roomCache;

    /**
     * Initializes the manager, generates a new dungeon based on difficulty,
     * and positions the player at the StartRoom.
     */
    public DungeonManager(GameManager game, DungeonDifficulty diff){
        this.game = game;
        this.roomCache = new HashMap<>();
        this.currentDifficulty = diff;

        // Generate the dungeon grid using the static DungeonGenerator
        this.dungeon = DungeonGenerator.generateDungeon(currentDifficulty);

        findStartingPosition();
        loadCurrentTexture();
    }

    // --- Getters ---

    /** Returns the Room object where the player is currently located */
    public Room getCurrentRoom(){
        return dungeon[playerY][playerX];
    }

    public Texture getCurrentBG() { return currentBG; }

    // --- Helpers & Logic ---

    /**
     * Retrieves the texture for the current room from the cache
     * or loads it from the internal assets if not already cached.
     */
    protected void loadCurrentTexture() {
        String key = getCurrentRoom().getTextureKey();
        if (!roomCache.containsKey(key)) {
            roomCache.put(key, new Texture("rooms/" + key + ".png"));
        }
        currentBG = roomCache.get(key);
    }

    /**
     * Moves the player by a relative delta (x, y) if the move is within bounds
     * and the target room exists.
     */
    public void movePlayer(int x, int y) {
        int nextX = playerX + x;
        int nextY = playerY + y;

        // Boundary check for the 2D array
        if (nextY >= 0 && nextY < dungeon.length && nextX >= 0 && nextX < dungeon[0].length) {
            // Check if the target cell is not empty
            if (dungeon[nextY][nextX] != null) {
                playerX = nextX;
                playerY = nextY;
                loadCurrentTexture();
                handleRoomEntry();
            }
        }
    }

    /**
     * Executes logic specific to the type of room the player just entered.
     */
    private void handleRoomEntry() {
        Room current = getCurrentRoom();

        // If it's a ChestRoom, trigger the loot generation based on difficulty
        if (current instanceof ChestRoom) {
            ((ChestRoom) current).onEnter(currentDifficulty.getDifficulty());
        }

        // If it's a BattleRoom and hasn't been cleared, pass the background to combat view
        if (current instanceof BattleRoom && !current.wasDiscovered()) {
            game.passBattleTexture(currentBG);
        }

        // If it's a BossRoom and hasn't been cleared, trigger boss battle state
        if (current instanceof BossRoom && !current.wasDiscovered()) {
            game.setBossBattle(true);
            game.passBattleTexture(currentBG);
        }

        // Mark the room as visited for the map/fog of war
        current.setDiscovered(true);
    }

    /**
     * Scans the dungeon grid to locate the StartRoom and sets player coordinates.
     */
    private void findStartingPosition() {
        for (int y = 0; y < dungeon.length; y++) {
            for (int x = 0; x < dungeon[y].length; x++) {
                if (dungeon[y][x] instanceof StartRoom) {
                    playerX = x;
                    playerY = y;
                    return;
                }
            }
        }
    }

    /**
     * Disposes of all loaded textures to free up memory.
     */
    public void dispose() {
        for (Texture tex : roomCache.values()) {
            tex.dispose();
        }
        roomCache.clear();
    }

    /**
     * Adds loot from a chest to the player's inventory via the LootManager.
     */
    public void addChestLoot() {
        game.getLootManager().generateChestLoot();
    }
}
