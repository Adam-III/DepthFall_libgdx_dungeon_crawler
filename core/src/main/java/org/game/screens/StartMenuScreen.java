package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;
import org.game.coreGame.GameState;
import org.game.entities.friendly.Swordsman;
import org.game.managers.SaveManager;

public class StartMenuScreen extends BaseScreen {
    private final Texture bgTexture;
    private final Skin skin;
    private final Stage stage;

    public StartMenuScreen(final GameManager game, SaveManager saveManager) {
        super(game);
        this.skin = game.getSkin();
        this.bgTexture = new Texture("Start_Menu.png");
        this.stage = new Stage(game.viewport, game.spriteBatch);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.top().padTop(80);

        // ---- START NEW RUN ----
        TextButton startButton = new TextButton("START NEW RUN", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startTransition();
                game.wipeSaveState();

                game.getPartyManager().addMelee(new Swordsman());
                game.getPartyManager().addMelee(new Swordsman());
                game.getPartyManager().addMelee(new Swordsman());

                game.setPlayTimeSeconds(0f);
                game.updateState(GameState.DUNGEON);
            }
        });

        // ---- SAVE / LOAD ----
        TextButton saveLoadButton = new TextButton("SAVE / LOAD", skin);
        saveLoadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.updateState(GameState.SAVE_LOAD);
            }
        });

        // Continue from autosave
        TextButton continueButton = new TextButton("CONTINUE", skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveManager.loadAutosave();
                game.updateState(GameState.PARTY_SELECTION);
            }
        });

        continueButton.setVisible(false);
        if(saveManager.hasAutosave()) continueButton.setVisible(true);

        // Layout
        table.add(startButton).width(220).height(50).padBottom(15).row();
        table.add(saveLoadButton).width(220).height(50).padBottom(100).row();
        table.add(continueButton).width(220).height(50).row();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        input();
        logic(delta);
        if (disposed) return; // A button may have switched (and disposed) this screen
        draw();
    }

    public void input() {}

    public void logic(float delta) {
        updateTransition(delta, 2.5f);
        stage.act(delta);
    }

    public void draw() {
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        game.spriteBatch.begin();
        game.spriteBatch.draw(bgTexture, 0, 0, 960, 540);
        game.spriteBatch.end();

        stage.draw();

        game.spriteBatch.begin();
        drawTransition();
        game.spriteBatch.end();
    }

    @Override
    public void onFadeOutComplete() {
        // Screen switching is handled by GameManager, which also disposes this screen.
    }

    @Override
    public void dispose() {
        if (disposed) return;
        stage.dispose();
        bgTexture.dispose();
        super.dispose();
    }
}
