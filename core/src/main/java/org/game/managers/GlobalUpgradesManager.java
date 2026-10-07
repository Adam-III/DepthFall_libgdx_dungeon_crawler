package org.game.managers;

import org.game.coreGame.GameManager;
import org.game.entities.abilities.HealingAbility;
import org.game.entities.abilities.MultishotAbility;
import org.game.entities.abilities.StunAbility;
import org.game.systems.GlobalUpgradeData;
import org.game.systems.GlobalUpgradesRegistry;

/**
 * Manages permanent global upgrades that persist across different game runs.
 * These upgrades affect economy, combat stats, and unlockable party abilities.
 */
public class GlobalUpgradesManager {
    private final GameManager game;

    // Level tracking for various upgrade categories
    public int maxUnitsLevel = 0,
        criticalHitChanceLevel = 0,
        criticalMultiplierLevel = 0,
        goldMultiplierLevel = 0,
        orbMultiplierLevel = 0,
        orbDropChanceLevel = 0,
        healAbilityLevel = 0,
        stunAbilityLevel = 0,
        multishotAbilityLevel = 0,
        startFloorLevel = 0;

    private PartyManager party;

    public GlobalUpgradesManager(GameManager game){
        this.game = game;
    }

    /**
     * Returns the current level of a specific upgrade based on its string ID.
     */
    public int getLevel(String id){
        return switch (id) {
            case "gold_multiplier" -> goldMultiplierLevel;
            case "unit_count" -> maxUnitsLevel;
            case "orb_multiplier" -> orbMultiplierLevel;
            case "orb_chance" -> orbDropChanceLevel;
            case "crit_multiplier" -> criticalMultiplierLevel;
            case "crit_chance" -> criticalHitChanceLevel;
            case "ability_heal" -> healAbilityLevel;
            case "ability_stun" -> stunAbilityLevel;
            case "ability_multishot" -> multishotAbilityLevel;
            case "start_floor" -> startFloorLevel;
            default -> 0;
        };
    }

    /**
     * Formats the current bonus value of an upgrade into a human-readable string for the UI.
     */
    public String getCurrentAmount(String id){
        String current = "Current: ";
        return switch (id) {
            case "gold_multiplier" -> current + Math.ceil(getGoldMultiplier()*100) + "%";
            case "unit_count" -> current + getMaxUnitsCount() + " units.";
            case "orb_multiplier" -> current + Math.ceil(getOrbMultiplier()*100) + "%";
            case "orb_chance" -> current +  Math.ceil(getOrbChance()*100 + 30) + "%";
            case "crit_multiplier" -> current + Math.ceil(getCriticalHitMultiplier()*100) + "%";
            case "crit_chance" -> current +  Math.ceil(getCriticalHitChance()*100) + "%";
            case "ability_heal" -> current + healAbilityLevel + " lvl";
            case "ability_stun" -> current + stunAbilityLevel + " lvl";
            case "ability_multishot" -> current + multishotAbilityLevel + " lvl";
            case "start_floor" -> current + " Floor " + getMaxStartingFloor() ;
            default -> "";
        };
    }

    /**
     * Processes the purchase of an upgrade.
     * Deducts Knowledge Orbs and increments the corresponding level or unlocks an ability.
     * @param id The identifier of the upgrade.
     * @param cost The calculated cost in Knowledge Orbs.
     */
    public void purchaseUpgrade(String id, int cost){
        if(cost > game.getKnowledgeOrbs()) return;


        switch(id){
            case "gold_multiplier":
                goldMultiplierLevel++;
                break;
            case "unit_count":
                maxUnitsLevel++;
                break;
            case "orb_multiplier":
                orbMultiplierLevel++;
                break;
            case "orb_chance":
                orbDropChanceLevel++;
                break;
            case "crit_multiplier":
                criticalMultiplierLevel++;
                break;
            case "crit_chance":
                criticalHitChanceLevel++;
                break;
            case "ability_heal":
                this.party = game.getPartyManager();
                party.addAbility(new HealingAbility());
                healAbilityLevel++;
                break;
            case "ability_stun":
                this.party = game.getPartyManager();
                party.addAbility(new StunAbility());
                stunAbilityLevel++;
                break;
            case "ability_multishot":
                this.party = game.getPartyManager();
                party.addAbility(new MultishotAbility());
                multishotAbilityLevel++;
                break;
            case "start_floor":
                if(game.getFloorReached() >= (startFloorLevel + 1) * 5)
                    startFloorLevel++;
                else return;
                break;
        }
        game.addKnowledgeOrbs(-cost);
    }

    /**
     * Calculates the exponential cost of the next upgrade level.
     * Formula: baseCost * (multiplier ^ currentLevel)
     */
    public int getUpgradeCost(String id){
        GlobalUpgradeData bp = null;
        // Search registry for blueprint data
        for (int i = 0; i < GlobalUpgradesRegistry.DATA.size; i++) {
            GlobalUpgradeData b = GlobalUpgradesRegistry.DATA.get(i);
            if (b.id.equals(id)) {
                bp = b;
                break;
            }
        }

        if (bp == null) return 0;

        int level = getLevel(id);
        return (int) (bp.baseCost * Math.pow(bp.costMultiplier, level));
    }

    // --- Calculation methods for applying bonuses to game logic ---

    /** Initial unit limit is 3, increased by the upgrade level. */
    public int getMaxUnitsCount() {
        return 3 + maxUnitsLevel;
    }

    public float getGoldMultiplier() {
        return goldMultiplierLevel * 0.01f; // 1% increase per level
    }

    public float getOrbMultiplier(){
        return orbMultiplierLevel * 0.10f; // 10% increase per level
    }

    public float getOrbChance(){
        return orbDropChanceLevel * 0.02f; // 2% flat increase per level
    }

    public float getCriticalHitMultiplier(){
        return criticalMultiplierLevel * 0.1f; // +0.1x crit damage per level
    }

    public float getCriticalHitChance() {
        return criticalHitChanceLevel * 0.01f; // 1% flat increase per level
    }

    public int getMaxStartingFloor() { return startFloorLevel * 5; }
}
