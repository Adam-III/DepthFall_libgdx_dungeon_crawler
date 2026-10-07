package org.game.ui.wholeUI;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.game.coreGame.GameManager;
import org.game.entities.base.Character;
import org.game.items.base.BaseArmor;
import org.game.items.base.Item;
import org.game.items.base.BaseShield;
import org.game.items.base.BaseWeapon;
import org.game.managers.EquipmentManager;
import org.game.screens.EquipmentScreen;
import org.game.ui.components.EquipmentCharacterMode;
import org.game.ui.components.EquipmentSelectMode;
import org.game.ui.components.InventoryModeOptions;

/**
 * The Stage responsible for the Equipment selection interface.
 * It allows players to pick a character and assign weapons, shields, or armor
 * from the inventory to that specific hero.
 */
public class EquipmentUI extends Stage {
    private final Table itemGrid, root, top, window;
    private final ScrollPane scrollPane;
    private final GameManager game;
    private final EquipmentManager manager;
    private InventoryModeOptions modeOptions;
    private final EquipmentCharacterMode equipCharacterMode;
    private final EquipmentSelectMode equipSelectMode;
    private final EquipmentScreen screen;
    private Label characterStats, title;

    public EquipmentUI(GameManager game, FitViewport viewport, EquipmentManager manager, EquipmentScreen screen) {
        super(viewport);
        this.game = game;
        this.manager = manager;
        this.screen = screen;

        // Main container
        root = new Table();
        root.setFillParent(true);
        this.addActor(root);

        // Sub-menus for choosing which character and which slot to edit
        modeOptions = new InventoryModeOptions(game, this);
        equipCharacterMode = new EquipmentCharacterMode(game, this, manager);
        equipSelectMode = new EquipmentSelectMode(game, this, manager);

        // Grid for available items
        itemGrid = new Table();
        itemGrid.top().left();

        scrollPane = new ScrollPane(itemGrid, game.getSkin());
        scrollPane.setFadeScrollBars(false);

        // Define dimensions for the scrollable item area
        window = new Table();
        float w = viewport.getWorldWidth() * 0.75f;
        float h = viewport.getWorldHeight() * 0.55f;
        window.add(scrollPane).size(w, h).padLeft(180).padBottom(15).padTop(40);

        top = new Table();
        title = new Label("Select Equipment", game.getSkin());
        title.setFontScale(1.5f);

        refreshDisplay();
    }

    /**
     * Rebuilds the UI to reflect changes in selected character, equipment type, or stats.
     */
    public void refreshDisplay() {
        float currentScrollY = scrollPane.getScrollY();

        itemGrid.clearChildren();

        // Update the stat preview for the currently selected character
        Character c = screen.getCurrentCharacter();
        if (c != null) {
            characterStats = new Label("HP: " + c.getCurrentHP() + "\nAttack: " + c.getAttack() +
                "\nAttack Speed: " + c.getAttackSpeed() + "\nDefense: " + (int) (c.getDefense() * 100) + "%", game.getSkin());
        } else {
            characterStats = new Label("\n\n\n", game.getSkin());
        }
        characterStats.setFontScale(0.8f);

        // Reset and rebuild the root table layout
        top.reset();
        root.reset();

        top.add(equipCharacterMode).padTop(15);
        top.add(title).padTop(15);
        top.add(equipSelectMode).padTop(15).row();

        root.add(top).top().padBottom(15).padTop(10).row();
        root.add(characterStats).left().padLeft(20).row();
        root.add(window).expand().center().padTop(20).row();
        root.add(modeOptions).bottom().center().padBottom(30).padTop(10).size(50, 20);

        // Populate the item grid
        int columnCount = 0;
        int maxColumns = 5;
        Array<Item> list = manager.getItems();

        for (final Item item : list) {
            if (item != null) {
                Table slot = createItemSlot(item);
                itemGrid.add(slot).expand().align(Align.top).pad(20);

                columnCount++;
                if (columnCount >= maxColumns) {
                    itemGrid.row();
                    columnCount = 0;
                }
            }
        }

        // Finalize layout updates and restore scroll position
        itemGrid.invalidateHierarchy();
        scrollPane.layout();
        scrollPane.setScrollY(currentScrollY);
        scrollPane.updateVisualScroll();
    }

    /**
     * Creates a visual representation of an item, showing its stats and a 'Select' button.
     */
    private Table createItemSlot(final Item item) {
        Table slot = new Table();

        Label name = new Label(item.getName(), game.getSkin());
        String level = "Lvl: " + item.getLevel();
        Label levelLbl = new Label(level, game.getSkin());
        Image img = new Image(item.getTexture());

        slot.add(name).top().center().row();
        slot.add(levelLbl).top().center().row();
        slot.add(img).size(64, 64).row();

        // Switch info based on the currently selected Equipment Mode
        String info;
        switch (manager.getMode()) {
            case SHIELD -> {
                BaseShield shield = (BaseShield) item;
                info = "HP: " + shield.getHP() + "\nDefense: " + (int)(shield.getDefense() * 100) + "%";
            }
            case ARMOR -> {
                BaseArmor armor = (BaseArmor) item;
                info = "HP: " + armor.getHP() + "\nDefense: " + (int)(armor.getDefense() * 100) + "%";
            }
            case WEAPON -> {
                BaseWeapon weapon = (BaseWeapon) item;
                info = "Attack: " + weapon.getAttack();
            }
            default -> info = "NaN";
        }

        Label label = new Label(info, game.getSkin());
        label.setAlignment(Align.center);
        label.setFontScale(0.85f);
        slot.add(label).padBottom(5).row();

        // Action button to equip the item to the character
        TextButton selectBtn = new TextButton("Select", game.getSkin());
        selectBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.equipItem(item);
                // Refresh is usually called via an update listener or manually after equipping
                refreshDisplay();
            }
        });

        slot.add(selectBtn).size(100, 20);

        return slot;
    }
}
