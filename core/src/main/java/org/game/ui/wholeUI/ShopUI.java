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
import org.game.items.other.Key;
import org.game.screens.ShopScreen;
import org.game.ui.components.GoldCount;
import org.game.ui.components.ShopModeOptions;

/**
 * The main UI Stage for the Shop system.
 * Handles the display of items in a grid, scrolling mechanics, and switching
 * between Buying from the shop and Selling from the player's inventory.
 */
public class ShopUI extends Stage {
    private final GameManager game;
    private final ShopScreen screen;
    private final Table itemGrid;
    private final ScrollPane scrollPane;
    private final GoldCount goldCount;
    private boolean isBuyMode; // Toggle between Buying and Selling
    private ShopModeOptions modeOptions;

    public ShopUI(Viewport viewport, GameManager game, ShopScreen screen) {
        super(viewport);
        this.game = game;
        this.screen = screen;
        this.isBuyMode = true;

        // Custom UI components
        goldCount = new GoldCount(game);
        modeOptions = new ShopModeOptions(game, this, screen.getShopManager());

        // Main root table for layout organization
        Table root = new Table();
        root.setFillParent(true);
        this.addActor(root);

        // Header Table (Gold display and Title)
        Table top = new Table();
        Label shopTitle = new Label("SHOP", game.getSkin());
        shopTitle.setFontScale(1.5f);
        top.add(goldCount).left().top().padTop(2);
        top.add(shopTitle).padTop(10).row();

        // The grid that holds all item slots
        itemGrid = new Table();
        itemGrid.top().left();

        // ScrollPane allows the user to swipe/scroll through the stock
        scrollPane = new ScrollPane(itemGrid, game.getSkin());
        scrollPane.setFadeScrollBars(false);

        // Container window for the scroll pane to maintain sizing
        Table window = new Table();
        float w = viewport.getWorldWidth() * 0.9f;
        float h = viewport.getWorldHeight() * 0.8f;
        window.add(scrollPane).size(w, h);

        // Assemble the final UI
        root.add(top).top().padBottom(10).padTop(10).row();
        root.add(window).expand().center().row();
        root.add(modeOptions).center().padBottom(20).padTop(10);

        refreshDisplay();
    }

    /**
     * Clears and repopulates the item grid based on the current mode (Buy or Sell).
     * Maintains the scroll position for a smoother user experience.
     */
    public void refreshDisplay() {
        float currentScrollY = scrollPane.getScrollY();
        itemGrid.clearChildren();

        int columnCount = 0;
        int maxColumns = 7;

        // Pick data source: Shop stock or Player inventory
        Array<Item> list = (isBuyMode) ? screen.getShopManager().getStock() : game.getInventory().getInventory();

        for (final Item item : list) {
            // Keys are usually managed separately, not sold in the regular grid
            if(item instanceof Key) continue;

            Table slot = createItemSlot(item);
            itemGrid.add(slot).expand().align(Align.top).padTop(10);

            columnCount++;
            if (columnCount >= maxColumns) {
                itemGrid.row();
                columnCount = 0;
            }
        }

        // Force Scene2D to recalculate layout positions
        itemGrid.invalidateHierarchy();
        scrollPane.layout();
        scrollPane.setScrollY(currentScrollY);
        scrollPane.updateVisualScroll();
    }

    /**
     * Creates a visual slot for a single item including name, stats, and an action button.
     */
    private Table createItemSlot(final Item item) {
        Table slot = new Table();

        Label name = new Label(item.getName(), game.getSkin());
        String level = "Lvl: " + item.getLevel();
        Label levelLbl = new Label(level, game.getSkin());
        Image img = new Image(item.getTexture());

        slot.add(name).top().center().row();
        slot.add(levelLbl).top().center().row();
        slot.add(img).size(64, 64).padBottom(5).row();

        // Dynamically build stat info string based on Item subclass
        String info;
        if (item instanceof BaseShield) {
            info = "HP: " + ((BaseShield) item).getHP() + "\nDefense: " +
                (int)(((BaseShield) item).getDefense() * 100) + "%";
        } else if (item instanceof BaseWeapon) {
            info = "Attack: " + ((BaseWeapon) item).getAttack() + "\n";
        } else if (item instanceof BaseArmor) {
            info = "HP: " + ((BaseArmor) item).getHP() + "\nDefense: " +
                (int)(((BaseArmor) item).getDefense() * 100) + "%";
        } else {
            info = "\n";
        }

        Label label = new Label(info, game.getSkin());
        label.setAlignment(Align.center);
        label.setFontScale(0.85f);
        slot.add(label).padBottom(5).row();

        // Transaction Button (Buy or Sell)
        String btnText = (isBuyMode) ? "BUY for " + item.getPrice() + "G" :
            "SELL for " + (int)(item.getSellPrice()) + "G";
        TextButton buyBtn = new TextButton(btnText, game.getSkin());

        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(isBuyMode) {
                    if (screen.getShopManager().buyItem(item)) {
                        refreshDisplay();
                    }
                } else {
                    screen.getShopManager().sellItem(item);
                    refreshDisplay();
                }
            }
        });

        buyBtn.getLabel().setFontScale(0.8f);
        slot.add(buyBtn).bottom().padBottom(5).size(100, 30);

        return slot;
    }

    /** Switches UI state between Buy mode and Sell mode. */
    public void setBuyMode(boolean mode) {
        isBuyMode = mode;
        refreshDisplay();
        if(!isBuyMode) modeOptions.refresh(true);
        else modeOptions.refresh(false);
    }

    public boolean isSellMode() { return !isBuyMode;}
}
