package org.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import org.game.coreGame.GameManager;
import org.game.entities.base.Character;
import org.game.entities.base.HostileCharacter;
import org.game.entities.factories.EnemyFactory;
import org.game.entities.base.FriendlyCharacter;
import org.game.entities.types.FriendlyType;
import org.game.managers.PartyManager;
import org.game.managers.TextureManager;
import org.game.ui.components.DamagePopup;
import org.game.ui.components.Projectile;
import org.game.managers.BattleManager;
import org.game.ui.wholeUI.BattleUI;
import org.game.world.dungeon.DungeonDifficulty;

/**
 * The main screen where combat gameplay occurs.
 * Manages the positioning of units, rendering of characters with equipment,
 * combat animations, and user input during battles.
 */
public class BattleScreen extends BaseScreen implements InputProcessor {
    private final GameManager game;
    private BattleManager activeBattle;
    private Array<Character> enemies, allies;
    private EnemyFactory enemyGeneration;
    private Texture currentBG, targeting, activeUnit, blocking, stunned, pixel;
    private BattleUI battleUI;
    private Cursor attackCursor;
    private BitmapFont defaultFont, damageFont;
    private PartyManager party;
    private boolean isBossBattle;

    public BattleScreen(final GameManager game, DungeonDifficulty diff, Texture currentBG, PartyManager party, boolean isBossBattle) {
        super(game);
        this.game = game;
        this.currentBG = currentBG;
        this.party = party;
        this.isBossBattle = isBossBattle;
        this.allies = new Array<>();

        // Load specific UI textures for combat state indicators
        targeting = new Texture("ui/icons/targetingUnit.png");
        activeUnit = new Texture("ui/icons/activeUnit.png");
        blocking = new Texture("ui/icons/blocking.png");
        stunned = new Texture("ui/icons/stunned.png");

        // Font initialization for UI and damage numbers
        defaultFont = new BitmapFont();
        defaultFont.getData().setScale(0.6f);
        damageFont = new BitmapFont();
        damageFont.getData().setScale(1.5f);

        pixel = new Texture("pixel.png");

        // Custom cursor for attack targeting
        Pixmap pixmap = new Pixmap(Gdx.files.internal("ui/icons/attackCursor.png"));
        attackCursor = Gdx.graphics.newCursor(pixmap, 15, 15);
        pixmap.dispose();

        // --- Enemy Positioning Logic ---
        enemyGeneration = new EnemyFactory(diff, isBossBattle);
        enemies = new Array<>();

        if(isBossBattle) {
            enemies = enemyGeneration.getBoss();
            if (enemies.size > 0) {
                // Center boss on the screen
                Character c = enemies.get(0);
                c.setPosition(526, 310);
            }
        } else {
            // Position normal enemies in two rows (Front/Back)
            Character[] enemiesMelee = enemyGeneration.getFrontRow();
            for (int i = 0; i < enemiesMelee.length; i++) {
                if (enemiesMelee[i] != null) {
                    enemiesMelee[i].setPosition(480, 410 - (i * 80));
                    enemies.add(enemiesMelee[i]);
                }
            }
            Character[] enemiesRanged = enemyGeneration.getBackRow();
            for (int i = 0; i < enemiesRanged.length; i++) {
                if (enemiesRanged[i] != null) {
                    enemiesRanged[i].setPosition(572, 380 - (i * 80));
                    enemies.add(enemiesRanged[i]);
                }
            }
        }

        // --- Ally Positioning Logic ---
        Character[] melee = party.getMeleeSlots();
        for (int i = 0; i < melee.length; i++) {
            if (melee[i] != null) {
                melee[i].setPosition(288, 410 - (i * 80));
                allies.add(melee[i]);
            }
        }
        Character[] ranged = party.getRangedSlots();
        for (int i = 0; i < ranged.length; i++) {
            if (ranged[i] != null) {
                ranged[i].setPosition(192, 380 - (i * 80));
                allies.add(ranged[i]);
            }
        }

        // Setup the battle core and its UI
        activeBattle = new BattleManager(game, enemies, allies, enemyGeneration, this, diff);
        this.battleUI = new BattleUI(game, activeBattle);

        // Input multiplexer to handle both Scene2D UI and custom mouse interactions
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(battleUI.getStage());
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void onFadeOutComplete() {}

    @Override
    public void render(float delta) {
        logic(delta);
        // The battle can end inside logic(), which switches screens and disposes this one.
        if (disposed) return;
        draw(delta);
    }

    /** Updates the battle state, transitions, and hover detection. */
    public void logic(float delta){
        updateTransition(Gdx.graphics.getDeltaTime(), 2.5f);
        updateMouseHover();

        if (activeBattle != null) {
            activeBattle.update(delta);
            activeBattle.getAnimation().updatePopups(delta);
        }
    }

    /** Main rendering loop. */
    public void draw(float delta){
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.spriteBatch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.spriteBatch.begin();

        if (currentBG != null) game.spriteBatch.draw(currentBG, 0, 0, 960, 540);

        // Visual highlighting of the currently acting unit
        for (Character a : allies) a.setActive(false);
        for (Character e : enemies) e.setActive(false);
        Character current = activeBattle.getCurrentUnit();
        if (current != null) current.setActive(true);

        // Render all units and their health/mana bars
        for (Character ally : allies) {
            drawCharacter(game.spriteBatch, ally);
            drawStatus(game.spriteBatch, ally);
        }
        for (Character enemy : enemies) {
            drawCharacter(game.spriteBatch, enemy);
            drawStatus(game.spriteBatch, enemy);
        }

        // Draw combat projectiles
        for (Projectile p : activeBattle.getAnimation().activeProjectiles) {
            if (p.active) batchDrawProjectile(p);
        }

        // Render damage/heal numbers
        drawPopups();

        drawTransition();
        game.spriteBatch.end();

        battleUI.render(delta);
    }

    private void batchDrawProjectile(Projectile p) {
        game.spriteBatch.draw(p.texture, p.x, p.y);
    }

    /** * Draws a character including their base texture,
     * armor, weapon, and shield based on their class and equipment.
     */
    public void drawCharacter(SpriteBatch batch, Character c) {
        if (c.isAlive()) {
            // Layer 1: Base Character
            batch.draw(c.getTexture(), c.getX() + c.getVisualOffsetX(), c.getY() + c.getVisualOffsetY(), c.getWidth(), c.getHeight());

            // Layer 2: Armor
            if(c.getCurrentArmor() != null) batch.draw(c.getCurrentArmor().getTexture(), c.getX()-4 + c.getVisualOffsetX(),
                c.getY()-8 + c.getVisualOffsetY(), 64, 64);

            // Layer 3: Hands (Friendly units only)
            if(c.isFriendly()) batch.draw(TextureManager.HANDS, c.getX() + c.getVisualOffsetX(),
                c.getY() + c.getVisualOffsetY(), c.getWidth(), c.getHeight());

            // Layer 4: Shield
            if(c.getCurrentShield() != null) batch.draw(c.getCurrentShield().getTexture(), c.getX()-16 + c.getVisualOffsetX(),
                c.getY() - 12 + c.getVisualOffsetY(), 64, 64);

            // Layer 5: Weapon (with Archer-specific offset)
            FriendlyCharacter ally = (c instanceof FriendlyCharacter) ? (FriendlyCharacter) c : null;
            if(c.getCurrentWeapon() != null) {
                if(ally != null && ally.getFriendlyType() == FriendlyType.ARCHER) {
                    batch.draw(c.getCurrentWeapon().getTexture(), c.getX()+24 + c.getVisualOffsetX(), c.getY()-4 + c.getVisualOffsetY(), 64, 64);
                } else {
                    batch.draw(c.getCurrentWeapon().getTexture(), c.getX()+22 + c.getVisualOffsetX(), c.getY()+8 + c.getVisualOffsetY(), 64, 64);
                }
            }

            // --- Status Indicators ---
            if (c.isActive() && c.isFriendly() && !activeBattle.isAttacking()) {
                if (c.isBlocking()) batch.draw(blocking, c.getX() + 12, c.getY());
                batch.draw(activeUnit, c.getX() - 4, c.getY() + 20);
            } else if (activeBattle.isValidTarget(c) && c.isHovered() && activeBattle.getState() != BattleManager.BattleState.BUSY) {
                batch.draw(targeting, c.getX()-25, c.getY());
            } else if (c.isAlive() && c.isBlocking()) {
                batch.draw(blocking, c.getX() + 12, c.getY());
            }
        }
    }

    /** Draws health numbers, mana, and stun icons above characters. */
    public void drawStatus(SpriteBatch batch, Character c){
        float x = c.getX() + 10 + c.getVisualOffsetX();
        float y = c.getY() + c.getVisualOffsetY() - 2;

        if(c instanceof FriendlyCharacter && c.isAlive()){
            defaultFont.setColor(Color.CYAN);
            String manaText = String.valueOf(((FriendlyCharacter) c).getCurrentMana());
            defaultFont.getData().setScale(0.5f);
            defaultFont.draw(batch, manaText, x + 12, y - 12);
        }

        if(c instanceof HostileCharacter && c.isStunned() && c.isAlive()){
            batch.draw(stunned, c.getX(), c.getY(), 64, 64);
        }

        if(c.getCurrentHP() > 0){
            float healthPercentage = (float) c.getCurrentHP() / c.getMaxHP();
            defaultFont.setColor(healthPercentage < 0.20f ? Color.RED : Color.WHITE);

            String hpText = c.getCurrentHP() + " / " + c.getMaxHP();
            defaultFont.getData().setScale(0.8f);
            defaultFont.draw(batch, hpText, x, y);
        }
        defaultFont.setColor(Color.WHITE);
    }

    /** Detects mouse hovering over units and handles selection via clicking. */
    public void updateMouseHover() {
        Vector3 mousePos = game.viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        Array<Character> allUnits = new Array<>();
        allUnits.addAll(enemies);
        allUnits.addAll(allies);

        for (Character unit : allUnits) {
            if (unit.getBounds().contains(mousePos.x, mousePos.y) && unit.isAlive()) {
                unit.setHovered(true);
                if (Gdx.input.justTouched()) {
                    activeBattle.onUnitSelected(unit);
                    unit.setHovered(false);
                }
            } else {
                unit.setHovered(false);
            }
        }
    }

    /** Changes the system cursor based on current battle state. */
    public void updateCursor() {
        if (activeBattle.getState() == BattleManager.BattleState.SELECT_TARGET) {
            Gdx.graphics.setCursor(attackCursor);
        } else {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }

    private void drawPopups() {
        for (DamagePopup p : activeBattle.getAnimation().activePopups) {
            // Shadow
            damageFont.setColor(0, 0, 0, p.alpha);
            damageFont.draw(game.spriteBatch, p.text, p.x + 2, p.y - 2);

            // Text color based on type (Crit=Red, Heal=Green, Standard=White)
            if (p.isCritical) damageFont.setColor(1, 0, 0, p.alpha);
            else if (p.isHeal) damageFont.setColor(0, 1, 0, p.alpha);
            else damageFont.setColor(1, 1, 1, p.alpha);

            damageFont.draw(game.spriteBatch, p.text, p.x, p.y);
        }
    }

    public void hideAttackOptions() { battleUI.hideAttackOptions(); }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            activeBattle.eraseAction(); // Cancel current action on right-click
            return true;
        }
        return false;
    }

    public BattleUI getBattleUI() { return battleUI; }

    @Override
    public void dispose() {
        if (disposed) return;
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        targeting.dispose();
        activeUnit.dispose();
        blocking.dispose();
        stunned.dispose();
        pixel.dispose();
        attackCursor.dispose();
        defaultFont.dispose();
        damageFont.dispose();
        battleUI.dispose();
        super.dispose();
    }

    // --- Unused InputProcessor overrides ---
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
