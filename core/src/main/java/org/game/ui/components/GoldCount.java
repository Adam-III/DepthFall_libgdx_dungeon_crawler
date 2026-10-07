package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.game.coreGame.GameManager;
import org.game.managers.TextureManager;

/**
 * A UI widget that displays the player's current permanent gold balance.
 * It automatically updates its display text every frame to reflect the
 * state stored in the GameManager.
 */
public class GoldCount extends Table {
    private final GameManager game;
    private final Label goldLabel;

    public GoldCount(GameManager game) {
        this.game = game;

        // --- UI Initialization ---

        // Load the visual icon for the currency
        Image coinIcon = new Image(TextureManager.COIN);

        // Setup the label with the initial gold value
        String initialGold = game.getPermanentGold() + " G";
        goldLabel = new Label(initialGold, game.getSkin());
        goldLabel.setFontScale(1.1f);
        goldLabel.setAlignment(Align.left);

        // --- Layout Assembly ---

        // Add the icon and label to this table container
        this.add(coinIcon).size(32, 32).padTop(8);
        this.add(goldLabel).width(115).padTop(6).padRight(5);
    }

    /**
     * Updates the logic of the widget.
     * Overriding act allows the component to poll the GameManager for the
     * latest gold value without needing external event listeners.
     * @param delta Time passed since the last frame.
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        // Refresh the text to show current permanent gold
        goldLabel.setText(game.getPermanentGold() + " G");
    }
}
