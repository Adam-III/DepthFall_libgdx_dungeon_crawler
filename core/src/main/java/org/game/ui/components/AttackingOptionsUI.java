package org.game.ui.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.game.entities.abilities.AbilityStrategy;
import org.game.entities.base.Character;
import org.game.managers.BattleManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the combat action buttons (Attack, Block, Abilities) shown during the player's turn.
 * It dynamically updates based on the active unit's available moves and supports
 * both mouse clicks and keyboard hotkeys.
 *
 * Icon textures are loaded once and cached, so showing the menu every turn
 * does not allocate new GPU memory. Call {@link #dispose()} when the battle ends.
 */
public class AttackingOptionsUI {
    private final Table attackIcon, shieldIcon, abilityIcon;
    private final Map<String, Texture> textureCache = new HashMap<>();

    public AttackingOptionsUI() {
        // Tables act as containers for our action buttons
        attackIcon = new Table();
        shieldIcon = new Table();
        abilityIcon = new Table();

        // Hide by default until a unit's turn starts
        attackIcon.setVisible(false);
        shieldIcon.setVisible(false);
        abilityIcon.setVisible(false);
    }

    /** Returns a cached texture, loading it from disk only the first time. */
    private Texture getTexture(String path) {
        Texture tex = textureCache.get(path);
        if (tex == null) {
            tex = new Texture(path);
            textureCache.put(path, tex);
        }
        return tex;
    }

    /**
     * Populates and shows the action buttons for the currently active unit.
     * @param activeUnit The character whose turn it is.
     * @param manager The battle manager handling the combat logic.
     * @param onActionSelected Callback to trigger once a choice is made.
     */
    public void showActions(final Character activeUnit, final BattleManager manager, final Runnable onActionSelected) {
        // Clear previous unit's icons
        attackIcon.clear();
        shieldIcon.clear();
        abilityIcon.clear();

        final ImageButton attackBtn = new ImageButton(new TextureRegionDrawable(getTexture(activeUnit.getAttackIcon())));
        final ImageButton blockBtn = new ImageButton(new TextureRegionDrawable(getTexture("ui/battle/block.png")));

        // --- Action Wrappers ---
        final Runnable attackAction = () -> {
            if (attackIcon.isVisible()) {
                manager.onAttackButtonClicked();
                onActionSelected.run();
            }
        };

        final Runnable blockAction = () -> {
            if (shieldIcon.isVisible()) {
                manager.onBlockButtonClicked();
                onActionSelected.run();
            }
        };

        // --- Ability Buttons Generation ---
        for (int i = 0; i < activeUnit.getAbilities().size(); i++) {
            final int index = i;
            AbilityStrategy ability = activeUnit.getAbilities().get(i);

            final Runnable abilityAction = () -> {
                if (abilityIcon.isVisible()) {
                    manager.onAbilitySelected(index);
                    onActionSelected.run();
                }
            };

            // Ability icon is looked up by the ability's ID
            ImageButton abilityBtn = new ImageButton(new TextureRegionDrawable(getTexture("ui/battle/" + ability.getId() + ".png")));
            abilityBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    abilityAction.run();
                }
            });

            // Map "Q" key to the unit's ability (keyboard focus sits on the attack button)
            attackBtn.addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == Input.Keys.Q) {
                        abilityAction.run();
                        return true;
                    }
                    return false;
                }
            });

            abilityIcon.add(abilityBtn).size(64).pad(5);
        }

        // --- Standard Input Listeners ---
        attackBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                attackAction.run();
            }
        });

        blockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blockAction.run();
            }
        });

        // Hotkeys: E = Attack, W = Block
        attackBtn.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.E) {
                    attackAction.run();
                    return true;
                }
                if (keycode == Input.Keys.W) {
                    blockAction.run();
                    return true;
                }
                return false;
            }
        });

        // Setup layouts
        attackIcon.add(attackBtn).size(64).pad(5);
        shieldIcon.add(blockBtn).size(64).pad(5);

        attackIcon.setVisible(true);
        shieldIcon.setVisible(true);
        abilityIcon.setVisible(true);

        // Focus the button so keyboard input is captured immediately
        if (attackBtn.getStage() != null) {
            attackBtn.getStage().setKeyboardFocus(attackBtn);
        }
    }

    /** Hides the action UI, usually called after a unit has finished its turn. */
    public void hideAttackOptions() {
        attackIcon.setVisible(false);
        shieldIcon.setVisible(false);
        abilityIcon.setVisible(false);
    }

    /** Frees every icon texture loaded during this battle. */
    public void dispose() {
        for (Texture tex : textureCache.values()) tex.dispose();
        textureCache.clear();
    }

    // Getters for the BattleUI to add these tables to the main stage
    public Table getAttackIcon() { return attackIcon; }
    public Table getShieldIcon() { return shieldIcon; }
    public Table getAbilityIcon() { return abilityIcon; }
}
