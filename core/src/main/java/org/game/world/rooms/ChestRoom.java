package org.game.world.rooms;

import com.badlogic.gdx.utils.Array;
import org.game.world.objects.Chest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specialized room that contains treasure chests.
 * Usually found at the end of side branches to reward exploration.
 */
public class ChestRoom extends Room {
    private static final Logger logger = LoggerFactory.getLogger(ChestRoom.class);
    private Array<Chest> chests;
    private boolean initialized = false;

    public ChestRoom(int x, int y) {
        // These rooms are typically part of the branch path
        super(x, y, RoomType.BRANCH_PATH);
        this.chests = new Array<>();
    }

    public Array<Chest> getChests() { return chests; }

    /**
     * Logic for generating chests based on floor difficulty.
     * Fixed positions are used to align 3 chests in the center of the room.
     * * @param difficulty The current floor/dungeon level to scale loot quality.
     */
    public void onEnter(int difficulty) {
        // Prevent double generation if the player re-enters the room
        if (initialized) return;

        // Generate 3 chests with specific screen coordinates (x, y)
        // These are spaced out horizontally (256, 368, 480)
        chests.add(new Chest(256, 290, difficulty));
        chests.add(new Chest(368, 290, difficulty));
        chests.add(new Chest(480, 290, difficulty));

        this.initialized = true;
        logger.debug("ChestRoom initialized with 3 chests at difficulty {}", difficulty);
    }

    /**
     * Implementation of the abstract method from Room.
     * Currently empty because this room requires difficulty data via the overloaded onEnter(int).
     */
    @Override
    public void onEnter() {
        // Logic could be added here if there's a default state
        // that doesn't depend on difficulty.
    }
}
