package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;
import org.game.coreGame.GameState;

/**
 * A navigation bar used in various menu screens (Inventory, Shop, Upgrades, etc.).
 * It dynamically generates buttons to switch between different game states
 * and hides the button for the currently active state to keep the UI clean.
 */
public class InventoryModeOptions extends Table {
    private final GameManager game;
    private final Stage ui;

    public InventoryModeOptions(final GameManager game, Stage ui) {
        this.game = game;
        this.ui = ui;

        // --- Button Definitions & State Transitions ---

        // Shop: Buy items and equipment
        TextButton shopBtn = new TextButton("SHOP", game.getSkin());
        shopBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.SHOP);
            }
        });

        // Play: Enter the dungeon
        TextButton playBtn = new TextButton("PLAY", game.getSkin());
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setDungeonDifficulty();
                game.updateState(GameState.DUNGEON);
            }
        });

        // Saves: opens saves menu
        TextButton mainMenuBtn = new TextButton("MAIN MENU", game.getSkin());
        mainMenuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.START_MENU);
            }
        });

        // Unlock Heroes: Meta-progression for new character classes
        TextButton unlockBtn = new TextButton("UNLOCK HEROES", game.getSkin());
        unlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.UNLOCK_HEROES);
            }
        });

        // Inventory: View all collected items
        TextButton inventoryBtn = new TextButton("INVENTORY", game.getSkin());
        inventoryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.INVENTORY);
            }
        });

        // Selection: Choose which heroes are in the active party
        TextButton selectionBtn = new TextButton("SELECT PARTY", game.getSkin());
        selectionBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.PARTY_SELECTION);
            }
        });

        // Equipment: Equipping items onto specific heroes
        TextButton equipmentBtn = new TextButton("SELECT GEAR", game.getSkin());
        equipmentBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.EQUIPMENT_SELECTION);
            }
        });

        // Upgrade: Improve base stats of equipment
        TextButton upgradeBtn = new TextButton("UPGRADE GEAR", game.getSkin());
        upgradeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.GEAR_UPGRADE);
            }
        });

        // Global Upgrades: Permanent stat boosts (Meta-progression)
        TextButton globalUpgradesBtn = new TextButton("UPGRADES", game.getSkin());
        globalUpgradesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.GLOBAL_UPGRADE);
            }
        });

        // --- Layout Logic ---
        // We add buttons horizontally, but only if they don't point to the current screen.

        this.add(playBtn).left().pad(5).padRight(10).size(50, 20);
        this.add(mainMenuBtn).left().pad(5).padRight(50).size(100, 20);

        if (game.currentState != GameState.PARTY_SELECTION)
            this.add(selectionBtn).left().pad(5).size(120, 20);

        if (game.currentState != GameState.EQUIPMENT_SELECTION)
            this.add(equipmentBtn).left().pad(5).size(120, 20);

        if (game.currentState != GameState.INVENTORY)
            this.add(inventoryBtn).left().pad(5).size(100, 20);

        this.add(shopBtn).left().pad(5).size(50, 20);

        if (game.currentState != GameState.GEAR_UPGRADE)
            this.add(upgradeBtn).left().pad(5).size(120, 20);

        this.add(unlockBtn).left().pad(5).size(130, 20);
        this.add(globalUpgradesBtn).left().pad(5).size(90, 20);
    }
}
