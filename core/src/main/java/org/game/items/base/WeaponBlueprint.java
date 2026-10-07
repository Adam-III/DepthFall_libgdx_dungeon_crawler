package org.game.items.base;

import com.badlogic.gdx.graphics.Texture;

/**
 * Blueprint specifically for weapon items.
 * Extends the base blueprint by adding attack power statistics.
 */
public class WeaponBlueprint extends BaseItemBlueprint {
    /** Base attack power of the weapon before level scaling */
    public int attack;

    /**
     * Comprehensive constructor for a weapon blueprint.
     * * @param name       Display name of the weapon.
     * @param rank       Tier/Quality rank (1-8).
     * @param price      Base market price.
     * @param attack     Base attack damage.
     * @param increment  Attack growth per level.
     * @param texture    Visual asset for the weapon.
     * @param itemClass  The specific Item subclass to instantiate.
     * @param minFloor   Minimum dungeon floor requirement.
     */
    public WeaponBlueprint(String name, int rank, int price, int attack, int increment, Texture texture, Class<? extends Item> itemClass, int minFloor) {
        super(name, rank, price, increment, texture, itemClass, minFloor);
        this.attack = attack;
    }

    /**
     * Simplified constructor for weapons that can appear from the very first floor.
     * Sets minFloor to 0 by default.
     */
    public WeaponBlueprint(String name, int rank, int price, int attack, int increment,
                           Texture texture, Class<? extends Item> itemClass) {
        this(name, rank, price, attack, increment, texture, itemClass, 0);
    }
}
