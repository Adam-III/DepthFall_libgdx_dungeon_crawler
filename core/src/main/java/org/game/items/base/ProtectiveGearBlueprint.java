package org.game.items.base;

import com.badlogic.gdx.graphics.Texture;

/**
 * Blueprint specifically for defensive equipment (Armor, Shields).
 * Adds health point (HP) bonuses and damage reduction (defense) to the base item template.
 */
public class ProtectiveGearBlueprint extends BaseItemBlueprint {
    /** Health bonus provided by this gear before level scaling */
    public int hp;

    /** Percentage-based damage reduction (e.g., 0.10f for 10% reduction) */
    public float defense;

    /**
     * Comprehensive constructor for defensive gear with a minimum floor requirement.
     * * @param name       Display name of the gear.
     * @param rank       Quality tier (1-8).
     * @param price      Base market cost.
     * @param hp         Base health bonus.
     * @param increment  Growth rate for HP per level.
     * @param defense    Base defense value.
     * @param texture    Visual asset for the gear.
     * @param itemClass  The specific Item subclass to be created.
     * @param minFloor   Minimum floor required for this item to drop.
     */
    public ProtectiveGearBlueprint(String name, int rank, int price, int hp, int increment, float defense, Texture texture, Class<? extends Item> itemClass, int minFloor) {
        super(name, rank, price, increment, texture, itemClass, minFloor);
        this.hp = hp;
        this.defense = defense;
    }

    /**
     * Simplified constructor for gear that can appear from the very first floor.
     * Sets minFloor to 0 by default.
     */
    public ProtectiveGearBlueprint(String name, int rank, int price, int hp, int increment, float defense,
                                   Texture texture, Class<? extends Item> itemClass) {
        this(name, rank, price, hp, increment, defense, texture, itemClass, 0);
    }
}
