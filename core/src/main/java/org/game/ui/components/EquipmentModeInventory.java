package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;
import org.game.managers.InventoryManager;
import org.game.ui.wholeUI.InventoryUI;

/**
 * A UI component (part of the Inventory) that provides filtering buttons.
 * It allows the player to switch between viewing Weapons, Shields, Armor, or all items.
 */
public class EquipmentModeInventory extends Table {
    private final GameManager game;
    private final InventoryManager manager;
    private final InventoryUI ui;

    public EquipmentModeInventory(GameManager game, InventoryUI ui) {
        this.game = game;
        this.manager = game.getInventory();
        this.ui = ui;

        // --- Filter Buttons Construction ---

        // Button to filter and show only Weapons
        TextButton weaponBtn = new TextButton("WEAPON", game.getSkin());
        weaponBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.updateState("WEAPON"); // Change logic state
                ui.refreshDisplay();           // Force UI redraw with new filter
            }
        });

        // Button to filter and show only Shields
        TextButton shieldBtn = new TextButton("SHIELDS", game.getSkin());
        shieldBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.updateState("SHIELD");
                ui.refreshDisplay();
            }
        });

        // Button to filter and show only Armor
        TextButton armorBtn = new TextButton("ARMOR", game.getSkin());
        armorBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.updateState("ARMOR");
                ui.refreshDisplay();
            }
        });

        // Button to reset filters and show all items
        TextButton allBtn = new TextButton("ALL", game.getSkin());
        allBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.updateState("DEFAULT");
                ui.refreshDisplay();
            }
        });

        // --- Layout Positioning ---
        // Organizes buttons in a horizontal row inside this table
        this.add(weaponBtn).left().pad(5).padLeft(50).size(80, 20);
        this.add(shieldBtn).left().pad(5).size(70, 20);
        this.add(armorBtn).left().pad(5).size(70, 20);
        this.add(allBtn).left().pad(5).size(70, 20);
    }
}
