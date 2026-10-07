package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import org.game.coreGame.GameManager;
import org.game.entities.base.Character;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.managers.PartyManager;
import org.game.managers.TextureManager;
import org.game.ui.wholeUI.SelectionUI;


public class SelectionScreen extends BaseScreen {
    private final GameManager game;
    private final Texture bgTexture;
    private final SelectionUI ui;
    private Character[] melee, ranged;
    private Array<Character> partyList;
    private final PartyManager party;
    private final SpriteBatch batch;

    public SelectionScreen(GameManager game, PartyManager party) {
        super(game);
        this.game = game;
        this.party = party;
        this.batch = game.spriteBatch;
        partyList = new Array<>();
        bgTexture = TextureManager.PARTY_SELECT_MENU;
        this.ui = new SelectionUI(game, game.viewport, party);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(ui);
    }

    @Override
    public void render(float delta) {
        logic();
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.spriteBatch.begin();
        game.spriteBatch.draw(bgTexture,0, 0, 960, 540);

        for (Character c : partyList){
            batch.draw(c.getTexture(), c.getX(), c.getY(), c.getWidth(), c.getHeight());

            if(c.getCurrentArmor() != null) batch.draw(c.getCurrentArmor().getTexture(), c.getX()-4 + c.getVisualOffsetX(),
                c.getY()-6 + c.getVisualOffsetY(), 64, 64);

            if(c.isFriendly()) batch.draw(TextureManager.HANDS, c.getX() + c.getVisualOffsetX(),
                c.getY() + 2 + c.getVisualOffsetY(), c.getWidth(), c.getHeight());


            if(c.getCurrentShield() != null) batch.draw(c.getCurrentShield().getTexture(), c.getX()-16 + c.getVisualOffsetX(),
                c.getY() - 10 + c.getVisualOffsetY(), 64, 64);

            FriendlyCharacter ally = null;
            if(c instanceof FriendlyCharacter)ally = (FriendlyCharacter) c;

            if(c.getCurrentWeapon() != null && ally != null && ally.getFriendlyType() == FriendlyType.ARCHER) batch.draw(c.getCurrentWeapon().getTexture(), c.getX()+24 + c.getVisualOffsetX(),
                c.getY()-2 + c.getVisualOffsetY(), 64, 64);
            else if(c.getCurrentWeapon() != null) batch.draw(c.getCurrentWeapon().getTexture(), c.getX()+22 + c.getVisualOffsetX(),
                c.getY()+10 + c.getVisualOffsetY(), 64, 64);
        }
        game.spriteBatch.end();

        ui.act(delta);
        if (disposed) return; // A button may have switched (and disposed) this screen
        ui.draw();

    }

    private void logic(){
        // Rebuilt every frame, so clear it first (otherwise the list grows forever)
        partyList.clear();
        melee = party.getMeleeSlots();
        for (int i = 0; i < melee.length; i++) {
            if (melee[i] != null) {
                float x = 156;
                float y = 346 - (i * 128);
                melee[i].setPosition(x, y);
                partyList.add(melee[i]);
            }
        }

        ranged = party.getRangedSlots();
        for (int i = 0; i < ranged.length; i++) {
            if (ranged[i] != null) {
                float x = 52;
                float y = 346 - (i * 128);
                ranged[i].setPosition(x, y);
                partyList.add(ranged[i]);
            }
        }

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
