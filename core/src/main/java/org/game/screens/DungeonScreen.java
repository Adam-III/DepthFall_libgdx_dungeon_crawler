package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import org.game.coreGame.GameManager;
import org.game.managers.DungeonManager;
import org.game.ui.wholeUI.DungeonUI;
import org.game.world.dungeon.DungeonDifficulty;
import org.game.world.rooms.ChestRoom;
import org.game.world.rooms.Room;
import org.game.world.objects.Chest;

/**
 * Handles the dungeon exploration phase of the game.
 * Manages room rendering, chest interactions, and player movement through the dungeon map.
 */
public class DungeonScreen extends BaseScreen {
    private final GameManager game;
    private DungeonUI ui;
    private final DungeonManager dungeonManager;

    private final Texture chestClosed;
    private final Texture chestOpened;
    private final Texture text; // "Press E to Open" prompt

    private int pendingX, pendingY; // Stores target coordinates for room transitions

    public DungeonScreen(final GameManager game, DungeonDifficulty diff) {
        super(game);
        this.game = game;
        // DungeonManager handles the grid and room generation logic
        this.dungeonManager = new DungeonManager(game, diff);
        this.ui = new DungeonUI(this, game);

        this.chestClosed = new Texture("objects/ChestClosed.png");
        this.chestOpened = new Texture("objects/ChestOpened.png");
        this.text = new Texture("ui/text/EtoOpen.png");
    }

    @Override
    public void show() {
        // Set input focus to the UI stage (e.g., for navigation buttons)
        Gdx.input.setInputProcessor(ui.getStage());
    }

    @Override
    public void render(float delta) {
        input();
        logic(delta);
        draw(delta);
    }

    /** Handles player input, specifically for interacting with objects in the world. */
    private void input() {
        if (isFading) return; // Prevent interaction during transitions

        Room room = dungeonManager.getCurrentRoom();
        if (room instanceof ChestRoom) {
            ChestRoom chestRoom = (ChestRoom) room;
            Vector2 mouse = game.viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

            for (Chest chest : chestRoom.getChests()) {
                // If hovering over an unlooted chest and player has a key
                if (!chest.isLooted() && chest.containsMouse(mouse.x, mouse.y)) {
                    if (game.getKeyCount() > 0) {
                        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                            handleChestOpening(chest);
                        }
                    }
                }
            }
        }
    }

    /** Logic for consuming keys and generating loot from chests. */
    private void handleChestOpening(Chest chest) {
        // Remove key from inventory or temporary loot pile
        if(game.getInventory().hasItem("Key")) {
            game.getInventory().removeFromInventory("Key");
        } else {
            game.getLootManager().removeFromLootInventory("Key");
        }

        game.addKey(-1);
        game.addGold(chest.getGoldAmount());
        dungeonManager.addChestLoot();
        chest.setLooted(true);
    }

    private void logic(float delta) {
        updateTransition(delta, 2.5f);
    }

    /** Renders the room background, interactive objects, and UI. */
    private void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.spriteBatch.begin();

        Vector2 mouse = game.viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

        // 1. Draw Background
        Texture bg = dungeonManager.getCurrentBG();
        if (bg != null) {
            game.spriteBatch.draw(bg, 0, 0, 960, 540);
        }

        // 2. Draw Interactive Objects (Chests)
        Room room = dungeonManager.getCurrentRoom();
        if (room instanceof ChestRoom) {
            for (Chest chest : ((ChestRoom) room).getChests()) {
                Texture tex = chest.isLooted() ? chestOpened : chestClosed;
                game.spriteBatch.draw(tex, chest.getX(), chest.getY(), 64, 64);

                // Show "Press E" prompt if hovered
                if (!chest.isLooted() && chest.containsMouse(mouse.x, mouse.y)) {
                    game.spriteBatch.draw(text, chest.getX(), chest.getY() - 72, 64, 64);
                }
            }
        }

        game.spriteBatch.end();

        // 3. Render UI Layer
        ui.update(dungeonManager.getCurrentRoom());
        ui.render(delta);

        // 4. Transitions Layer
        game.spriteBatch.begin();
        drawTransition();
        game.spriteBatch.end();
    }

    /**
     * Overloaded transition start specifically for moving between dungeon rooms.
     * @param dx Move amount on X axis (e.g., 1 for East, -1 for West)
     * @param dy Move amount on Y axis (e.g., 1 for North, -1 for South)
     */
    public void startTransition(int dx, int dy) {
        if (isFading) return;
        this.pendingX = dx;
        this.pendingY = dy;
        this.isFading = true;
        super.startTransition();
    }

    /**
     * Effectively moves the player to the new room once the screen is fully black.
     */
    @Override
    public void onFadeOutComplete() {
        dungeonManager.movePlayer(pendingX, pendingY);
    }

    @Override
    public void dispose() {
        if (disposed) return;
        dungeonManager.dispose();
        ui.dispose();
        chestClosed.dispose();
        chestOpened.dispose();
        text.dispose();
        super.dispose();
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }
}
