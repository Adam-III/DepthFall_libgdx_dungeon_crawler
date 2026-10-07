package org.game.ui.wholeUI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.game.coreGame.GameManager;
import org.game.coreGame.GameState;
import org.game.items.base.BaseArmor;
import org.game.items.base.Item;
import org.game.items.base.BaseShield;
import org.game.items.base.BaseWeapon;
import org.game.items.other.Key;
import org.game.managers.InventoryManager;
import org.game.ui.components.EquipmentModeInventory;
import org.game.ui.components.InventoryModeOptions;

public class InventoryUI extends Stage {
    private final Table itemGrid;
    private final ScrollPane scrollPane;
    private final GameManager game;
    private InventoryModeOptions modeOptions;
    private final InventoryManager inventoryManager;
    private final EquipmentModeInventory equipSelectMode;

    public InventoryUI(GameManager game, Viewport viewport){
        super(viewport);
        this.game = game;
        Table root = new Table();
        root.setFillParent(true);
        this.addActor(root);
        inventoryManager = game.getInventory();


        itemGrid = new Table();
        itemGrid.top().left();

        equipSelectMode = new EquipmentModeInventory(game, this);

        scrollPane = new ScrollPane(itemGrid, game.getSkin());
        scrollPane.setFadeScrollBars(false);

        Table window = new Table();
        float w = viewport.getWorldWidth() * 0.9f;
        float h = viewport.getWorldHeight() * 0.78f;
        window.add(scrollPane).size(w, h);

        Table top = new Table();

        Label title;

        if(game.isDungeonNull()){
            modeOptions = new InventoryModeOptions(game, this);
            title = new Label( "Inventory", game.getSkin());
            title.setFontScale(1.5f);

            top.add(title).center().padTop(15);
            top.add(equipSelectMode).padTop(15).row();
            root.add(top).top().padBottom(15).padTop(10).row();
            root.add(window).expand().center().row();
            root.add(modeOptions).bottom().center().padBottom(25).padTop(15);
        }
        else{
            TextButton backBtn = new TextButton("BACK", game.getSkin());
            backBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.updateState(GameState.DUNGEON);
                }

            });
            title = new Label( "Current Loot", game.getSkin());
            title.setFontScale(1.5f);

            top.add(title).padTop(10);
            top.add(equipSelectMode).padTop(10).row();
            root.add(top).top().padBottom(20).padTop(10).row();
            root.add(window).expand().center().row();
            root.add(backBtn).bottom().center().padBottom(25).padTop(15).size(50,20);

        }



        refreshDisplay();
    }

    public void refreshDisplay() {
        float currentScrollY = scrollPane.getScrollY();

        itemGrid.clearChildren();

        int columnCount = 0;
        int maxColumns = 7;

        Array<Item> list = inventoryManager.getItems();
        for (final Item item : list) {
            Table slot = createItemSlot(item);
            itemGrid.add(slot).expand().align(Align.top).padTop(10);

            columnCount++;
            if (columnCount >= maxColumns) {
                itemGrid.row();
                columnCount = 0;
            }
        }

        itemGrid.invalidateHierarchy();
        scrollPane.layout();

        scrollPane.setScrollY(currentScrollY);
        scrollPane.updateVisualScroll();
    }

    private Table createItemSlot(final Item item) {
        Table slot = new Table();

        Label name = new Label(item.getName(), game.getSkin());
        String level = "Lvl: " + item.getLevel();
        Label levelLbl = new Label(level, game.getSkin());
        Image img = new Image((item).getTexture());

        slot.add(name).top().center().row();
        slot.add(levelLbl).top().center().row();
        slot.add(img).size(64, 64).row();

        // Shows the same sell value the shop pays
        String sellValue = item.getSellPrice() + " G";
        String info;
        if (item instanceof BaseShield shield) {
            info = "HP: " + shield.getHP() + "\nDefense: " +
                Math.round(shield.getDefense() * 100) + "%\n" + sellValue;
        } else if (item instanceof BaseWeapon weapon) {
            info = "Attack: " + weapon.getAttack() + "\n" + sellValue;
        } else if (item instanceof BaseArmor armor) {
            info = "HP: " + armor.getHP() + "\nDefense: " +
                Math.round(armor.getDefense() * 100) + "%\n" + sellValue;
        } else if (item instanceof Key) {
            info = "Opens chests";
        } else info = sellValue;

        Label label = new Label(info, game.getSkin());
        label.setAlignment(Align.center);
        label.setFontScale(0.85f);
        slot.add(label).padBottom(5).row();

        return slot;
    }
}
