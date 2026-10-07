package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.game.coreGame.GameManager;
import org.game.managers.TextureManager;


public class KnowledgeOrbCount extends Table {
    private final GameManager game;
    private final Label knowledgeOrbLabel;

    public KnowledgeOrbCount(GameManager game) {
        this.game = game;

        Image knowledgeOrbIcon = new Image(TextureManager.KNOWLEDGE_ORB);
        knowledgeOrbLabel = new Label(String.valueOf(game.getKnowledgeOrbs()), game.getSkin());
        knowledgeOrbLabel.setFontScale(1.1f);
        knowledgeOrbLabel.setAlignment(Align.left);

        this.add(knowledgeOrbIcon).size(32, 32).padTop(2);
        this.add(knowledgeOrbLabel).width(115).padRight(5);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        knowledgeOrbLabel.setText(String.valueOf(game.getKnowledgeOrbs()));
    }
}
