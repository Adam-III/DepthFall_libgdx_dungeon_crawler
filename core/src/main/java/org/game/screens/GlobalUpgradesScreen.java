package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import org.game.coreGame.GameManager;
import org.game.managers.GlobalUpgradesManager;
import org.game.managers.TextureManager;
import org.game.ui.wholeUI.GlobalUpgradesUI;

public class GlobalUpgradesScreen extends BaseScreen{
    private GlobalUpgradesUI ui;
    private GlobalUpgradesManager upgradeManager;
    private Texture bgTexture = TextureManager.INVENTORY_MENU;

    public GlobalUpgradesScreen(GameManager game) {
        super(game);
        this.upgradeManager = game.getUpgrades();
        this.ui = new GlobalUpgradesUI(game, game.viewport);
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(ui);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.spriteBatch.begin();
        game.spriteBatch.draw(bgTexture,0, 0, 960, 540);
        game.spriteBatch.end();

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

        if (ui != null) {
            ui.getViewport().update(width, height, true);
        }
    }

    @Override
    public void onFadeOutComplete() {

    }
}
