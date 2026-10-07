package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;
import org.game.managers.EquipmentManager;
import org.game.ui.wholeUI.EquipmentUI;


public class EquipmentCharacterMode extends Table{
    private final GameManager game;
    private final Stage ui;
    private final EquipmentManager manager;

    public EquipmentCharacterMode(GameManager game, EquipmentUI ui, EquipmentManager manager){
        this.game = game;
        this.manager = manager;
        this.ui = ui;

        TextButton ms1Btn = new TextButton("1", game.getSkin());
        ms1Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.setSlot(0);
                ui.refreshDisplay();
            }
        });
        TextButton ms2Btn = new TextButton("2", game.getSkin());
        ms2Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.setSlot(1);
                ui.refreshDisplay();
            }
        });
        TextButton ms3Btn = new TextButton("3", game.getSkin());
        ms3Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.setSlot(2);
                ui.refreshDisplay();
            }
        });
        TextButton rs1Btn = new TextButton("4", game.getSkin());
        rs1Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.setSlot(3);
                ui.refreshDisplay();
            }
        });
        TextButton rs2Btn = new TextButton("5", game.getSkin());
        rs2Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.setSlot(4);
                ui.refreshDisplay();
            }
        });
        TextButton rs3Btn = new TextButton("6", game.getSkin());
        rs3Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.setSlot(5);
                ui.refreshDisplay();
            }
        });


        this.add(ms1Btn).left().pad(5).size(20,20);
        this.add(ms2Btn).left().pad(5).size(20,20);
        this.add(ms3Btn).left().pad(5).size(20,20);
        this.add(rs1Btn).left().pad(5).size(20,20);
        this.add(rs2Btn).left().pad(5).size(20,20);
        this.add(rs3Btn).left().pad(5).padRight(50).size(20,20);

    }
}
