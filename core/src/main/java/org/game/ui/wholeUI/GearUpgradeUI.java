package org.game.ui.wholeUI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.game.coreGame.GameManager;
import org.game.items.base.BaseArmor;
import org.game.items.base.Item;
import org.game.items.base.BaseShield;
import org.game.items.base.BaseWeapon;
import org.game.managers.GearUpgradeManager;
import org.game.screens.GearUpgradeScreen;
import org.game.ui.components.GoldCount;
import org.game.ui.components.InventoryModeOptions;

public class GearUpgradeUI extends Stage {
    private final GameManager game;
    private final GearUpgradeScreen screen;
    private final Table itemGrid;
    private final ScrollPane scrollPane;
    private final GoldCount goldCount;
    private boolean isBuyMode;
    private InventoryModeOptions modeOptions;
    private final GearUpgradeManager manager;

    public GearUpgradeUI(Viewport viewport, GameManager game, GearUpgradeScreen screen, GearUpgradeManager manager) {
        super(viewport);
        this.game = game;
        this.screen = screen;
        this.manager = manager;

        goldCount = new GoldCount(game);
        modeOptions = new InventoryModeOptions(game, this);

        Table root = new Table();
        root.setFillParent(true);
        this.addActor(root);


        Table top = new Table();

        Label shopTitle = new Label( "Gear Upgrades", game.getSkin());
        shopTitle.setFontScale(1.5f);
        top.add(goldCount).left().top().padTop(2);
        top.add(shopTitle).padTop(10).row();

        itemGrid = new Table();
        itemGrid.top().left();

        scrollPane = new ScrollPane(itemGrid, game.getSkin());
        scrollPane.setFadeScrollBars(false);

        Table window = new Table();
        float w = viewport.getWorldWidth() * 0.9f;
        float h = viewport.getWorldHeight() * 0.8f;
        window.add(scrollPane).size(w, h);

        root.add(top).top().padBottom(10).padTop(10).row();
        root.add(window).expand().center().row();
        root.add(modeOptions).center().padBottom(20).padTop(10);

        refreshDisplay();
    }

    public void refreshDisplay() {
        float currentScrollY = scrollPane.getScrollY();

        itemGrid.clearChildren();

        int columnCount = 0;
        int maxColumns = 7;

        Array<Item> list = manager.getItems();


        for (final Item item : list) {
            if(item == null) continue;
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
        slot.add(img).size(64, 64).padBottom(5).row();

        String info;
        if (item instanceof BaseShield) {
            info = "HP: " + ((BaseShield) item).getHP() + " + " + item.getUpgradeAmount() + "\nDefense: " +
                Math.ceil(((BaseShield) item).getDefense()*100) + "%";
        }else if(item instanceof BaseWeapon){
            info = "Attack: " + ((BaseWeapon) item).getAttack() + " + " + item.getUpgradeAmount() + "\n";
        }else if (item instanceof BaseArmor){
            info = "HP: " + ((BaseArmor) item).getHP() + " + " + item.getUpgradeAmount() + "\nDefense: " +
                Math.ceil(((BaseArmor) item).getDefense()*100) + "%";
        }else info = "\n";

        Label label = new Label(info, game.getSkin());
        label.setAlignment(Align.center);
        label.setFontScale(0.85f);
        slot.add(label).padBottom(5).row();

        TextButton upgradeBtn = new TextButton("Upgrade for " + item.getUpgradePrice() + "G", game.getSkin());
        upgradeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    if (manager.upgradeItem(item)) {
                        refreshDisplay();
                    }
            }
        });

        upgradeBtn.getLabel().setFontScale(0.8f);
        slot.add(upgradeBtn).bottom().padBottom(5).size(120, 30);

        return slot;
    }
}
