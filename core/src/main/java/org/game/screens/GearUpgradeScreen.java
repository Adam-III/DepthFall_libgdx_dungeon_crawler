package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import org.game.coreGame.GameManager;
import org.game.managers.TextureManager;
import org.game.managers.GearUpgradeManager;
import org.game.ui.wholeUI.GearUpgradeUI;

/**
 * Screen dedicated to upgrading weapons, armor, and shields.
 * It provides an interface for the player to spend resources (like Knowledge Orbs)
 * to improve the base statistics of their items.
 */
public class GearUpgradeScreen extends BaseScreen {
    private GearUpgradeUI ui;
    private GearUpgradeManager upgradeManager;
    private Texture bgTexture = TextureManager.INVENTORY_MENU;

    public GearUpgradeScreen(GameManager game) {
        super(game);
        // Logical manager that handles the math behind item upgrades
        this.upgradeManager = new GearUpgradeManager(game);
        // Stage-based UI containing the item lists and upgrade buttons
        this.ui = new GearUpgradeUI(game.viewport, game, this, upgradeManager);
    }

    @Override
    public void show() {
        // Redirect input to the UI stage to allow clicking on items/buttons
        Gdx.input.setInputProcessor(ui);
    }

    @Override
    public void render(float delta) {
        // Standard clear and projection setup
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        // 1. Draw Background
        game.spriteBatch.begin();
        game.spriteBatch.draw(bgTexture, 0, 0, 960, 540);
        game.spriteBatch.end();

        // 2. Draw UI components (Item lists, upgrade costs, stats)
        ui.act(delta);
        if (disposed) return; // A button may have switched (and disposed) this screen
        ui.draw();
    }

    @Override
    public void dispose() {
        ui.dispose();
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        // Ensure UI scaling remains consistent when the game window is resized
        if (ui != null) {
            ui.getViewport().update(width, height, true);
        }
    }

    @Override
    public void onFadeOutComplete() {
        // Currently no post-fade logic required for this screen
    }
}
