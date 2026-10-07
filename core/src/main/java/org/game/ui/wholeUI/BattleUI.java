package org.game.ui.wholeUI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.game.coreGame.GameManager;
import org.game.entities.base.Character;
import org.game.managers.BattleManager;
import org.game.ui.components.*;

/**
 * The main UI Stage for combat encounters.
 * Manages the display of battle-specific controls (Attack/Block/Ability),
 * the turn order queue, and player resources.
 */
public class BattleUI {
    private Stage stage;
    private AttackingOptionsUI attackingOptions;
    private GoldCount goldCount;
    private KnowledgeOrbCount knowledgeOrbCount;
    private KeyCounter keyCount;
    private GameManager game;
    private Label attackLabel, blockLabel, abilityLabel;
    private Table actionOptions, abilityGroup;
    private FloorCounter floorCounter;
    private BattleQueue turnQue;

    public BattleUI(GameManager game, BattleManager manager) {
        this.game = game;
        this.stage = new Stage(game.viewport, game.spriteBatch);

        // --- Component Initialization ---
        this.attackingOptions = new AttackingOptionsUI();
        this.goldCount = new GoldCount(game);
        this.knowledgeOrbCount = new KnowledgeOrbCount(game);
        this.keyCount = new KeyCounter(game);
        this.turnQue = new BattleQueue(game, manager); // Shows the order of units
        this.floorCounter = new FloorCounter(game);

        // Labels for keyboard shortcut hints
        attackLabel = new Label("Attack: E", game.getSkin());
        attackLabel.setFontScale(1f);
        blockLabel = new Label("Block: W", game.getSkin());
        blockLabel.setFontScale(1f);
        abilityLabel = new Label("Ability: Q", game.getSkin());
        abilityLabel.setFontScale(1f);

        // --- Main Layout Setup ---
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Top-right corner: Resources and Battle Queue
        root.add(floorCounter).top().right().padTop(5).row();
        root.add(goldCount).top().right().row();
        root.add(knowledgeOrbCount).top().right().row();
        root.add(keyCount).top().right().row();
        root.add(turnQue).top().padTop(10).right().padRight(20);
        root.row();

        // --- Action Groups (Icons + Hotkey Labels) ---
        Table attackGroup = new Table();
        attackGroup.add(attackingOptions.getAttackIcon()).size(64, 64).row();
        attackGroup.add(attackLabel).padTop(2);

        Table shieldGroup = new Table();
        shieldGroup.add(attackingOptions.getShieldIcon()).size(64, 64).row();
        shieldGroup.add(blockLabel).padTop(2);

        abilityGroup = new Table();
        abilityGroup.add(attackingOptions.getAbilityIcon()).size(64, 64).row();
        abilityGroup.add(abilityLabel).padTop(2);
        abilityGroup.setVisible(false); // Only visible if the unit has abilities

        // Bottom-left: Main action menu container
        actionOptions = new Table();
        actionOptions.add(attackGroup).padRight(10);
        actionOptions.add(shieldGroup).padRight(10);
        actionOptions.add(abilityGroup).padRight(10);

        root.add(actionOptions).expand().left().bottom().pad(20);
        actionOptions.setVisible(false); // Hidden until it's the player's turn
    }

    /**
     * Activates and displays the action menu for the currently active character.
     * @param activeUnit The character whose turn it is.
     * @param manager The manager handling battle logic.
     */
    public void showActionMenu(Character activeUnit, BattleManager manager) {
        actionOptions.setVisible(true);

        // Show ability button only if the unit actually has skills to use
        abilityGroup.setVisible(!activeUnit.getAbilities().isEmpty());

        attackingOptions.showActions(activeUnit, manager, new Runnable() {
            @Override
            public void run() {
                // Auto-hide options once an action is selected
                hideAttackOptions();
            }
        });
    }

    /** Updates and renders the Scene2D stage. */
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    /** Hides the combat menu once an action is performed or the turn ends. */
    public void hideAttackOptions() {
        attackingOptions.hideAttackOptions();
        actionOptions.setVisible(false);
    }

    /** Frees the stage and the cached action icons. */
    public void dispose() {
        stage.dispose();
        attackingOptions.dispose();
    }
}
