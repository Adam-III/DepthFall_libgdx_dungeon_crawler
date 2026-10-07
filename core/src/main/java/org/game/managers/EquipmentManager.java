package org.game.managers;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.items.base.*;
import org.game.entities.base.Character;
import org.game.screens.EquipmentScreen;
import org.game.ui.wholeUI.EquipmentUI;

/**
 * Handles the logic for equipping and unequipping items on party members.
 * Manages interaction between the player's inventory and character equipment slots.
 */
public class EquipmentManager {
    private final GameManager game;
    private final PartyManager party;
    private EquipmentMode mode; // Current filter mode (WEAPON, ARMOR, SHIELD)
    private int characterIndex = 0; // Currently selected character in the UI
    private EquipmentUI ui;
    private final EquipmentScreen screen;
    private Character[] currentParty = new Character[6];

    public EquipmentManager(GameManager game, PartyManager party, EquipmentScreen screen){
        this.game = game;
        this.party = party;
        this.screen = screen;

        this.currentParty = party.getParty();
        this.mode = EquipmentMode.DEFAULT;
    }

    /**
     * Updates the filtering mode based on which slot the player clicked in the UI.
     */
    public void updateState(String state){
        switch(state){
            case "SHIELD" : {
                this.mode = EquipmentMode.SHIELD;
                break;
            }
            case "WEAPON" : {
                this.mode = EquipmentMode.WEAPON;
                break;
            }
            case "ARMOR" : {
                this.mode = EquipmentMode.ARMOR;
                break;
            }
            default: return;
        }
    }

    /**
     * Returns a filtered list of items from the inventory that match the current
     * equipment mode and the selected character's class requirements.
     */
    public Array<Item> getItems(){
        Array<Item> list = new Array<>();
        for(Item item : game.getInventory().getInventory()){
            switch(mode){
                case SHIELD -> {
                    if(item instanceof BaseShield) list.add(item);
                }
                case ARMOR -> {
                    if(item instanceof BaseArmor) list.add(item);
                }
                case WEAPON -> {
                    if(item instanceof BaseWeapon weapon)
                        if(currentParty[characterIndex] != null){
                            FriendlyCharacter c = (FriendlyCharacter) currentParty[characterIndex];
                            // Class-specific weapon filtering (Sword for Swordsman, Staff for Mage, etc.)
                            if(c.getFriendlyType() == FriendlyType.SWORDSMAN) {
                                if(weapon.getWeaponType() == EquipmentMode.SWORD) list.add(weapon);
                            } else if(c.getFriendlyType() == FriendlyType.MAGE){
                                if(weapon.getWeaponType() == EquipmentMode.STAFF) list.add(weapon);
                            } else if(c.getFriendlyType() == FriendlyType.ARCHER){
                                if(weapon.getWeaponType() == EquipmentMode.BOW) list.add(weapon);
                            }
                        }
                }
                default -> list.add(null);
            }
        }
        return list;
    }

    public int getCharacterIndex() { return this.characterIndex; }
    public EquipmentMode getMode() { return mode; }

    /** Sets which party member slot is currently being managed. */
    public void setSlot(int i) {
        characterIndex = i;
    }

    /**
     * Equips the selected item to the current character.
     * Handles unequipping the previous item and moving it back to the inventory.
     */
    public void equipItem(Item item) {
        Character c;
        if(currentParty[characterIndex] != null) c = currentParty[characterIndex];
        else return;

        this.ui = screen.getUI();

        // --- Logic for Shields ---
        if(item instanceof BaseShield){
            if(c.getCurrentShield() != null) {
                // Return old shield to inventory
                game.getInventory().removeFromEquippedInventory(c.getCurrentShield());
                c.getCurrentShield().setCurrentCharacter(null);
                game.getInventory().addToInventory(c.unequipShield());
            }
            // Equip new shield
            c.equipShield((BaseShield) item);
            item.setCurrentCharacter(c);
            game.getInventory().removeFromInventory(item);
            game.getInventory().addToEquippedInventory(item);
            ui.refreshDisplay();
        }
        // --- Logic for Weapons ---
        else if(item instanceof BaseWeapon){
            if(c.getCurrentWeapon() != null) {
                game.getInventory().removeFromEquippedInventory(c.getCurrentWeapon());
                c.getCurrentWeapon().setCurrentCharacter(null);
                game.getInventory().addToInventory(c.unequipWeapon());
            }
            c.equipWeapon((BaseWeapon) item);
            item.setCurrentCharacter(c);
            game.getInventory().removeFromInventory(item);
            game.getInventory().addToEquippedInventory(item);
            ui.refreshDisplay();
        }
        // --- Logic for Armor ---
        else if(item instanceof BaseArmor){
            if(c.getCurrentArmor() != null) {
                game.getInventory().removeFromEquippedInventory(c.getCurrentArmor());
                c.getCurrentArmor().setCurrentCharacter(null);
                game.getInventory().addToInventory(c.unequipArmor());
            }
            c.equipArmor((BaseArmor) item);
            item.setCurrentCharacter(c);
            game.getInventory().removeFromInventory(item);
            game.getInventory().addToEquippedInventory(item);
            ui.refreshDisplay();
        }
    }
}
