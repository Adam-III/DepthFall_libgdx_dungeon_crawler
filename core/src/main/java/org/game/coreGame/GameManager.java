package org.game.coreGame;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.*;
import org.game.entities.base.Character;
import org.game.entities.factories.FriendlyFactory;
import org.game.managers.*;
import org.game.screens.*;
import org.game.world.dungeon.DungeonDifficulty;

import java.util.Random;

/**
 * GameManager is the central hub of the application.
 * It manages screen transitions, global game state, currencies,
 * and persistent managers.
 */
public class GameManager extends Game {

    // Core Rendering
    public SpriteBatch spriteBatch;
    public FitViewport viewport;
    private Skin skin;
    private Texture currentBG;

    // Game State & Progression
    public GameState currentState;
    public DungeonDifficulty dungeonDifficulty;
    private int permanentGold = 0;
    private int knowledgeOrbs = 0;
    private int floorReached = 1;
    private int keyCount = 0;
    private boolean isBossBattle;
    private float playTimeSeconds = 0f;
    private int startingFloor = 0;
    private final Random RNG = new Random();
    private static final long MAX_CURRENCY = 1_000_000_000L;

    // Persistent Managers
    private DungeonScreen dungeonScreen;
    private PartyManager partyManager;
    private InventoryManager inventory;
    private LootManager lootManager;
    private FriendlyFactory friendlyFactory;
    private ShopManager shopManager;
    private GlobalUpgradesManager upgrades;
    private SaveManager saveManager;

    @Override
    public void create() {
        // 1. Initialize Asset & UI basics
        TextureManager.load();
        createDefaultSkin();
        spriteBatch = new SpriteBatch();

        // 2. Setup Viewport (960x540 resolution)
        viewport = new FitViewport(960, 540);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // 3. Initialize Global Upgrades
        upgrades = new GlobalUpgradesManager(this);
        Character.setGlobalUpgrades(this.upgrades);

        // 4. Initialize Core Logic Managers
        inventory = new InventoryManager(this);
        partyManager = new PartyManager(this);
        friendlyFactory = new FriendlyFactory();
        dungeonDifficulty = new DungeonDifficulty(startingFloor);
        shopManager = new ShopManager(this, floorReached);
        lootManager = new LootManager(this);

        // 4b. Save system (must come AFTER the managers it captures from)
        saveManager = new SaveManager(this);

        // 5. Start Game
        setScreen(new StartMenuScreen(this, saveManager));
    }

    /**
     * Handles switching between different game screens based on the GameState.
     */
    public void updateState(GameState newState) {
        currentState = newState;
        Screen oldScreen = getScreen();

        switch (currentState) {
            case START_MENU -> setScreen(new StartMenuScreen(this, saveManager));
            case SHOP -> setScreen(new ShopScreen(this, shopManager));
            case INVENTORY -> {
                if (dungeonScreen == null) setScreen(new InventoryScreen(this));
                else setScreen(new DungeonInventoryScreen(this));
            }
            case PARTY_SELECTION -> setScreen(new SelectionScreen(this, partyManager));
            case EQUIPMENT_SELECTION -> setScreen(new EquipmentScreen(this, partyManager));
            case GEAR_UPGRADE -> setScreen(new GearUpgradeScreen(this));
            case UNLOCK_HEROES -> setScreen(new UnlockHeroesScreen(this));
            case GLOBAL_UPGRADE -> setScreen(new GlobalUpgradesScreen(this));
            case SAVE_LOAD -> setScreen(new SaveLoadScreen(this));
            case BATTLE -> setScreen(new BattleScreen(this, dungeonDifficulty, currentBG, partyManager, isBossBattle));
            case DUNGEON -> {
                if (dungeonScreen == null) {
                    dungeonScreen = new DungeonScreen(this, dungeonDifficulty);
                }
                setScreen(dungeonScreen);
            }
        }

        // Memory Management: Dispose of old screens unless it's the active Dungeon
        if (oldScreen != null && oldScreen != dungeonScreen) {
            oldScreen.dispose();
        }
    }

    /**
     * Resolves the logic after a battle ends.
     */
    public void resolveBattle(boolean won) {
        if (won) handleVictory();
        else     handleDefeat();
    }

    private void handleVictory() {
        if (dungeonScreen != null) {
            lootManager.generateLoot();
            dungeonScreen.getDungeonManager().getCurrentRoom().setDiscovered(true);
        }

        if (isBossBattle) {
            if (dungeonScreen != null) dungeonScreen.dispose();

            lootManager.generateBossLoot();
            dungeonScreen = null;

            if (dungeonDifficulty.getDifficulty() < 100) {
                dungeonDifficulty.increaseDifficultyLevel();
            }

            addGold(1000 * dungeonDifficulty.getDifficulty());
            rollForOrbs(2.0f);

            partyManager.refillMana(0.5f);
            updateState(GameState.DUNGEON);
            isBossBattle = false;
        } else {
            updateState(GameState.DUNGEON);
            rollForOrbs(1.0f);
        }
    }

    private void handleDefeat() {
        isBossBattle = false;
        if (dungeonScreen != null) dungeonScreen.dispose();

        inventory.clearEquippedInventory();
        partyManager.clearParty();
        inventory.addLoot(lootManager.getLoot());

        if (floorReached < dungeonDifficulty.getDifficulty()) {
            floorReached = dungeonDifficulty.getDifficulty();
        }

        shopManager = new ShopManager(this, floorReached);
        dungeonDifficulty.setDifficulty(1);
        lootManager = new LootManager(this);

        dungeonScreen = null;
        updateState(GameState.PARTY_SELECTION);

        saveManager.autosave("Floor " + floorReached);
    }

    private void rollForOrbs(float stageMultiplier) {
        if (0.3f + upgrades.getOrbChance() > RNG.nextFloat()) {
            int baseOrbs = RNG.nextInt(Math.max(1,dungeonDifficulty.getDifficulty()/2+1)) +
                Math.max(1,dungeonDifficulty.getDifficulty()/2);
            int totalOrbs = (int) (baseOrbs * (stageMultiplier + upgrades.getOrbMultiplier()));
            addKnowledgeOrbs(totalOrbs);
        }
    }

    // --- Currency Management ---

    public void addGold(int amount) {
        if (amount < 0) {
            permanentGold = Math.max(0, permanentGold + amount);
            return;
        }
        float multiplier = 1.0f + upgrades.getGoldMultiplier();
        long totalToAdd = Math.round(amount * multiplier);

        permanentGold = (int) Math.min(MAX_CURRENCY, (long) permanentGold + totalToAdd);
    }

    public void addSellGold(int amount) {
        if (amount < 0) {
            permanentGold = Math.max(0, permanentGold + amount);
            return;
        }
        permanentGold = (int) Math.min(MAX_CURRENCY, (long) permanentGold + amount);
    }

    public void addKnowledgeOrbs(int amount) {
        long result = (long) knowledgeOrbs + amount;
        knowledgeOrbs = (int) Math.max(0, Math.min(MAX_CURRENCY, result));
    }

    // --- Helper Methods ---

    public void passBattleTexture(Texture bg) {
        this.currentBG = bg;
        updateState(GameState.BATTLE);
    }

    public void unequipEquipment(Character c) {
        if (c.getCurrentWeapon() != null) inventory.addToInventory(c.unequipWeapon());
        if (c.getCurrentShield() != null) inventory.addToInventory(c.unequipShield());
        if (c.getCurrentArmor()  != null) inventory.addToInventory(c.unequipArmor());
    }

    private void createDefaultSkin() {
        skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());
        skin.add("default", new ScrollPane.ScrollPaneStyle());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up   = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
    }

    @Override
    public void render() {
        if(!(screen instanceof StartMenuScreen) && !(screen instanceof SaveLoadScreen))
            playTimeSeconds += Gdx.graphics.getDeltaTime();
        super.render(); // calls the active screen's render(delta)
    }

    // --- Getters & Setters ---

    public int getPermanentGold()  { return permanentGold; }
    public int getKnowledgeOrbs()  { return knowledgeOrbs; }
    public int getKeyCount()       { return keyCount; }
    public int getFloorReached()   { return floorReached; }
    public int getStartingFloor() { return Math.max(1, startingFloor);}
    public void addKey(int i)      { keyCount += i; }
    public boolean isDungeonNull() { return dungeonScreen == null; }
    public boolean isBossBattle()  { return isBossBattle; }
    public void setBossBattle(boolean state) { this.isBossBattle = state; }
    public void setDungeonDifficulty () { dungeonDifficulty.setDifficulty(Math.max(1,startingFloor));}
    public void setStartingFloor(int i) {
        startingFloor = Math.min((startingFloor + i), upgrades.getMaxStartingFloor());
        startingFloor = Math.max(0,startingFloor);
    }

    // Setters used by SaveManager. They bypass the multipliers/clamps in addGold
    // because we are restoring an exact previously-saved value.
    public void setPermanentGold(int v) { this.permanentGold = Math.max(0, v); }
    public void setKnowledgeOrbs(int v) { this.knowledgeOrbs = Math.max(0, v); }
    public void setKeyCount(int v)      { this.keyCount      = Math.max(0, v); }
    public void setFloorReached(int v)  {
        this.floorReached  = Math.max(1, v);
        this.shopManager = new ShopManager(this, floorReached);
    }
    public void  setPlayTimeSeconds(float v) { this.playTimeSeconds = Math.max(0f, v); }

    public InventoryManager getInventory() { return inventory; }
    public PartyManager getPartyManager()  { return partyManager; }
    public ShopManager getShopManager() { return shopManager; }
    public LootManager getLootManager()    { return lootManager; }
    public FriendlyFactory getFriendlyFactory() { return friendlyFactory; }
    public Skin getSkin() { return skin; }
    public GlobalUpgradesManager getUpgrades() { return upgrades; }
    public SaveManager getSaveManager()    { return saveManager; }
    public float getPlayTimeSeconds()      { return playTimeSeconds; }

    /**
     * Reset everything the save system tracks back to the state a brand-new
     * GameManager would have. Call this when the player wants to abandon a
     * loaded session entirely (e.g. a "New Game" button after Load).
     */
    public void wipeSaveState() {
        // 1. Currencies & progression
        permanentGold = 0;
        knowledgeOrbs = 0;
        keyCount      = 0;
        floorReached  = 1;
        playTimeSeconds = 0f;
        dungeonDifficulty.setDifficulty(1);

        // 2. Global upgrade levels
        upgrades.maxUnitsLevel           = 0;
        upgrades.startFloorLevel         = 0;
        upgrades.criticalHitChanceLevel  = 0;
        upgrades.criticalMultiplierLevel = 0;
        upgrades.goldMultiplierLevel     = 0;
        upgrades.orbMultiplierLevel      = 0;
        upgrades.orbDropChanceLevel      = 0;
        upgrades.healAbilityLevel        = 0;
        upgrades.stunAbilityLevel        = 0;
        upgrades.multishotAbilityLevel   = 0;

        // 3. Hero pool + abilities (this re-seeds the default Swordsman unlock)
        partyManager.resetUnlocks();
        partyManager.resetAbilities();

        // 4. Inventory (free + equipped)
        inventory.clearEquippedInventory();
        inventory.getInventory().clear();

        // 5. Party formation
        partyManager.clearParty();

        // 6. Run-scoped managers — replace, like handleDefeat() does
        shopManager  = new ShopManager(this, 1);
        lootManager  = new LootManager(this);

        // 7. Drop any cached dungeon screen so the next DUNGEON state rebuilds it
        if (dungeonScreen != null) {
            dungeonScreen.dispose();
            dungeonScreen = null;
        }

        isBossBattle = false;
    }

    /**
     * Releases every GPU resource when the application closes.
     */
    @Override
    public void dispose() {
        Screen current = getScreen();
        if (current != null) current.dispose();
        if (dungeonScreen != null && dungeonScreen != current) dungeonScreen.dispose();

        TextureManager.dispose();
        if (skin != null) skin.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
    }
}
