package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import org.game.coreGame.GameManager;
import org.game.entities.base.Character;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.managers.EquipmentManager;
import org.game.managers.PartyManager;
import org.game.managers.TextureManager;
import org.game.ui.wholeUI.EquipmentUI;

/**
 * Screen responsible for managing and displaying character equipment.
 * Allows the player to select a party member and view/change their
 * current weapon, shield, and armor.
 */
public class EquipmentScreen extends BaseScreen {
    private final GameManager game;
    private final Texture bgTexture;
    private final EquipmentUI ui;

    // Arrays to store party members based on their formation
    private Character[] melee, ranged, partyList;
    private final PartyManager party;
    private final EquipmentManager equipmentManager;
    private final SpriteBatch batch;

    // UI Textures for slot placeholders and selections
    private Texture selectShield, selectSword, selectBow, selectWand, selectArmor, selectWeapon, currentSelection;

    public EquipmentScreen(GameManager game, PartyManager party) {
        super(game);
        this.game = game;
        this.party = party;
        this.batch = game.spriteBatch;
        this.partyList = new Character[6];
        this.bgTexture = TextureManager.EQUIPMENT_SELECT_MENU;

        // Logical manager for handling item swapping
        this.equipmentManager = new EquipmentManager(game, party, this);
        // Stage-based UI for buttons and item lists
        this.ui = new EquipmentUI(game, game.viewport, equipmentManager, this);

        // Initialize default placeholder icons
        this.selectShield = TextureManager.SELECT_SHIELD;
        this.selectSword = TextureManager.SELECT_SWORD;
        this.selectBow = TextureManager.SELECT_BOW;
        this.selectWand = TextureManager.SELECT_WAND;
        this.selectArmor = TextureManager.SELECT_ARMOR;
        this.selectWeapon = selectSword;
        this.currentSelection = TextureManager.CHARACTER_SELECTED;
    }

    @Override
    public void show() {
        // Set input focus to the UI stage
        Gdx.input.setInputProcessor(ui);
    }

    @Override
    public void render(float delta) {
        logic(); // Update positions and textures
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.spriteBatch.begin();
        // 1. Draw Background
        game.spriteBatch.draw(bgTexture, 0, 0, 960, 540);

        // 2. Draw Equipment Slots (Icons update dynamically based on selected character)
        game.spriteBatch.draw(selectWeapon, 64, 284, 64, 64);
        game.spriteBatch.draw(selectShield, 64, 176, 64, 64);
        game.spriteBatch.draw(selectArmor, 64, 72, 64, 64);

        // 3. Draw All Party Members with their equipment
        for (Character c : partyList){
            if(c != null) {
                drawCharacterWithEquipment(c);
            }
        }

        // 4. Highlight the currently selected character
        if(partyList[equipmentManager.getCharacterIndex()] != null){
            Character c = partyList[equipmentManager.getCharacterIndex()];
            batch.draw(currentSelection, c.getX() - 16, c.getY() - 10, 94, 92);
        }

        game.spriteBatch.end();

        // 5. Render Scene2D UI (Item lists, navigation buttons)
        ui.act(delta);
        if (disposed) return; // A button may have switched (and disposed) this screen
        ui.draw();
    }

    /** Helper method to render a character and their individual equipment layers. */
    private void drawCharacterWithEquipment(Character c) {
        // Draw Base Body
        batch.draw(c.getTexture(), c.getX(), c.getY(), c.getWidth(), c.getHeight());

        // Draw Armor Layer
        if(c.getCurrentArmor() != null) {
            batch.draw(c.getCurrentArmor().getTexture(), c.getX() - 4 + c.getVisualOffsetX(),
                c.getY() - 6 + c.getVisualOffsetY(), 64, 64);
        }

        // Draw Hands (Friendly characters only)
        if(c.isFriendly()) {
            batch.draw(TextureManager.HANDS, c.getX() + c.getVisualOffsetX(),
                c.getY() + 2 + c.getVisualOffsetY(), c.getWidth(), c.getHeight());
        }

        // Draw Shield Layer
        if(c.getCurrentShield() != null) {
            batch.draw(c.getCurrentShield().getTexture(), c.getX() - 16 + c.getVisualOffsetX(),
                c.getY() - 10 + c.getVisualOffsetY(), 64, 64);
        }

        // Draw Weapon Layer (Different offsets for Archer)
        FriendlyCharacter ally = (c instanceof FriendlyCharacter) ? (FriendlyCharacter) c : null;
        if(c.getCurrentWeapon() != null && ally != null) {
            if(ally.getFriendlyType() == FriendlyType.ARCHER) {
                batch.draw(c.getCurrentWeapon().getTexture(), c.getX() + 24 + c.getVisualOffsetX(),
                    c.getY() - 2 + c.getVisualOffsetY(), 64, 64);
            } else {
                batch.draw(c.getCurrentWeapon().getTexture(), c.getX() + 22 + c.getVisualOffsetX(),
                    c.getY() + 10 + c.getVisualOffsetY(), 64, 64);
            }
        }
    }

    /**
     * Updates character positions and dynamic equipment icons.
     * Maps the melee and ranged formations into a single linear party list.
     */
    private void logic(){
        melee = party.getMeleeSlots();
        for (int i = 0; i < melee.length; i++) {
            if (melee[i] != null) {
                melee[i].setPosition(128 + (i * 128), 410);
                partyList[i] = melee[i];
            }
        }

        ranged = party.getRangedSlots();
        for (int i = 0; i < ranged.length; i++) {
            if (ranged[i] != null) {
                ranged[i].setPosition(512 + (i * 128), 410);
                partyList[i + 3] = ranged[i];
            }
        }

        // Update the big slot icons on the left side based on the selected hero
        if(partyList[equipmentManager.getCharacterIndex()] != null) {
            FriendlyCharacter selected = (FriendlyCharacter) partyList[equipmentManager.getCharacterIndex()];
            updateSlotIcons(selected);
        }
    }

    /** Updates placeholder textures based on what the selected character is wearing. */
    private void updateSlotIcons(FriendlyCharacter c) {
        // Weapon Slot
        if(c.getCurrentWeapon() != null) selectWeapon = c.getCurrentWeapon().getTexture();
        else if(c.getFriendlyType() == FriendlyType.MAGE) selectWeapon = selectWand;
        else if (c.getFriendlyType() == FriendlyType.ARCHER) selectWeapon = selectBow;
        else selectWeapon = selectSword;

        // Shield Slot
        if(c.getCurrentShield() != null) selectShield = c.getCurrentShield().getTexture();
        else selectShield = TextureManager.SELECT_SHIELD;

        // Armor Slot
        if(c.getCurrentArmor() != null) selectArmor = c.getCurrentArmor().getTexture();
        else selectArmor = TextureManager.SELECT_ARMOR;
    }

    @Override
    public void onFadeOutComplete() {
        // No specific logic needed on transition finish for this screen
    }

    @Override
    public void dispose() {
        ui.dispose();
        super.dispose();
    }

    public EquipmentUI getUI() { return this.ui; }

    public Character getCurrentCharacter() {
        return partyList[equipmentManager.getCharacterIndex()];
    }
}
