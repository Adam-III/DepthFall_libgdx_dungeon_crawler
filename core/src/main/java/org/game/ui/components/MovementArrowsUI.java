package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.game.coreGame.GameManager;
import org.game.managers.TextureManager;
import org.game.screens.DungeonScreen;
import org.game.world.rooms.Room;

/**
 * UI Component that manages movement arrows in the Dungeon view.
 * It maps directional buttons to coordinate changes (transitioning between rooms)
 * and updates visibility based on room exits.
 */
public class MovementArrowsUI {
    private Table rootTable;
    private ImageButton btnN, btnS, btnE, btnW;

    public MovementArrowsUI(final DungeonScreen screen, GameManager game){
        rootTable = new Table();
        rootTable.setFillParent(true); // Overlay the full screen for centering

        // --- Button Initialization ---
        // Textures are shared through TextureManager, so new dungeons do not reload them.
        btnN = new ImageButton(new TextureRegionDrawable(TextureManager.ARROW_N));
        btnS = new ImageButton(new TextureRegionDrawable(TextureManager.ARROW_S));
        btnE = new ImageButton(new TextureRegionDrawable(TextureManager.ARROW_E));
        btnW = new ImageButton(new TextureRegionDrawable(TextureManager.ARROW_W));

        // --- Movement Listeners ---
        // North: (0, 1) increment in dungeon grid
        btnN.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.startTransition(0, 1);
            }
        });

        // South: (0, -1) decrement in dungeon grid
        btnS.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.startTransition(0, -1);
            }
        });

        // East: (1, 0) increment on X axis
        btnE.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.startTransition(1, 0);
            }
        });

        // West: (-1, 0) decrement on X axis
        btnW.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.startTransition(-1, 0);
            }
        });

        // --- UI Layout Assembly ---
        // Top: North arrow
        rootTable.add(btnN).expand().top().padTop(41).padRight(160).row();

        // Middle: West and East arrows in a sub-table for horizontal alignment
        Table middleRow = new Table();
        middleRow.add(btnW).expandX().left().padTop(6).padLeft(45);
        middleRow.add(btnE).expandX().right().padTop(6).padRight(205);

        rootTable.add(middleRow).expandY().center().fillX().row();

        // Bottom: South arrow
        rootTable.add(btnS).expand().bottom().padBottom(170).padRight(160);
    }

    /**
     * Updates arrow visibility based on the available exits in the current room.
     * @param currentRoom The logic object representing the player's current location.
     */
    public void update(Room currentRoom){
        btnN.setVisible(currentRoom.north);
        btnS.setVisible(currentRoom.south);
        btnE.setVisible(currentRoom.east);
        btnW.setVisible(currentRoom.west);
    }

    public Actor getTable() { return rootTable; }
}
