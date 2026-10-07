package org.game.managers;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.entities.abilities.AbilityStrategy;
import org.game.entities.base.Character;
import org.game.entities.factories.EnemyFactory;
import org.game.screens.BattleScreen;
import org.game.world.dungeon.DungeonDifficulty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Main manager responsible for controlling the turn-based combat system.
 * Handles state transitions, turn order, target selection, and AI execution.
 */
public class BattleManager {
    private static final Logger logger = LoggerFactory.getLogger(BattleManager.class);

    /** Possible states for the battle phase logic */
    public enum BattleState { START, PLAYER_TURN, SELECT_ACTION, SELECT_TARGET, ENEMY_TURN, BUSY, LOST, WON }

    private GameManager game;
    private BattleState state;
    private Array <Character> enemies, allies, backRow, frontRow;
    private Array<Character> turnQueue = new Array<>();
    private int currentCharacterIndex;
    private float busyTimer = 0;
    private final float WAIT_TIME = 1.2f;
    private BattleScreen screen;
    private AnimationManager animation;
    private boolean isAttacking = false;
    private static final Random RNG = new Random();
    private final EnemyFactory factory;
    DungeonDifficulty diff;
    private AbilityStrategy selectedAbility;
    private int selectedAbilityIndex;

    /**
     * Initializes the battle manager and sets up the initial turn order.
     */
    public BattleManager(GameManager game, Array<Character> enemies, Array<Character> allies, EnemyFactory factory, BattleScreen screen, DungeonDifficulty diff){
        this.game = game;
        this.allies = allies;
        this.enemies = enemies;
        this.screen = screen;
        this.diff = diff;
        this.factory = factory;
        animation = new AnimationManager(game);

        // Reset blocking state for all units at the start
        for(Character c : enemies) c.setBlocking(false);
        for(Character c : allies) c.setBlocking(false);

        createRows();
        determineTurnOrder();

        this.currentCharacterIndex = 0;
        this.state = BattleState.START;
    }

    /**
     * Main update loop that drives the battle state machine.
     * @param delta time elapsed since last frame.
     */
    public void update(float delta) {
        if (state == BattleState.START) {
            logger.debug("Initializing battle...");
            busyTimer += delta;
            if (busyTimer >= WAIT_TIME) {
                busyTimer = 0;
                determineTurnOrder();
                currentCharacterIndex = -1;
                nextTurn();
            }
            return;
        }

        Character activeUnit = getCurrentUnit();
        if (activeUnit == null) return;

        switch (state) {
            case PLAYER_TURN:
                // If it's a friendly unit, show the UI menu
                if (activeUnit.isFriendly()) {
                    screen.getBattleUI().showActionMenu(activeUnit, this);
                    state = BattleState.SELECT_ACTION;
                } else {
                    state = BattleState.ENEMY_TURN;
                }
                break;

            case SELECT_ACTION:
                // Waiting for user to click a button (Attack/Ability/Block)
                break;

            case SELECT_TARGET:
                // Waiting for user to click on a character target
                screen.updateCursor();
                break;

            case ENEMY_TURN:
                executeEnemyAI();
                state = BattleState.BUSY;
                break;

            case BUSY:
                // Global wait state to let animations finish
                busyTimer += delta;
                if (busyTimer >= WAIT_TIME) {
                    busyTimer = 0;
                    nextTurn();
                }
                break;

            case WON:
                logger.info("Player won the battle!");
                game.resolveBattle(true);
                state = null;
                break;

            case LOST:
                logger.info("Player lost the battle!");
                // Clear equipment as a penalty for losing
                for(Character c : allies){
                    game.unequipEquipment(c);
                }
                game.resolveBattle(false);
                state = null;
                break;
        }
    }

    /**
     * Calculates turn order based on each character's Attack Speed.
     */
    private void determineTurnOrder(){
        turnQueue.clear();
        for(Character c : enemies) if(c != null) turnQueue.add(c);
        for(Character c : allies) if(c != null) turnQueue.add(c);

        // Sorting by speed descending: highest speed goes first
        turnQueue.sort((a, b) -> Integer.compare(b.getAttackSpeed(), a.getAttackSpeed()));
    }

    /**
     * Moves to the next valid character in the turn queue.
     * Handles win/loss checks and stun effects.
     */
    private void nextTurn() {
        isAttacking = false;
        selectedAbility = null;
        selectedAbilityIndex = -1;

        // Check if battle is over
        if (isTeamDead(enemies)) {
            state = BattleState.WON;
            return;
        }
        if (isTeamDead(allies)) {
            state = BattleState.LOST;
            return;
        }

        currentCharacterIndex++;
        if (currentCharacterIndex >= turnQueue.size) {
            currentCharacterIndex = 0;
        }

        Character activeUnit = turnQueue.get(currentCharacterIndex);

        // Handle Stun mechanics
        if(activeUnit.isStunned()){
            activeUnit.resetStun();
            state = BattleState.BUSY; // Skip turn
            return;
        }

        // Skip dead units in the queue
        int safetyCounter = 0;
        while (!activeUnit.isAlive() && safetyCounter < turnQueue.size) {
            currentCharacterIndex++;
            if (currentCharacterIndex >= turnQueue.size) currentCharacterIndex = 0;
            activeUnit = turnQueue.get(currentCharacterIndex);
            safetyCounter++;
        }

        if (activeUnit.isFriendly()) state = BattleState.PLAYER_TURN;
        else state = BattleState.ENEMY_TURN;
    }

    /**
     * Validates if a target can be attacked based on the current unit's range.
     */
    public boolean isValidTarget(Character target) {
        Character current = getCurrentUnit();
        if (current == null) return false;

        boolean isValid = false;
        switch(current.getAttackType()){
            case MELEE:
                // Melee can only hit back row if the front row is dead
                if (isFrontRowDead() || frontRow.contains(target, true)) isValid = true;
                break;
            case RANGE:
                // Ranged can hit anyone
                isValid = true;
                break;
        }
        return isValid;
    }

    /**
     * Logic triggered when a user selects a target on the battlefield.
     */
    public void onUnitSelected(Character target) {
        if (state != BattleState.SELECT_TARGET) return;
        Character activeUnit = getCurrentUnit();

        boolean isFriendlyAction = (selectedAbility != null) && selectedAbility.targetsAlly();
        boolean targetIsAlly = allies.contains(target, true);

        // Validation for friendly vs hostile target selection
        if (isFriendlyAction != targetIsAlly) return;
        if (!isFriendlyAction && !isValidTarget(target)) return;

        isAttacking = true;
        screen.hideAttackOptions();

        if (selectedAbility != null) {
            // Attempt to consume mana and execute ability
            if(!activeUnit.useAbility(selectedAbilityIndex, target, allies, enemies)){
                state = BattleState.PLAYER_TURN;
                selectedAbility = null;
                screen.updateCursor();
                return;
            }
            animation.startAbilityAnimation(selectedAbility, target, activeUnit);
            selectedAbility = null;
            this.state = BattleState.BUSY;
            screen.updateCursor();
            return;
        } else {
            // Basic attack logic
            activeUnit.attack(allies, enemies, animation);
        }

        animation.startAttackAnimation(activeUnit, target);
        this.busyTimer = 0;
        this.state = BattleState.BUSY;
        screen.updateCursor();
    }

    /** Checks if all characters in a specific team are dead */
    private boolean isTeamDead(Array <Character> team){
        for(Character c : team ) if(c.isAlive()) return false;
        return true;
    }

    /** Logic for enemy turns (basic automated attack) */
    private void executeEnemyAI(){
        Character activeUnit = getCurrentUnit();
        activeUnit.attack(enemies, allies, this.animation);
        this.busyTimer = 0;
        this.state = BattleState.BUSY;
    }

    /** Cancels the target selection phase */
    public void eraseAction() {
        if(!isAttacking && state == BattleState.SELECT_TARGET) {
            state = BattleState.PLAYER_TURN;
            screen.updateCursor();
        }
    }

    /** Categorizes enemies into front and back rows for melee targeting logic */
    private void createRows() {
        frontRow = new Array<>();
        backRow = new Array<>();

        if (game.isBossBattle()) {
            for (Character c : enemies) if (c != null) frontRow.add(c);
        } else {
            for (int i = 0; i < factory.getFrontRow().length; i++) {
                frontRow.add(factory.getFrontRow()[i]);
            }
            for (int i = 0; i < factory.getBackRow().length; i++) {
                backRow.add(factory.getBackRow()[i]);
            }
        }
    }

    public boolean hasStarted() {
        return turnQueue.size > 0 && currentCharacterIndex >= 0;
    }

    public Character getCurrentUnit() {
        if (!hasStarted()) return null;
        return turnQueue.get(currentCharacterIndex);
    }

    public void onAttackButtonClicked() {
        if (state == BattleState.SELECT_ACTION) state = BattleState.SELECT_TARGET;
    }

    public void onBlockButtonClicked() {
        if (state == BattleState.SELECT_ACTION) {
            getCurrentUnit().setBlocking(true);
            state = BattleState.BUSY;
        }
    }

    public void onAbilitySelected(int index) {
        Character activeUnit = getCurrentUnit();
        if (state == BattleState.SELECT_ACTION && activeUnit != null) {
            this.selectedAbilityIndex = index;
            this.selectedAbility = activeUnit.getAbilities().get(index);
            this.state = BattleState.SELECT_TARGET;
        }
    }

    /** Helper to check if any front-row unit is still alive */
    public boolean isFrontRowDead(){
        if (frontRow.size == 0) return true;
        for (Character c : frontRow) if (c != null && c.isAlive()) return false;
        return true;
    }

    public AnimationManager getAnimation() { return animation; }
    public boolean isAttacking(){ return isAttacking; }
    public BattleState getState() { return this.state; }

    /** Returns a copy of the turn queue starting from the active unit */
    public Array<Character> getTurnQueue() {
        Array<Character> que = new Array<>();
        if (turnQueue.size == 0 || currentCharacterIndex < 0) return que;

        for (int count = 0; count < turnQueue.size; count++) {
            int nextIndex = (currentCharacterIndex + count) % turnQueue.size;
            Character c = turnQueue.get(nextIndex);
            if (c != null && c.isAlive()) que.add(c);
        }
        return que;
    }
}
