package org.game.systems;

import com.badlogic.gdx.utils.Array;

/**
 * A static registry that defines all global (permanent) upgrades available in the game.
 * These upgrades provide meta-progression, affecting drop rates, damage multipliers,
 * and unlocking class-specific abilities.
 */
public class GlobalUpgradesRegistry {
    /** List of all upgrade definitions used by the UI and Save systems. */
    public static Array<GlobalUpgradeData> DATA = new Array<>();

    static {
        // --- Economy Upgrades ---
        DATA.add(new GlobalUpgradeData(
            "unit_count",
            "Hero Caller",
            "Adds +1 maximum unit you can equip.",
            10,   // Initial cost
            10,   // Scaling/Value
            3     // Max Level
        ));

        DATA.add(new GlobalUpgradeData(
            "start_floor",
            "Head Start",
            "Increases the maximum starting floor.",
            30,
            1.5f,
            20
        ));

        DATA.add(new GlobalUpgradeData(
            "gold_multiplier",
            "Greed",
            "Permanent Gold multiplier.",
            10,
            1.1f,
            200
        ));

        DATA.add(new GlobalUpgradeData(
            "orb_multiplier",
            "Studying",
            "Permanent Knowledge Orb multiplier.",
            20,
            1.1f,
            100
        ));

        // --- Combat Statistics ---
        DATA.add(new GlobalUpgradeData(
            "orb_chance",
            "Wisdom",
            "Increases the chance to get Knowledge Orbs.",
            30,
            1.4f,
            20
        ));

        DATA.add(new GlobalUpgradeData(
            "crit_multiplier",
            "Brutality",
            "Increases the critical damage multiplier.",
            10,
            1.3f,
            35
        ));

        DATA.add(new GlobalUpgradeData(
            "crit_chance",
            "Precision",
            "Increases the chance to deal critical damage.",
            30,
            1.3f,
            30
        ));

        // --- Ability Unlocks ---
        // These typically have high costs and only 1 level to act as a toggle/unlock.
        DATA.add(new GlobalUpgradeData(
            "ability_heal",
            "Holy Light",
            "Heals an ally for a percentage of their max health. Unlocks healing ability for Healers.",
            100,
            1,
            1
        ));

        DATA.add(new GlobalUpgradeData(
            "ability_stun",
            "Heavy Blow",
            "Stuns enemy for one round. Unlocks stun ability for Tanks.",
            100,
            1,
            1
        ));

        DATA.add(new GlobalUpgradeData(
            "ability_multishot",
            "Multishot",
            "Shoots three projectiles at an enemy. Unlocks Multishot ability for Archers.",
            100,
            1,
            1
        ));
    }
}
