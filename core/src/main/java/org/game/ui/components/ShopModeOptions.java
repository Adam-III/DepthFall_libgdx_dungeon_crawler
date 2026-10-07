package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;
import org.game.coreGame.GameState;
import org.game.managers.ShopManager;
import org.game.ui.wholeUI.ShopUI;

public class ShopModeOptions extends Table{
    private final GameManager game;
    private final ShopUI ui;
    private final ShopManager manager;
    private final TextButton sellAllBtn;

    public ShopModeOptions(GameManager game, ShopUI ui, ShopManager manager){
        this.game = game;
        this.ui = ui;
        this.manager = manager;

        TextButton buyButton = new TextButton("BUY", game.getSkin());
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ui.setBuyMode(true);
            }
        });
        TextButton sellButton = new TextButton("SELL", game.getSkin());
        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ui.setBuyMode(false);
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

        sellAllBtn = new TextButton("SELL ALL", game.getSkin());
        sellAllBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.sellAll();
                ui.refreshDisplay();
            }
        });

        this.add(playBtn).left().pad(5).padRight(50).size(50,20);
        this.add(selectionBtn).left().pad(5).size(120,20);
        this.add(inventoryBtn).left().pad(5).size(100,20);
        this.add(buyButton).left().pad(5).size(50,20);
        this.add(sellButton).left().pad(5).size(50, 20);
        sellAllBtn.setVisible(false);
        this.add(sellAllBtn).left().pad(5).size(70, 20);

    }

    public void refresh(boolean isVisible){
        sellAllBtn.setVisible(isVisible);
    }
}
