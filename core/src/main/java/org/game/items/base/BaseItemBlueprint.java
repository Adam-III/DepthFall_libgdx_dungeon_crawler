package org.game.items.base;

import com.badlogic.gdx.graphics.Texture;

/**
 * Abstract base class for item blueprints.
 * Blueprints store template data (metadata) used to instantiate actual Item objects.
 */
public abstract class BaseItemBlueprint {
    /** Display name of the item */
    public final String name;

    /** The specific Item subclass to be instantiated (e.g., SwordBlueprint.class) */
    public final Class<? extends Item> itemClass;

    /** The quality tier of the item (Rank 1 to 8) */
    public final int specificRank;

    /** Graphical representation of the item */
    public final Texture texture;

    /** The minimum dungeon floor required for this item to start appearing */
    public int minFloor;

    /** The level assigned to the item during the generation process (calculated in LootFactory) */
    public int rolledLevel;

    /** Base gold value of the item */
    public int price;

    /** Scaling factor used to increase item stats based on its level */
    public int increment;

    /**
     * Primary constructor for creating a blueprint.
     * @param name       Name of the item.
     * @param rank       Tier/Quality rank.
     * @param price      Base cost in shop.
     * @param increment  Stat growth per level.
     * @param texture    Visual asset.
     * @param itemClass  The class type for instantiation.
     * @param minFloor   Minimum floor requirement.
     */
    public BaseItemBlueprint(String name, int rank, int price, int increment, Texture texture, Class<? extends Item> itemClass, int minFloor) {
        this.name = name;
        this.specificRank = rank;
        this.price = price;
        this.increment = increment;
        this.texture = texture;
        this.itemClass = itemClass;
        this.minFloor = minFloor;
    }
}
