package org.game.ui.wholeUI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.game.coreGame.GameManager;
import org.game.systems.GlobalUpgradesRegistry;
import org.game.ui.components.GlobalUpgradesUIButtons;
import org.game.systems.GlobalUpgradeData;
import org.game.ui.components.KnowledgeOrbCount;

/**
 * The UI Stage for permanent global upgrades (meta-progression).
 * Displays a scrollable list of upgrades that players can purchase using
 * Knowledge Orbs earned during their runs.
 */
public class GlobalUpgradesUI extends Stage {
    private final Table upgradeListTable;
    private final ScrollPane scrollPane;
    private final GameManager game;
    private final KnowledgeOrbCount knowledgeOrbCount;

    public GlobalUpgradesUI(GameManager game, Viewport viewport) {
        super(viewport);
        this.game = game;

        // Main container for the entire screen
        Table root = new Table();
        root.setFillParent(true);
        this.addActor(root);

        // UI component to show how many Knowledge Orbs the player has
        knowledgeOrbCount = new KnowledgeOrbCount(game);

        // This table will contain all the rows of upgrades
        upgradeListTable = new Table();
        upgradeListTable.top();

        // Wrap the list in a ScrollPane for navigation if there are many upgrades
        scrollPane = new ScrollPane(upgradeListTable, game.getSkin());
        scrollPane.setFadeScrollBars(false);

        // Window-like table to hold the ScrollPane with specific dimensions
        Table window = new Table();
        float w = viewport.getWorldWidth() * 0.95f;
        float h = viewport.getWorldHeight() * 0.77f;
        window.add(scrollPane).size(w, h);

        // Header section with currency and title
        Table top = new Table();
        Label title = new Label("PERMANENT UPGRADES", game.getSkin());
        title.setFontScale(1.5f);

        top.add(knowledgeOrbCount).left().padRight(20).padTop(10);
        top.add(title).center().padTop(10);

        // Assemble the root layout
        root.add(top).top().padBottom(20).padTop(10).row();
        root.add(window).expand().top().row();

        // Bottom navigation buttons (Back, Play, etc.)
        GlobalUpgradesUIButtons modeOptions = new GlobalUpgradesUIButtons(game, this);
        root.add(modeOptions).padBottom(23).padTop(15);

        refreshDisplay();
    }

    /**
     * Rebuilds the upgrade list from scratch.
     * Usually called upon opening the screen or after a successful purchase.
     */
    public void refreshDisplay() {
        float currentScrollY = scrollPane.getScrollY();
        upgradeListTable.clearChildren();

        // Iterate through all upgrades defined in the static Registry
        for (int i = 0; i < GlobalUpgradesRegistry.DATA.size; i++) {
            GlobalUpgradeData data = GlobalUpgradesRegistry.DATA.get(i);
            upgradeListTable.add(createUpgradeRow(data)).fillX().expandX().pad(10).row();
        }

        // Force UI recalculation and restore scroll position
        upgradeListTable.invalidateHierarchy();
        scrollPane.layout();
        scrollPane.setScrollY(currentScrollY);
        scrollPane.updateVisualScroll();
    }

    /**
     * Creates a single horizontal row for an upgrade.
     * Contains the name, level, description, current stat value, and the buy button.
     */
    private Table createUpgradeRow(final GlobalUpgradeData data) {
        Table row = new Table(game.getSkin());

        // Get current progress from the upgrade manager
        int currentLevel = game.getUpgrades().getLevel(data.id);
        int cost = game.getUpgrades().getUpgradeCost(data.id);

        // Format level text (Check if maxed out)
        String lvl = currentLevel == data.maxLevel ?
            " [Lvl " + currentLevel + " (MAXED)]" : " [Lvl " + currentLevel + "]";

        Label nameLabel = new Label(data.name + lvl, game.getSkin());
        nameLabel.setColor(1, 0.8f, 0.2f, 1); // Gold-ish color for titles

        Label infoLabel = new Label(data.description, game.getSkin());
        infoLabel.setWrap(true);
        infoLabel.setAlignment(Align.left);

        // Displays the current effect value (e.g. "+10% Gold")
        Label currentAmountLabel = new Label(game.getUpgrades().getCurrentAmount(data.id), game.getSkin());
        currentAmountLabel.setAlignment(Align.right);

        // Purchase button showing cost
        TextButton buyBtn = new TextButton("UPGRADE\n" + cost + " Orbs", game.getSkin());

        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Check if player can afford the upgrade before processing
                if (game.getKnowledgeOrbs() >= cost) {
                    game.getUpgrades().purchaseUpgrade(data.id, cost);
                    refreshDisplay();
                }
            }
        });

        // Layout the text info (Title above Description)
        Table textStack = new Table();
        textStack.add(nameLabel).left().row();
        textStack.add(infoLabel).width(450).left().padTop(5);

        // Assemble the row: [Info] [Current Value] [Buy Button]
        row.add(textStack).expandX().left().pad(15);
        row.add(currentAmountLabel).right().padRight(20).width(200);

        // Hide button if the upgrade is at its maximum level
        if (currentLevel < data.maxLevel) {
            row.add(buyBtn).right().pad(15).size(140, 50);
        }

        return row;
    }
}
