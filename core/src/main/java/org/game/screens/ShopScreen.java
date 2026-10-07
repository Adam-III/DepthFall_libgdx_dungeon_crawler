package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import org.game.coreGame.GameManager;
import org.game.coreGame.GameState;
import org.game.managers.ShopManager;
import org.game.managers.TextureManager;
import org.game.ui.wholeUI.ShopUI;

public class ShopScreen extends BaseScreen {
    private ShopUI ui;
    private ShopManager shopManager;
    private Texture bgTexture = TextureManager.INVENTORY_MENU;

    public ShopScreen(GameManager game, ShopManager shopManager) {
        super(game);
        this.shopManager = shopManager;
        this.ui = new ShopUI(game.viewport, game, this);
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

    public ShopManager getShopManager() { return shopManager; }

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
