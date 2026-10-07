package org.game.ui.wholeUI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.game.coreGame.GameManager;
import org.game.entities.base.Character;
import org.game.ui.components.KnowledgeOrbCount;
import org.game.ui.components.UnlockHeroesUIButtons;

public class UnlockHeroesUI extends Stage {
    private final Table heroGrid;
    private final ScrollPane scrollPane;
    private final GameManager game;
    private final UnlockHeroesUIButtons modeOptions;
    private final KnowledgeOrbCount knowledgeOrbCount;

    public UnlockHeroesUI(GameManager game, Viewport viewport){
        super(viewport);
        this.game = game;
        Table root = new Table();
        root.setFillParent(true);
        this.addActor(root);

        knowledgeOrbCount = new KnowledgeOrbCount(game);
        heroGrid = new Table();
        heroGrid.top().left();

        scrollPane = new ScrollPane(heroGrid, game.getSkin());
        scrollPane.setFadeScrollBars(false);

        Table window = new Table();
        float w = viewport.getWorldWidth() * 0.9f;
        float h = viewport.getWorldHeight() * 0.78f;
        window.add(scrollPane).size(w, h);

        Table top = new Table();

        Label title;


        modeOptions = new UnlockHeroesUIButtons(game, this);
        title = new Label( "Unlock Heroes", game.getSkin());
        title.setFontScale(1.5f);

        top.add(knowledgeOrbCount).left().top().padTop(15);
        top.add(title).padTop(20);
        root.add(top).top().padBottom(15).padTop(10).row();
        root.add(window).expand().center().row();
        root.add(modeOptions).bottom().center().padBottom(30).padTop(15);


        refreshDisplay();
    }

    public void refreshDisplay() {
        float currentScrollY = scrollPane.getScrollY();

        heroGrid.clearChildren();

        int columnCount = 0;
        int maxColumns = 7;

        Array<Character> list = game.getPartyManager().getUnlockableHeroes();

        for (final Character c : list) {
            Table slot = createItemSlot(c);
            heroGrid.add(slot).expand().align(Align.top).pad(20);

            columnCount++;
            if (columnCount >= maxColumns) {
                heroGrid.row();
                columnCount = 0;
            }
        }

        heroGrid.invalidateHierarchy();
        scrollPane.layout();

        scrollPane.setScrollY(currentScrollY);
        scrollPane.updateVisualScroll();
    }

    private Table createItemSlot(final Character c) {
        Table slot = new Table();

        Label name = new Label(c.getName(), game.getSkin());
        Image img = new Image((c).getTexture());

        slot.add(name).top().center().row();
        slot.add(img).size(124, 124).row();

        String info = "Damage: " + c.getAttack() + "\nAttack Speed: " + c.getAttackSpeed() +
            "\nAttack Type: " + c.getAttackType() + "\nHP: " + c.getMaxHP() +
            "\nDefense: " + Math.round(c.getDefense() * 100) + "%" +
            "\nCritical Chance: " + Math.round(c.getCriticalChance() * 100) + "%";

        Label label = new Label(info, game.getSkin());
        label.setAlignment(Align.center);
        label.setFontScale(0.85f);
        slot.add(label).padBottom(5).row();



        String price = "Unlock for " + c.getUnlockPrice() + "\nKnowledge Orbs";
        TextButton buyBtn = new TextButton( price, game.getSkin());
        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.getKnowledgeOrbs() >= c.getUnlockPrice()) {
                    game.addKnowledgeOrbs(-c.getUnlockPrice());
                    game.getPartyManager().unlockHero(c);
                    refreshDisplay();
                }
            }
        });

        slot.add(buyBtn).size(120, 50);

        return slot;
    }
}
