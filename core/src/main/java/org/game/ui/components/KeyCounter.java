package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.game.coreGame.GameManager;
import org.game.managers.TextureManager;


public class KeyCounter extends Table {
    private final GameManager game;
    private final Label keyLabel;

    public KeyCounter(GameManager game) {
        this.game = game;

        Image keyIcon = new Image(TextureManager.KEY);
        keyLabel = new Label(String.valueOf(game.getKeyCount()), game.getSkin());
        keyLabel.setFontScale(1.1f);
        keyLabel.setAlignment(Align.left);

        this.add(keyIcon).size(32, 32).padTop(2);
        this.add(keyLabel).width(115).padRight(5);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        keyLabel.setText(String.valueOf(game.getKeyCount()));
    }
}
