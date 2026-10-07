package org.game.managers;

import com.badlogic.gdx.graphics.Texture;

/**
 * Global registry for all game textures.
 * Handles bulk loading of assets and manual memory management (disposal).
 */
public class TextureManager {
    // --- General & UI ---
    public static Texture HANDS;
    public static Texture INVENTORY_MENU, PARTY_SELECT_MENU, EQUIPMENT_SELECT_MENU;
    public static Texture KEY, KNOWLEDGE_ORB, COIN;
    public static Texture ARROW_N, ARROW_S, ARROW_E, ARROW_W;
    public static Texture SELECT_SHIELD, SELECT_SWORD, SELECT_WAND, SELECT_BOW, SELECT_ARMOR, CHARACTER_SELECTED;

    // --- Equipment: Shields ---
    public static Texture WOODEN_SHIELD, BONE_SHIELD, LEATHER_SHIELD, RUSTY_SHIELD, IRON_SHIELD, STEEL_SHIELD,
        LIGHT_GOLD_SHIELD, HEAVY_GOLD_SHIELD, EMERALD_SHIELD, RUBY_SHIELD, DIAMOND_SHIELD, MAGMA_SHIELD,
        DRAGON_SHIELD, OBSIDIAN_SHIELD;

    // --- Equipment: Weapons ---
    public static Texture WOODEN_SWORD, BONE_SWORD, RUSTY_SWORD, IRON_SWORD, SNAKE_SWORD, GOLD_SWORD,
        RUBY_SWORD, EMERALD_SWORD, DIAMOND_SWORD, MAGMA_SWORD, OBSIDIAN_SWORD;
    public static Texture WOODEN_STAFF, STICK_STAFF, IRON_STAFF, GOLD_STAFF, EMERALD_STAFF, RUBY_STAFF,
        DIAMOND_STAFF, DRAGON_STAFF, OBSIDIAN_STAFF;
    public static Texture WOODEN_BOW, BONE_BOW, RUSTY_BOW, IRON_BOW, GOLD_BOW, EMERALD_BOW, RUBY_BOW,
        DIAMOND_BOW, MAGMA_BOW, OBSIDIAN_BOW;

    // --- Equipment: Armor ---
    public static Texture WOODEN_ARMOR, LEATHER_ARMOR, RUSTY_ARMOR, IRON_ARMOR, STEEL_ARMOR, GOLD_ARMOR,
        EMERALD_ARMOR, RUBY_ARMOR, DIAMOND_ARMOR, MAGMA_ARMOR, DRAGON_ARMOR, OBSIDIAN_ARMOR;

    // --- Hostile Entities (Enemies) ---
    public static Texture ANIMAL_BEE, ANIMAL_BOAR, ANIMAL_GECKO, ANIMAL_GOOSE, ANIMAL_LADY_BUG,
        ANIMAL_RAT, ANIMAL_SCORPION, ANIMAL_SHRIMP_BUG, ANIMAL_SPIDER, ANIMAL_WORM;
    public static Texture DEVIL_BLUE, DEVIL_BOAR, DEVIL_EYE, DEVIL_FIRE_SPIRIT, DEVIL_GOAT,
        DEVIL_GRIFFIN, DEVIL_RED, DEVIL_SKULL_REINDEER, DEVIL_WINGS;
    public static Texture MUSHROOM_BIG, MUSHROOM_CLAW, MUSHROOM_CRAB, MUSHROOM_FAT,
        MUSHROOM_GROUP, MUSHROOM_MOLE, MUSHROOM_SCORPION;
    public static Texture PLANT_MEAT_EATER, PLANT_ONION, PLANT_PARSLEY, PLANT_PUMPKIN,
        PLANT_ROSE, PLANT_SUNFLOWER, PLANT_TOMATO;
    public static Texture SLIME_ARROW_HEAD, SLIME_ATTACKED, SLIME_AXE_HEAD, SLIME_BLOB, SLIME_FAMILY,
        SLIME_FLOATING, SLIME_GOLEM, SLIME_KIDS, SLIME_TREE;

    // --- Friendly Entities & Combat ---
    public static Texture PROJECTILE;
    public static Texture MAGE, ARCHER, SWORDSMAN, KNIGHT, TANK, HEALER;
    public static Texture RED_DRAGON;

    /**
     * Synchronously loads all textures into VRAM.
     * Should be called during the initial game loading screen.
     */
    public static void load() {
        HANDS = new Texture("objects/hands.png");

        // UI & Menus
        INVENTORY_MENU = new Texture("ui/menus/ShopMenu.png");
        PARTY_SELECT_MENU = new Texture("ui/menus/PartySelectMenu.png");
        EQUIPMENT_SELECT_MENU = new Texture("ui/menus/EquipmentSelectionMenu.png");

        // Icons
        SELECT_SHIELD = new Texture("ui/icons/shield_selection.png");
        SELECT_SWORD = new Texture("ui/icons/sword_selection.png");
        SELECT_WAND = new Texture("ui/icons/wand_selection.png");
        SELECT_BOW = new Texture("ui/icons/bow_selection.png");
        SELECT_ARMOR = new Texture("ui/icons/armor_selection.png");
        CHARACTER_SELECTED = new Texture("ui/icons/character_selected.png");

        // Shields
        WOODEN_SHIELD = new Texture("items/shield/wooden_shield.png");
        BONE_SHIELD = new Texture("items/shield/bone_shield.png");
        LEATHER_SHIELD = new Texture("items/shield/leather_shield.png");
        RUSTY_SHIELD = new Texture("items/shield/rusty_shield.png");
        IRON_SHIELD = new Texture("items/shield/iron_shield.png");
        STEEL_SHIELD = new Texture("items/shield/steel_shield.png");
        LIGHT_GOLD_SHIELD = new Texture("items/shield/lightGold_shield.png");
        HEAVY_GOLD_SHIELD = new Texture("items/shield/heavyGold_shield.png");
        EMERALD_SHIELD = new Texture("items/shield/emerald_shield.png");
        RUBY_SHIELD = new Texture("items/shield/ruby_shield.png");
        DIAMOND_SHIELD = new Texture("items/shield/diamond_shield.png");
        MAGMA_SHIELD = new Texture("items/shield/magma_shield.png");
        DRAGON_SHIELD = new Texture("items/shield/dragon_shield.png");
        OBSIDIAN_SHIELD = new Texture("items/shield/obsidian_shield.png");

        // Swords
        WOODEN_SWORD = new Texture("items/weapon/sword/wooden_sword.png");
        BONE_SWORD = new Texture("items/weapon/sword/bone_sword.png");
        RUSTY_SWORD = new Texture("items/weapon/sword/rusty_sword.png");
        IRON_SWORD = new Texture("items/weapon/sword/iron_sword.png");
        SNAKE_SWORD = new Texture("items/weapon/sword/snake_sword.png");
        GOLD_SWORD = new Texture("items/weapon/sword/gold_sword.png");
        RUBY_SWORD = new Texture("items/weapon/sword/ruby_sword.png");
        EMERALD_SWORD = new Texture("items/weapon/sword/emerald_sword.png");
        DIAMOND_SWORD = new Texture("items/weapon/sword/diamond_sword.png");
        MAGMA_SWORD = new Texture("items/weapon/sword/magma_sword.png");
        OBSIDIAN_SWORD = new Texture("items/weapon/sword/obsidian_sword.png");

        // Staffs
        WOODEN_STAFF = new Texture("items/weapon/staff/wooden_staff.png");
        STICK_STAFF = new Texture("items/weapon/staff/stick_staff.png");
        IRON_STAFF = new Texture("items/weapon/staff/iron_staff.png");
        GOLD_STAFF = new Texture("items/weapon/staff/gold_staff.png");
        EMERALD_STAFF = new Texture("items/weapon/staff/emerald_staff.png");
        RUBY_STAFF = new Texture("items/weapon/staff/ruby_staff.png");
        DIAMOND_STAFF = new Texture("items/weapon/staff/diamond_staff.png");
        DRAGON_STAFF = new Texture("items/weapon/staff/dragon_staff.png");
        OBSIDIAN_STAFF = new Texture("items/weapon/staff/obsidian_staff.png");

        // Bows
        WOODEN_BOW = new Texture("items/weapon/bow/wooden_bow.png");
        BONE_BOW = new Texture("items/weapon/bow/bone_bow.png");
        RUSTY_BOW = new Texture("items/weapon/bow/rusty_bow.png");
        IRON_BOW = new Texture("items/weapon/bow/iron_bow.png");
        GOLD_BOW = new Texture("items/weapon/bow/gold_bow.png");
        EMERALD_BOW = new Texture("items/weapon/bow/emerald_bow.png");
        RUBY_BOW = new Texture("items/weapon/bow/ruby_bow.png");
        DIAMOND_BOW = new Texture("items/weapon/bow/diamond_bow.png");
        MAGMA_BOW = new Texture("items/weapon/bow/magma_bow.png");
        OBSIDIAN_BOW = new Texture("items/weapon/bow/obsidian_bow.png");

        // Armor sets
        WOODEN_ARMOR = new Texture("items/armor/wooden_armor.png");
        LEATHER_ARMOR = new Texture("items/armor/leather_armor.png");
        RUSTY_ARMOR = new Texture("items/armor/rusty_armor.png");
        IRON_ARMOR = new Texture("items/armor/iron_armor.png");
        STEEL_ARMOR = new Texture("items/armor/steel_armor.png");
        GOLD_ARMOR = new Texture("items/armor/gold_armor.png");
        EMERALD_ARMOR = new Texture("items/armor/emerald_armor.png");
        RUBY_ARMOR = new Texture("items/armor/ruby_armor.png");
        DIAMOND_ARMOR = new Texture("items/armor/diamond_armor.png");
        MAGMA_ARMOR = new Texture("items/armor/magma_armor.png");
        DRAGON_ARMOR = new Texture("items/armor/dragon_armor.png");
        OBSIDIAN_ARMOR = new Texture("items/armor/obsidian_armor.png");

        // Enemies
        ANIMAL_BEE = new Texture("entities/hostile/AnimalBee.png");
        ANIMAL_BOAR = new Texture("entities/hostile/AnimalBoar.png");
        ANIMAL_GECKO = new Texture("entities/hostile/AnimalGecko.png");
        ANIMAL_GOOSE = new Texture("entities/hostile/AnimalGoose.png");
        ANIMAL_LADY_BUG = new Texture("entities/hostile/AnimalLadyBug.png");
        ANIMAL_RAT = new Texture("entities/hostile/AnimalRat.png");
        ANIMAL_SCORPION = new Texture("entities/hostile/AnimalScorpion.png");
        ANIMAL_SHRIMP_BUG = new Texture("entities/hostile/AnimalShrimpBug.png");
        ANIMAL_SPIDER = new Texture("entities/hostile/AnimalSpider.png");
        ANIMAL_WORM = new Texture("entities/hostile/AnimalWorm.png");

        DEVIL_BLUE = new Texture("entities/hostile/DevilBlue.png");
        DEVIL_BOAR = new Texture("entities/hostile/DevilBoar.png");
        DEVIL_EYE = new Texture("entities/hostile/DevilEye.png");
        DEVIL_FIRE_SPIRIT = new Texture("entities/hostile/DevilFireSpirit.png");
        DEVIL_GOAT = new Texture("entities/hostile/DevilGoat.png");
        DEVIL_GRIFFIN = new Texture("entities/hostile/DevilGriffin.png");
        DEVIL_RED = new Texture("entities/hostile/DevilRed.png");
        DEVIL_SKULL_REINDEER = new Texture("entities/hostile/DevilSkullReindeer.png");
        DEVIL_WINGS = new Texture("entities/hostile/DevilWings.png");

        MUSHROOM_BIG = new Texture("entities/hostile/MushroomBig.png");
        MUSHROOM_CLAW = new Texture("entities/hostile/MushroomClaw.png");
        MUSHROOM_CRAB = new Texture("entities/hostile/MushroomCrab.png");
        MUSHROOM_FAT = new Texture("entities/hostile/MushroomFat.png");
        MUSHROOM_GROUP = new Texture("entities/hostile/MushroomGroup.png");
        MUSHROOM_MOLE = new Texture("entities/hostile/MushroomMole.png");
        MUSHROOM_SCORPION = new Texture("entities/hostile/MushroomScorpion.png");

        PLANT_MEAT_EATER = new Texture("entities/hostile/PlantMeatEater.png");
        PLANT_ONION = new Texture("entities/hostile/PlantOnion.png");
        PLANT_PARSLEY = new Texture("entities/hostile/PlantParsley.png");
        PLANT_PUMPKIN = new Texture("entities/hostile/PlantPumpkin.png");
        PLANT_ROSE = new Texture("entities/hostile/PlantRose.png");
        PLANT_SUNFLOWER = new Texture("entities/hostile/PlantSunflower.png");
        PLANT_TOMATO = new Texture("entities/hostile/PlantTomato.png");

        SLIME_ARROW_HEAD = new Texture("entities/hostile/SlimeArrowHead.png");
        SLIME_ATTACKED = new Texture("entities/hostile/SlimeAttacked.png");
        SLIME_AXE_HEAD = new Texture("entities/hostile/SlimeAxeHead.png");
        SLIME_BLOB = new Texture("entities/hostile/SlimeBlob.png");
        SLIME_FAMILY = new Texture("entities/hostile/SlimeFamily.png");
        SLIME_FLOATING = new Texture("entities/hostile/SlimeFloating.png");
        SLIME_GOLEM = new Texture("entities/hostile/SlimeGolem.png");
        SLIME_KIDS = new Texture("entities/hostile/SlimeKids.png");
        SLIME_TREE = new Texture("entities/hostile/SlimeTree.png");

        // Heroes
        MAGE = new Texture("entities/friendly/mage.png");
        ARCHER = new Texture("entities/friendly/archer.png");
        SWORDSMAN = new Texture("entities/friendly/swordsman.png");
        TANK = new Texture("entities/friendly/tank.png");
        HEALER = new Texture("entities/friendly/healer.png");
        KNIGHT = new Texture("entities/friendly/knight.png");

        // Miscellaneous
        PROJECTILE = new Texture("entities/projectiles/projMage.png");
        KEY = new Texture("items/other/key.png");
        KNOWLEDGE_ORB = new Texture("ui/KnowledgeOrb.png");
        COIN = new Texture("ui/Coin.png");

        // Dungeon navigation
        ARROW_N = new Texture("ui/dungeon/ArrowN.png");
        ARROW_S = new Texture("ui/dungeon/ArrowS.png");
        ARROW_E = new Texture("ui/dungeon/ArrowE.png");
        ARROW_W = new Texture("ui/dungeon/ArrowW.png");

        // Bosses
        RED_DRAGON = new Texture("entities/boss/RedDragon.png");
    }

    /**
     * Unloads all textures from memory.
     * Essential to call when the game closes to prevent memory leaks.
     */
    public static void dispose() {
        HANDS.dispose();
        EQUIPMENT_SELECT_MENU.dispose();
        INVENTORY_MENU.dispose();
        PARTY_SELECT_MENU.dispose();
        SELECT_WAND.dispose();
        SELECT_BOW.dispose();
        SELECT_SWORD.dispose();
        SELECT_SHIELD.dispose();
        SELECT_ARMOR.dispose();
        CHARACTER_SELECTED.dispose();
        KEY.dispose();
        KNOWLEDGE_ORB.dispose();
        COIN.dispose();
        ARROW_N.dispose(); ARROW_S.dispose(); ARROW_E.dispose(); ARROW_W.dispose();
        RED_DRAGON.dispose();

        // Shields
        WOODEN_SHIELD.dispose(); BONE_SHIELD.dispose(); RUSTY_SHIELD.dispose();
        LEATHER_SHIELD.dispose(); IRON_SHIELD.dispose(); STEEL_SHIELD.dispose();
        LIGHT_GOLD_SHIELD.dispose(); HEAVY_GOLD_SHIELD.dispose(); EMERALD_SHIELD.dispose();
        RUBY_SHIELD.dispose(); DIAMOND_SHIELD.dispose(); MAGMA_SHIELD.dispose();
        DRAGON_SHIELD.dispose(); OBSIDIAN_SHIELD.dispose();

        // Swords
        WOODEN_SWORD.dispose(); BONE_SWORD.dispose(); RUSTY_SWORD.dispose();
        IRON_SWORD.dispose(); SNAKE_SWORD.dispose(); GOLD_SWORD.dispose();
        EMERALD_SWORD.dispose(); RUBY_SWORD.dispose(); DIAMOND_SWORD.dispose();
        MAGMA_SWORD.dispose(); OBSIDIAN_SWORD.dispose();

        // Staffs
        WOODEN_STAFF.dispose(); STICK_STAFF.dispose(); IRON_STAFF.dispose();
        GOLD_STAFF.dispose(); EMERALD_STAFF.dispose(); RUBY_STAFF.dispose();
        DIAMOND_STAFF.dispose(); DRAGON_STAFF.dispose(); OBSIDIAN_STAFF.dispose();

        // Bows
        WOODEN_BOW.dispose(); BONE_BOW.dispose(); RUSTY_BOW.dispose();
        IRON_BOW.dispose(); GOLD_BOW.dispose(); EMERALD_BOW.dispose();
        RUBY_BOW.dispose(); DIAMOND_BOW.dispose(); MAGMA_BOW.dispose();
        OBSIDIAN_BOW.dispose();

        // Armor
        WOODEN_ARMOR.dispose(); LEATHER_ARMOR.dispose(); RUSTY_ARMOR.dispose();
        IRON_ARMOR.dispose(); STEEL_ARMOR.dispose(); GOLD_ARMOR.dispose();
        EMERALD_ARMOR.dispose(); RUBY_ARMOR.dispose(); DIAMOND_ARMOR.dispose();
        MAGMA_ARMOR.dispose(); DRAGON_ARMOR.dispose(); OBSIDIAN_ARMOR.dispose();

        // Enemies
        ANIMAL_BEE.dispose(); ANIMAL_BOAR.dispose(); ANIMAL_GECKO.dispose();
        ANIMAL_GOOSE.dispose(); ANIMAL_LADY_BUG.dispose(); ANIMAL_RAT.dispose();
        ANIMAL_SCORPION.dispose(); ANIMAL_SHRIMP_BUG.dispose(); ANIMAL_SPIDER.dispose();
        ANIMAL_WORM.dispose();

        DEVIL_BLUE.dispose(); DEVIL_BOAR.dispose(); DEVIL_EYE.dispose();
        DEVIL_FIRE_SPIRIT.dispose(); DEVIL_GOAT.dispose(); DEVIL_GRIFFIN.dispose();
        DEVIL_RED.dispose(); DEVIL_SKULL_REINDEER.dispose(); DEVIL_WINGS.dispose();

        MUSHROOM_BIG.dispose(); MUSHROOM_CLAW.dispose(); MUSHROOM_CRAB.dispose();
        MUSHROOM_FAT.dispose(); MUSHROOM_GROUP.dispose(); MUSHROOM_MOLE.dispose();
        MUSHROOM_SCORPION.dispose();

        PLANT_MEAT_EATER.dispose(); PLANT_ONION.dispose(); PLANT_PARSLEY.dispose();
        PLANT_PUMPKIN.dispose(); PLANT_ROSE.dispose(); PLANT_SUNFLOWER.dispose();
        PLANT_TOMATO.dispose();

        SLIME_ARROW_HEAD.dispose(); SLIME_ATTACKED.dispose(); SLIME_AXE_HEAD.dispose();
        SLIME_BLOB.dispose(); SLIME_FAMILY.dispose(); SLIME_FLOATING.dispose();
        SLIME_GOLEM.dispose(); SLIME_KIDS.dispose(); SLIME_TREE.dispose();

        // Other assets
        PROJECTILE.dispose();
        MAGE.dispose(); ARCHER.dispose(); SWORDSMAN.dispose();
        TANK.dispose(); HEALER.dispose(); KNIGHT.dispose();
    }
}
