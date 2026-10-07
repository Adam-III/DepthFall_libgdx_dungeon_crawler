package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.entities.base.Character;
import org.game.managers.BattleManager;


public class BattleQueue extends Table {
    private final GameManager game;
    private final Label que;
    private final BattleManager manager;
    private Array<Character> queList;
    private StringBuilder turnQueue;

    public BattleQueue(GameManager game, BattleManager manager) {
        this.game = game;
        this.manager = manager;
        queList = manager.getTurnQueue();
        turnQueue = new StringBuilder();
        turnQueue.append("Battle Queue: \n");
        for(Character c : queList){
            turnQueue.append(c.getName()).append("\n");
        }

        que = new Label(turnQueue.toString(), game.getSkin());
        que.getStyle().font.getData().markupEnabled = true;
        que.setFontScale(0.8f);
        que.setAlignment(Align.left);

        this.add(que).width(115).padTop(6).padRight(2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        queList = manager.getTurnQueue();
        turnQueue.setLength(0);
        turnQueue.append("Battle Queue: \n");

        for (Character c : queList) {
            if (!c.isFriendly()) {
                turnQueue.append("  [RED]").append(c.getName()).append("[]\n");
            } else {
                turnQueue.append("  [GREEN]").append(c.getName()).append("[]\n");
            }
        }
        que.setText(turnQueue);
    }
}
