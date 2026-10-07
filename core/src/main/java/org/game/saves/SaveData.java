package org.game.saves;

import com.badlogic.gdx.utils.Array;

/**
 * Data Transfer Object holding the entire serializable game state.
 * This is what the SaveManager writes to disk and reads back.
 *
 * Anything that should persist must live here. Texture references, manager
 * instances, observer lists, and any other transient runtime objects are
 * intentionally NOT stored — they are rebuilt at load time from the
 * primitive fields below.
 */
public class SaveData {

    /** Bumped whenever the file format changes so old saves can be detected. */
    public int saveVersion = 1;

    /** Wall-clock millis the save was written. */
    public long timestamp;

    /** Total seconds of wall-clock playtime accumulated. */
    public float playTimeSeconds;

    /** Free-form label shown in the slot UI (e.g. "Floor 12"). */
    public String saveName = "";

    // ---------------- Currencies & high-level progression ----------------
    public int permanentGold;
    public int knowledgeOrbs;
    public int keyCount;
    public int floorReached;
    public int dungeonDifficulty;

    // ---------------- Global (meta) upgrade levels -----------------------
    public int maxUnitsLevel;
    public int startFloorLevel;
    public int criticalHitChanceLevel;
    public int criticalMultiplierLevel;
    public int goldMultiplierLevel;
    public int orbMultiplierLevel;
    public int orbDropChanceLevel;
    public int healAbilityLevel;
    public int stunAbilityLevel;
    public int multishotAbilityLevel;

    /** Names of every hero that has been unlocked (e.g. "Swordsman"). */
    public Array<String> unlockedHeroes = new Array<>();

    /** Free-floating items the player owns that aren't equipped on a slot. */
    public Array<ItemData> inventory = new Array<>();

    /** The shop's exact current stock at save time. */
    public Array<ItemData> shopStock = new Array<>();

    /** 3 melee front-row slots; null entries = empty slot. */
    public Array<CharacterSlotData> meleeSlots = new Array<>();

    /** 3 ranged back-row slots; null entries = empty slot. */
    public Array<CharacterSlotData> rangedSlots = new Array<>();

    // ====================================================================
    // Nested DTOs
    // ====================================================================

    /** Snapshot of a single inventory or equipped item. */
    public static class ItemData {
        public String name;
        public int level;

        public ItemData() {}
        public ItemData(String name, int level) {
            this.name = name;
            this.level = level;
        }
    }

    /** Snapshot of a single party slot, including HP/Mana state and gear. */
    public static class CharacterSlotData {
        /** null/empty = empty slot. */
        public String heroName;
        public int currentHP;
        public int currentMana;
        public ItemData weapon;
        public ItemData shield;
        public ItemData armor;

        public boolean isEmpty() { return heroName == null || heroName.isEmpty(); }
    }
}
