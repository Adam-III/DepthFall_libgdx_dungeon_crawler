package org.game.managers;
 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import org.game.coreGame.GameManager;
import org.game.entities.abilities.HealingAbility;
import org.game.entities.abilities.MultishotAbility;
import org.game.entities.abilities.StunAbility;
import org.game.entities.base.Character;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.friendly.Archer;
import org.game.entities.friendly.Healer;
import org.game.entities.friendly.Knight;
import org.game.entities.friendly.Mage;
import org.game.entities.friendly.Swordsman;
import org.game.entities.friendly.Tank;
import org.game.items.base.BaseArmor;
import org.game.items.base.BaseShield;
import org.game.items.base.BaseWeapon;
import org.game.items.base.Item;
import org.game.items.base.LootFactory;
import org.game.saves.SaveData;
 
import java.io.File;
 
/**
 * Handles save / load for the entire game across {@link #SLOT_COUNT} slots.
 * Save files live as JSON in the platform's local storage folder
 * in a "saves" folder in the project root (see {@link #saveDir()}), so they survive process restarts and are scoped to
 * the user's application directory.
 *
 * Public API:
 *   - {@link #hasSave(int)} / {@link #peek(int)} — inspect a slot.
 *   - {@link #save(int, String)}                 — snapshot live state to disk.
 *   - {@link #load(int)}                         — apply a saved snapshot to live state.
 *   - {@link #delete(int)}                       — remove a slot file.
 */
public class SaveManager {
    private static final String AUTOSAVE_FILE = "autosave.json";
 
 
    public static final int SLOT_COUNT = 3;
    private static final String SAVE_DIR_NAME = "saves";
 
    /**
     * Folder that holds all save files.
     *
     * The desktop launcher runs with the "assets" folder as its working directory,
     * so Gdx.files.local("saves") would land in assets/saves (and get packed into the JAR).
     * When we detect that, we step up one level so saves go to the project root instead.
     * When the game runs from a built JAR, saves go next to wherever it is started.
     */
    private static FileHandle saveDir() {
        File workingDir = new File("").getAbsoluteFile();
        File root = (workingDir.getName().equals("assets") && workingDir.getParentFile() != null)
            ? workingDir.getParentFile()
            : workingDir;
        return Gdx.files.absolute(new File(root, SAVE_DIR_NAME).getAbsolutePath());
    }
 
    private static FileHandle autosaveFile() {
        return saveDir().child(AUTOSAVE_FILE);
    }
 
    private final GameManager game;
    private final Json json;
 
    public SaveManager(GameManager game) {
        this.game = game;
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
        this.json.setUsePrototypes(false);
 
        // libGDX Json can't infer generic element types via reflection.
        // We tell it explicitly so deserialization picks the right class.
        json.setElementType(SaveData.class, "unlockedHeroes", String.class);
        json.setElementType(SaveData.class, "inventory",     SaveData.ItemData.class);
        json.setElementType(SaveData.class, "shopStock", SaveData.ItemData.class);
        json.setElementType(SaveData.class, "meleeSlots",    SaveData.CharacterSlotData.class);
        json.setElementType(SaveData.class, "rangedSlots",   SaveData.CharacterSlotData.class);
    }
 
    // =====================================================================
    // FILE I/O
    // =====================================================================
 
    public boolean hasSave(int slot) {
        return resolve(slot).exists();
    }
 
    /** Read the slot from disk into a SaveData record (or null on miss/error). */
    public SaveData peek(int slot) {
        FileHandle f = resolve(slot);
        if (!f.exists()) return null;
        try {
            return json.fromJson(SaveData.class, f);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to read slot " + slot, e);
            return null;
        }
    }
 
    public boolean save(int slot, String label) {
        try {
            SaveData data = capture(label);
            FileHandle f = resolve(slot);
            f.writeString(json.prettyPrint(data), false);
            return true;
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to save slot " + slot, e);
            return false;
        }
    }
 
    public boolean load(int slot) {
        SaveData data = peek(slot);
        if (data == null) return false;
        try {
            apply(data);
            return true;
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to apply slot " + slot, e);
            return false;
        }
    }
 
    public boolean delete(int slot) {
        FileHandle f = resolve(slot);
        return !f.exists() || f.delete();
    }
 
    private FileHandle resolve(int slot) {
        return saveDir().child("slot_" + slot + ".json");
    }
 
    /** Autosave functions **/
    public boolean autosave(String label) {
        try {
            SaveData data = capture(label);
            autosaveFile().writeString(json.prettyPrint(data), false);
            return true;
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Autosave failed", e);
            return false;
        }
    }
 
    public boolean hasAutosave() {
        return autosaveFile().exists();
    }
 
    public boolean loadAutosave() {
        FileHandle f = autosaveFile();
        if (!f.exists()) return false;
        try { apply(json.fromJson(SaveData.class, f)); return true; }
        catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load autosave", e);
            return false;
        }
    }
 
    // =====================================================================
    // CAPTURE  (live state -> SaveData)
    // =====================================================================
 
    public SaveData capture(String label) {
        SaveData d = new SaveData();
        d.saveName = label != null ? label : "";
        d.timestamp = System.currentTimeMillis();
        d.playTimeSeconds = game.getPlayTimeSeconds();
 
        // Currencies & progression
        d.permanentGold     = game.getPermanentGold();
        d.knowledgeOrbs     = game.getKnowledgeOrbs();
        d.keyCount          = game.getKeyCount();
        d.floorReached      = game.getFloorReached();
        d.dungeonDifficulty = game.dungeonDifficulty.getDifficulty();
 
        // Global upgrades
        GlobalUpgradesManager u = game.getUpgrades();
        d.maxUnitsLevel             = u.maxUnitsLevel;
        d.criticalHitChanceLevel    = u.criticalHitChanceLevel;
        d.criticalMultiplierLevel   = u.criticalMultiplierLevel;
        d.goldMultiplierLevel       = u.goldMultiplierLevel;
        d.orbMultiplierLevel        = u.orbMultiplierLevel;
        d.orbDropChanceLevel        = u.orbDropChanceLevel;
        d.healAbilityLevel          = u.healAbilityLevel;
        d.stunAbilityLevel          = u.stunAbilityLevel;
        d.multishotAbilityLevel     = u.multishotAbilityLevel;
        d.startFloorLevel           = u.startFloorLevel;
 
        // Unlocked heroes
        for (Character c : game.getPartyManager().getGlobalUnlockedHeroes()) {
            d.unlockedHeroes.add(c.getName());
        }
 
        // Free inventory items
        for (Item i : game.getInventory().getInventory()) {
            d.inventory.add(toItemData(i));
        }
 
        for (Item i : game.getShopManager().getStock()) {
            d.shopStock.add(toItemData(i));
        }
 
        // Party slots — store all 3 slots in order, even if empty.
        Character[] melee = game.getPartyManager().getMeleeSlots();
        for (int i = 0; i < 3; i++) d.meleeSlots.add(toSlot(melee[i]));
 
        Character[] ranged = game.getPartyManager().getRangedSlots();
        for (int i = 0; i < 3; i++) d.rangedSlots.add(toSlot(ranged[i]));
 
        return d;
    }
 
    private SaveData.ItemData toItemData(Item i) {
        if (i == null) return null;
        return new SaveData.ItemData(i.getName(), i.getLevel());
    }
 
 
    private SaveData.CharacterSlotData toSlot(Character c) {
        SaveData.CharacterSlotData s = new SaveData.CharacterSlotData();
        if (c == null) return s; // empty slot marker
        s.heroName    = c.getName();
        s.currentHP   = c.getCurrentHP();
        s.currentMana = (c instanceof FriendlyCharacter)
            ? ((FriendlyCharacter) c).getCurrentMana()
            : 0;
        s.weapon = toItemData(c.getCurrentWeapon());
        s.shield = toItemData(c.getCurrentShield());
        s.armor  = toItemData(c.getCurrentArmor());
        return s;
    }
 
    // =====================================================================
    // APPLY  (SaveData -> live state)
    // =====================================================================
 
    public void apply(SaveData d) {
        // 1. Currencies & progression
        game.setPermanentGold(d.permanentGold);
        game.setKnowledgeOrbs(d.knowledgeOrbs);
        game.setKeyCount(d.keyCount);
        game.setFloorReached(d.floorReached);
        game.dungeonDifficulty.setDifficulty(Math.max(1, d.dungeonDifficulty));
        game.setPlayTimeSeconds(d.playTimeSeconds);
 
        // 2. Global upgrades — set raw fields directly. We do NOT call
        // purchaseUpgrade() because that would deduct knowledge orbs.
        GlobalUpgradesManager u = game.getUpgrades();
        u.maxUnitsLevel             = d.maxUnitsLevel;
        u.criticalHitChanceLevel    = d.criticalHitChanceLevel;
        u.criticalMultiplierLevel   = d.criticalMultiplierLevel;
        u.goldMultiplierLevel       = d.goldMultiplierLevel;
        u.orbMultiplierLevel        = d.orbMultiplierLevel;
        u.orbDropChanceLevel        = d.orbDropChanceLevel;
        u.healAbilityLevel          = d.healAbilityLevel;
        u.stunAbilityLevel          = d.stunAbilityLevel;
        u.multishotAbilityLevel     = d.multishotAbilityLevel;
        u.startFloorLevel           = d.startFloorLevel;
 
        // 3. Reset and rebuild hero pools.
        PartyManager party = game.getPartyManager();
        party.resetUnlocks();
        if (d.unlockedHeroes != null) {
            for (String name : d.unlockedHeroes) {
                if (!"Swordsman".equals(name)) party.unlockHeroByName(name);
            }
        }
 
        // 4. Re-add globally unlocked abilities (without equipping yet —
        // party is empty at this point).
        party.resetAbilities();
        if (d.healAbilityLevel > 0)      party.addAbilitySilent(new HealingAbility());
        if (d.stunAbilityLevel > 0)      party.addAbilitySilent(new StunAbility());
        if (d.multishotAbilityLevel > 0) party.addAbilitySilent(new MultishotAbility());
 
        // 5. Rebuild inventory from blueprints.
        InventoryManager inv = game.getInventory();
        inv.clearEquippedInventory();
        inv.getInventory().clear();
 
        if (d.inventory != null) {
            for (SaveData.ItemData id : d.inventory) {
                if (id == null) continue;
                Item item = LootFactory.createItemByName(id.name, id.level);
                if (item != null) inv.addToInventory(item);
            }
        }
 
        // Replace the freshly-rolled shop stock with what was saved.
        if (d.shopStock != null) {
            Array<Item> rebuiltStock = new Array<>();
            for (SaveData.ItemData id : d.shopStock) {
                if (id == null) continue;
                Item item = LootFactory.createItemByName(id.name, id.level);
                if (item != null) rebuiltStock.add(item);
            }
            game.getShopManager().setStock(rebuiltStock);
        }
 
        // 6. Rebuild party formation.
        party.clearParty();
 
        for (int i = 0; i < d.meleeSlots.size && i < 3; i++) {
            Character c = restoreCharacter(d.meleeSlots.get(i));
            if (c != null) party.setMeleeSlot(i, c);
        }
        for (int i = 0; i < d.rangedSlots.size && i < 3; i++) {
            Character c = restoreCharacter(d.rangedSlots.get(i));
            if (c != null) party.setRangedSlot(i, c);
        }
    }
 
    /** Build a fresh hero from a saved slot, equip its gear, stamp HP/Mana. */
    private Character restoreCharacter(SaveData.CharacterSlotData slot) {
        if (slot == null || slot.isEmpty()) return null;
 
        Character hero = createHero(slot.heroName);
        if (hero == null) return null;
 
        // Equip gear FIRST: equipping shifts maxHP, so we cap currentHP afterward.
        if (slot.weapon != null) {
            Item w = LootFactory.createItemByName(slot.weapon.name, slot.weapon.level);
            if (w instanceof BaseWeapon) {
                BaseWeapon bw = (BaseWeapon) w;
                hero.equipWeapon(bw);
                bw.setCurrentCharacter(hero);
                game.getInventory().addToEquippedInventory(bw);
            }
        }
        if (slot.shield != null) {
            Item s = LootFactory.createItemByName(slot.shield.name, slot.shield.level);
            if (s instanceof BaseShield) {
                BaseShield bs = (BaseShield) s;
                hero.equipShield(bs);
                bs.setCurrentCharacter(hero);
                game.getInventory().addToEquippedInventory(bs);
            }
        }
        if (slot.armor != null) {
            Item a = LootFactory.createItemByName(slot.armor.name, slot.armor.level);
            if (a instanceof BaseArmor) {
                BaseArmor ba = (BaseArmor) a;
                hero.equipArmor(ba);
                ba.setCurrentCharacter(hero);
                game.getInventory().addToEquippedInventory(ba);
            }
        }
 
        // Snapshot the live HP/Mana values.
        hero.setCurrentHP(slot.currentHP);
        if (hero instanceof FriendlyCharacter) {
            ((FriendlyCharacter) hero).setCurrentMana(slot.currentMana);
        }
 
        // Wire up unlocked abilities for this class.
        game.getPartyManager().equipAbilities(hero);
        return hero;
    }
 
    private Character createHero(String name) {
        if (name == null) return null;
        switch (name) {
            case "Swordsman": return new Swordsman();
            case "Archer":    return new Archer();
            case "Knight":    return new Knight();
            case "Mage":      return new Mage();
            case "Healer":    return new Healer();
            case "Tank":      return new Tank();
            default:          return null;
        }
    }
}
 
