package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;
import org.game.coreGame.GameState;
import org.game.ui.wholeUI.UnlockHeroesUI;

public class UnlockHeroesUIButtons extends Table {
    private final GameManager game;
    private final UnlockHeroesUI ui;

    public UnlockHeroesUIButtons(GameManager game, UnlockHeroesUI ui){
        this.game = game;
        this.ui = ui;

        TextButton shopButton = new TextButton("SHOP", game.getSkin());
        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.SHOP);
            }
        });

        TextButton playBtn = new TextButton("PLAY", game.getSkin());
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setDungeonDifficulty();
                game.updateState(GameState.DUNGEON);
            }
        });

        TextButton inventoryBtn = new TextButton("INVENTORY", game.getSkin());
        inventoryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.INVENTORY);
            }
        });

        TextButton selectionBtn = new TextButton("SELECT PARTY", game.getSkin());
        selectionBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.PARTY_SELECTION);
            }
        });

        this.add(playBtn).left().pad(5).padRight(50).size(50,20);
        this.add(selectionBtn).left().pad(5).size(120,20);
        this.add(inventoryBtn).left().pad(5).size(100,20);
        this.add(shopButton).left().pad(5).size(50,20);
    }
}
