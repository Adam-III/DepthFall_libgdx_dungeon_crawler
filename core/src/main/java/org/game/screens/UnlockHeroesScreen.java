package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import org.game.coreGame.GameManager;
import org.game.managers.TextureManager;
import org.game.ui.wholeUI.UnlockHeroesUI;

public class UnlockHeroesScreen extends BaseScreen{
    private final GameManager game;
    private final Texture bgTexture;
    private final UnlockHeroesUI ui;

    public UnlockHeroesScreen(GameManager game){
        super(game);
        this.game = game;
        bgTexture = TextureManager.INVENTORY_MENU;
        this.ui = new UnlockHeroesUI(game, game.viewport);
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
    public void onFadeOutComplete() {

    }

    @Override
    public void dispose() {
        ui.dispose();
        super.dispose();
    }
}
