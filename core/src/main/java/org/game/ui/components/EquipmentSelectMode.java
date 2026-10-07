package org.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.game.coreGame.GameManager;
import org.game.items.base.EquipmentMode;
import org.game.managers.EquipmentManager;
import org.game.ui.wholeUI.EquipmentUI;
import org.game.ui.wholeUI.SelectionUI;


public class EquipmentSelectMode extends Table{
    private final GameManager game;
    private final EquipmentManager manager;
    private final EquipmentUI ui;

    public EquipmentSelectMode(GameManager game, EquipmentUI ui, EquipmentManager manager){
        this.game = game;
        this.manager = manager;
        this.ui = ui;


        TextButton shieldBtn = new TextButton("SHIELDS", game.getSkin());
        shieldBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.updateState("SHIELD");
                ui.refreshDisplay();
            }
        });
        TextButton weaponBtn = new TextButton("WEAPON", game.getSkin());
        weaponBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.updateState("WEAPON");
                ui.refreshDisplay();
            }
        });
        TextButton armorBtn = new TextButton("ARMOR", game.getSkin());
        armorBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.updateState("ARMOR");
                ui.refreshDisplay();
            }
        });


        this.add(weaponBtn).left().pad(5).padLeft(50).size(80,20);
        this.add(shieldBtn).left().pad(5).size(70,20);
        this.add(armorBtn).left().pad(5).size(70,20);


    }
}
