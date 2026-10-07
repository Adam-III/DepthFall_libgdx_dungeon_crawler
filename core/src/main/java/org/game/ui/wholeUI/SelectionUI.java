package org.game.ui.wholeUI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.game.coreGame.GameManager;
import org.game.entities.types.AttackType;
import org.game.entities.base.Character;
import org.game.entities.factories.FriendlyFactory;
import org.game.managers.GlobalUpgradesManager;
import org.game.managers.PartyManager;
import org.game.ui.components.GoldCount;
import org.game.ui.components.InventoryModeOptions;
import org.game.ui.components.StartingFloorSetters;

/**
 * UI Stage for the Hero Selection screen.
 * Allows players to hire new heroes using permanent gold.
 * It checks for party capacity limits and unit type slots (Melee vs Ranged).
 */
public class SelectionUI extends Stage {
    private final Table itemGrid;
    private final ScrollPane scrollPane;
    private final GameManager game;
    private InventoryModeOptions modeOptions;
    private StartingFloorSetters startFloor;
    private final GoldCount goldCount;
    private final PartyManager party;
    private final FriendlyFactory factory;
    private final GlobalUpgradesManager globalUpgrades;

    public SelectionUI(GameManager game, FitViewport viewport, PartyManager party) {
        super(viewport);
        this.game = game;
        this.party = party;
        this.factory = game.getFriendlyFactory();
        this.globalUpgrades = game.getUpgrades();

        // Main layout container
        Table root = new Table();
        root.setFillParent(true);
        this.addActor(root);

        // Reusable components
        goldCount = new GoldCount(game);
        modeOptions = new InventoryModeOptions(game, this);
        startFloor = new StartingFloorSetters(game);

        // Grid for hero cards
        itemGrid = new Table();
        itemGrid.top().left();

        // ScrollPane for navigating through many unlocked heroes
        scrollPane = new ScrollPane(itemGrid, game.getSkin());
        scrollPane.setFadeScrollBars(false);

        // Window container with specific alignment offsets
        Table window = new Table();
        float w = viewport.getWorldWidth() * 0.7f;
        float h = viewport.getWorldHeight() * 0.77f;
        window.add(scrollPane).size(w, h).padLeft(280).padBottom(10);

        // Header with Gold and Title
        Table top = new Table();
        Label title = new Label("Select Party", game.getSkin());
        title.setFontScale(1.5f);

        top.add(startFloor).padTop(15).left().width(200).expand();
        top.add(title).center().width(300).padTop(15).padRight(50).padLeft(50);
        top.add(goldCount).padTop(5).row();


        // Assembly of the root table
        root.add(top).top().padBottom(15).padTop(10).row();
        root.add(window).expand().center().row();
        root.add(modeOptions).bottom().center().padBottom(37).padTop(13).size(50, 20);

        refreshDisplay();
    }

    /**
     * Clears the grid and populates it with heroes currently unlocked in the meta-progression.
     */
    public void refreshDisplay() {
        float currentScrollY = scrollPane.getScrollY();
        itemGrid.clearChildren();

        int columnCount = 0;
        int maxColumns = 3;

        // Retrieve heroes that have been permanently unlocked by the player
        Array<Character> list = game.getPartyManager().getGlobalUnlockedHeroes();

        for (final Character c : list) {
            Table slot = createItemSlot(c);
            itemGrid.add(slot).expand().align(Align.top).pad(20);

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

    /**
     * Creates a detailed card for a hero, showing stats and a "Hire" button.
     */
    private Table createItemSlot(final Character c) {
        Table slot = new Table();

        Label name = new Label(c.getName(), game.getSkin());
        Image img = new Image(c.getTexture());

        slot.add(name).top().center().row();
        slot.add(img).size(124, 124).row();

        // Detailed stat readout for the player to compare heroes
        String info = "Damage: " + c.getAttack() +
            "\nAttack Speed: " + c.getAttackSpeed() +
            "\nAttack Type: " + c.getAttackType() +
            "\nHP: " + c.getMaxHP() +
            "\nDefense: " + (int)(c.getDefense() * 100) + "%" +
            "\nCritical Chance: " + (int)(c.getCriticalChance() * 100) + "%";

        Label label = new Label(info, game.getSkin());
        label.setAlignment(Align.center);
        label.setFontScale(0.85f);
        slot.add(label).padBottom(5).row();

        // Hire Button logic
        String price = "Hire for " + c.getPrice() + " G";
        TextButton buyBtn = new TextButton(price, game.getSkin());

        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 1. Check if the player has enough gold
                if (game.getPermanentGold() >= c.getPrice()) {

                    // 2. Determine slot availability based on AttackType (Melee vs Ranged)
                    // and check the global unit count limit.
                    boolean canAdd = false;

                    if (c.getAttackType() == AttackType.MELEE) {
                        if (!party.isMeleeFull() && party.getCurrentUnits() < globalUpgrades.getMaxUnitsCount()) {
                            party.addMelee(factory.createNewCharacterByName(c.getName()));
                            canAdd = true;
                        }
                    } else {
                        if (!party.isRangedFull() && party.getCurrentUnits() < globalUpgrades.getMaxUnitsCount()) {
                            party.addRanged(factory.createNewCharacterByName(c.getName()));
                            canAdd = true;
                        }
                    }

                    // 3. Deduct gold if hiring was successful
                    if (canAdd) {
                        game.addGold(-c.getPrice());
                    }
                }
            }
        });

        slot.add(buyBtn).size(100, 20);

        return slot;
    }
}
