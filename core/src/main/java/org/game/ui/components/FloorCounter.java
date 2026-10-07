package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.game.coreGame.GameManager;

public class FloorCounter extends Table{
    private final GameManager game;
    private Label floorCount;

    public FloorCounter(GameManager game){
        this.game = game;

        String gold = ("Floor lvl: " + game.dungeonDifficulty.getDifficulty());
        floorCount = new Label(gold, game.getSkin());
        floorCount.setFontScale(1.1f);

        floorCount.setAlignment(Align.left);

        this.add(floorCount).width(115).padTop(6).padRight(5);
    }
}
