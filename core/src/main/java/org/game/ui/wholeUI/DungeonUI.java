package org.game.ui.wholeUI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;
import org.game.coreGame.GameState;
import org.game.screens.DungeonScreen;
import org.game.ui.components.*;
import org.game.world.rooms.Room;

/**
 * The main Heads-Up Display (HUD) for the Dungeon exploration phase.
 * It manages the stage where all dungeon-related UI elements (counters, movement arrows,
 * and navigation buttons) are drawn.
 */
public class DungeonUI {
    private Stage stage;
    private MovementArrowsUI movementArrows;
    private GoldCount goldCount;
    private KeyCounter keyCount;
    private FloorCounter floorCounter;
    private KnowledgeOrbCount knowledgeOrbCount;

    public DungeonUI(DungeonScreen screen, GameManager game) {
        // Create a new stage using the shared viewport and batch
        this.stage = new Stage(game.viewport, game.spriteBatch);

        // --- Component Initialization ---
        this.movementArrows = new MovementArrowsUI(screen, null);
        this.goldCount = new GoldCount(game);
        this.knowledgeOrbCount = new KnowledgeOrbCount(game);
        this.keyCount = new KeyCounter(game);
        this.floorCounter = new FloorCounter(game);

        // --- Inventory Shortcut ---
        TextButton InventoryBtn = new TextButton("Inventory", game.getSkin());
        InventoryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch game state to view collected loot
                game.updateState(GameState.INVENTORY);
            }
        });

        // --- UI Layout Assembly ---
        Table root = new Table();
        root.setFillParent(true); // Overlay the entire screen
        stage.addActor(root);

        // Aligning all counters to the top-right corner
        root.add(floorCounter).top().right().padTop(5).row();
        root.add(goldCount).expand().top().right().row();
        root.add(knowledgeOrbCount).expand().top().right().row();
        root.add(keyCount).top().right().padBottom(280).row();

        // Positioning the Inventory button at the bottom-right
        root.add(InventoryBtn).bottom().right().padBottom(100).padRight(30).size(100, 20);

        root.row();

        // Movement arrows are added as a separate actor since they often have their own internal table
        stage.addActor(movementArrows.getTable());
    }

    /**
     * Updates the UI based on the current room's state (e.g., hiding/showing arrows).
     * @param currentRoom The room the player has just entered.
     */
    public void update(Room currentRoom) {
        movementArrows.update(currentRoom);
    }

    /**
     * Standard LibGDX lifecycle methods for acting and drawing the stage.
     */
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public Stage getStage() { return stage; }

    public void dispose(){
        stage.dispose();
    }
}
