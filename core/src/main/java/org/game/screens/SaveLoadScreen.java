package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;
import org.game.coreGame.GameState;
import org.game.managers.SaveManager;
import org.game.managers.TextureManager;
import org.game.saves.SaveData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Save / Load menu screen.
 *
 * Layout:
 *   - Title row.
 *   - One row per save slot showing its current contents (or "Empty"),
 *     plus three actions: Save, Load, Delete.
 *   - Status line for the most recent action.
 *   - Back-to-menu button.
 *
 * SaveManager is the only object that touches disk. This screen is just UI glue.
 */
public class SaveLoadScreen extends BaseScreen {

    private final Skin skin;
    private final Stage stage;
    private final SaveManager saveManager;
    private final Texture bgTexture;
    private boolean loaded = false;
    private final SimpleDateFormat dateFmt =
        new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT);

    private final Label[] slotLabels = new Label[SaveManager.SLOT_COUNT];
    private Label statusLabel;

    public SaveLoadScreen(final GameManager game) {
        super(game);
        this.skin = game.getSkin();
        this.saveManager = game.getSaveManager();
        bgTexture = TextureManager.INVENTORY_MENU;
        this.stage = new Stage(game.viewport, game.spriteBatch);

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(10);
        stage.addActor(root);

        // ---- Title ----
        Label title = new Label("SAVE / LOAD", skin);
        title.setColor(Color.WHITE);
        title.setFontScale(2);
        root.add(title).colspan(4).padBottom(40).row();

        // ---- One row per save slot ----
        for (int i = 0; i < SaveManager.SLOT_COUNT; i++) {
            final int slot = i;

            Label info = new Label(describe(slot), skin);
            slotLabels[i] = info;

            TextButton saveBtn   = new TextButton("Save",   skin);
            TextButton loadBtn   = new TextButton("Load",   skin);
            TextButton deleteBtn = new TextButton("Delete", skin);

            saveBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    String label = "Floor " + game.getFloorReached();
                    if (saveManager.save(slot, label)) {
                        setStatus("Saved to slot " + (slot + 1));
                    } else {
                        setStatus("Save FAILED for slot " + (slot + 1));
                    }
                    refresh();
                }
            });

            loadBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    if (!saveManager.hasSave(slot)) {
                        setStatus("Slot " + (slot + 1) + " is empty");
                        return;
                    }
                    if (saveManager.load(slot)) {
                        setStatus("Loaded slot " + (slot + 1));
                        loaded = true;
                        startTransition();
                    } else {
                        setStatus("Load FAILED for slot " + (slot + 1));
                    }
                }
            });

            deleteBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    if (saveManager.delete(slot)) {
                        setStatus("Slot " + (slot + 1) + " cleared");
                    } else {
                        setStatus("Could not clear slot " + (slot + 1));
                    }
                    refresh();
                }
            });

            Table saveTable = new Table();

            saveTable.add(info).left().padLeft(25).width(600);
            saveTable.add(saveBtn).right().width(80).height(40).pad(4).padLeft(30);
            saveTable.add(loadBtn).right().width(80).height(40).pad(4);
            saveTable.add(deleteBtn).right().width(80).height(40).pad(4).padRight(25);
            root.add(saveTable).padTop(50).row();

        }

        // ---- Status line ----
        statusLabel = new Label("", skin);
        root.add(statusLabel).colspan(4).padTop(20).row();

        // ---- Back ----
        TextButton back = new TextButton("Back to Menu", skin);
        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                startTransition();
            }
        });
        root.add(back).bottom().colspan(4).width(120).height(20).padTop(80).row();
    }

    /** Refresh every slot's description label after a save/delete. */
    private void refresh() {
        for (int i = 0; i < SaveManager.SLOT_COUNT; i++) {
            slotLabels[i].setText(describe(i));
        }
    }

    /** Build the human-readable summary for a slot. */
    private String describe(int slot) {
        SaveData d = saveManager.peek(slot);
        if (d == null) return "Slot " + (slot + 1) + "  -  Empty";

        String when  = dateFmt.format(new Date(d.timestamp));
        String label = (d.saveName == null || d.saveName.isEmpty())
            ? "(unnamed)" : d.saveName;
        return String.format(Locale.ROOT,
            "Slot %d  |  %s  |  Played %s   \n" +
                    "           |  Gold %d  |  Orbs %d  |  %s",
            slot + 1, label,formatPlayTime(d.playTimeSeconds),
            d.permanentGold, d.knowledgeOrbs, when);
    }

    /** "1h 23m 45s" for long sessions, "23m 45s" for short ones. */
    private String formatPlayTime(float seconds) {
        int s = (int) seconds;
        int hours   = s / 3600;
        int minutes = (s % 3600) / 60;
        int sec     = s % 60;
        if (hours > 0) return String.format(Locale.ROOT, "%dh %02dm %02ds", hours, minutes, sec);
        return String.format(Locale.ROOT, "%dm %02ds", minutes, sec);
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.10f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);


        game.spriteBatch.begin();
        game.spriteBatch.draw(bgTexture,0, 0, 960, 540);
        game.spriteBatch.end();

        stage.act(delta);
        if (disposed) return; // A button may have switched (and disposed) this screen
        stage.draw();

        updateTransition(delta, 2.5f);
        if (disposed) return;

        game.spriteBatch.begin();
        drawTransition();
        game.spriteBatch.end();
    }

    @Override
    public void onFadeOutComplete() {
        if(loaded) game.updateState(GameState.PARTY_SELECTION);
        else game.updateState(GameState.START_MENU);
    }



    @Override
    public void dispose() {
        stage.dispose();
        super.dispose();
    }
}
