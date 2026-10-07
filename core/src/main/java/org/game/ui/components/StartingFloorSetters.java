package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;

public class StartingFloorSetters extends Table {
    private final GameManager game;
    private Label floor;

    public StartingFloorSetters(GameManager game){
        this.game = game;

        String text = "Floor: " + game.getStartingFloor();
        floor = new Label(text, game.getSkin());
        floor.setFontScale(1.5f);

        TextButton decreaseFloorBtn = new TextButton("-5", game.getSkin());
        decreaseFloorBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setStartingFloor(-5);
                refresh();
            }
        });
        TextButton increaseFloorBtn = new TextButton("+5", game.getSkin());
        increaseFloorBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setStartingFloor(5);
                refresh();
            }
        });

        this.add(floor).pad(5);
        this.add(decreaseFloorBtn).pad(5).width(30).height(20);
        this.add(increaseFloorBtn).pad(5).width(30).height(20);
    }

    private void refresh(){
        String text = "Floor: " + game.getStartingFloor();
        floor.setText(text);
        floor.setFontScale(1.5f);
    }
}
