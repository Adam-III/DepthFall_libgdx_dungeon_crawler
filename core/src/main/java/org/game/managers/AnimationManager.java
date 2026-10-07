package org.game.managers;

import com.badlogic.gdx.utils.Array;
import org.game.coreGame.GameManager;
import org.game.entities.abilities.AbilityStrategy;
import org.game.entities.base.Character;
import org.game.ui.components.DamagePopup;
import org.game.ui.components.Projectile;

import java.util.Random;

/**
 * Manages all in-game animations, including combat movements, projectile physics,
 * and UI feedback like damage numbers.
 */
public class AnimationManager {
    private boolean damageApplied = false, stunApplied = false;
    public Array<Projectile> activeProjectiles = new Array<>();
    public Array<DamagePopup> activePopups = new Array<>();
    private final GameManager game;
    private Random RNG = new Random();

    public AnimationManager(GameManager game){
        this.game = game;
    }

    /**
     * Creates a new floating text element to indicate damage or healing.
     */
    public void addDamagePopup(int damage, float x, float y, boolean isCritical, boolean isHeal) {
        String text = (isHeal ? "+" : "-") + damage;
        activePopups.add(new DamagePopup(text, x, y, isCritical, isHeal));
    }

    /**
     * Updates the lifetime and position of all active popups.
     */
    public void updatePopups(float delta) {
        for (int i = activePopups.size - 1; i >= 0; i--) {
            DamagePopup p = activePopups.get(i);
            p.update(delta);
            if (p.alpha <= 0) {
                activePopups.removeIndex(i);
            }
        }
    }

    /**
     * Triggers the appropriate attack animation based on the attacker's type.
     */
    public void startAttackAnimation(final Character attacker, Character target) {
        switch(attacker.getAttackType()){
            case MELEE : attackAnimationMelee(attacker, target);
                break;
            case RANGE: attackAnimationRanged(attacker, target);
                break;
        }
    }

    /**
     * Handles the logic for ranged attacks, including projectile movement
     * and collision detection with the target.
     */
    private void attackAnimationRanged(Character attacker, Character target) {
        activeProjectiles = new Array<>();
        final Projectile p = new Projectile(attacker.getProjectileTexture(), attacker.getX(), attacker.getY(), target.getX(), target.getY());
        activeProjectiles.add(p);

        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                float delta = 0.02f;
                float dx = p.targetX - p.x;
                float dy = p.targetY - p.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                // Collision detection: when projectile reaches the target
                if (distance < 25f) {
                    attacker.performDirectAttack(target);
                    int actualDealt = target.getDamageTaken();
                    addDamagePopup(actualDealt, target.getX(), target.getY(), attacker.isCritical(), false);
                    attacker.resetCritical();

                    // Reward gold if an enemy is killed
                    if(!target.isAlive() && !target.isFriendly()){
                        int goldGained = (game.dungeonDifficulty.getDifficulty() +
                            RNG.nextInt(game.dungeonDifficulty.getDifficulty() + 1)) * 40;
                        game.addGold(goldGained);
                    }

                    p.active = false;
                    activeProjectiles.removeValue(p, true);
                    this.cancel();
                } else {
                    // Update projectile position
                    p.x += (dx / distance) * p.speed * delta;
                    p.y += (dy / distance) * p.speed * delta;
                }
            }
        }, 0, 0.02f);
    }

    /**
     * Handles melee animations using interpolation to move the character
     * towards the target and back to its original position.
     */
    private void attackAnimationMelee(Character attacker, Character target){
        final float distanceX = (target.getX() - attacker.getX()) * 0.8f;
        final float distanceY = (target.getY() - attacker.getY()) * 0.8f;
        damageApplied = false;

        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            float elapsed = 0;
            float duration = 0.8f;

            @Override
            public void run() {
                elapsed += 0.02f;
                float progress = elapsed / duration;

                // Phase 1: Lunge towards target
                if (progress < 0.5f) {
                    float subProgress = progress * 2;
                    float x = com.badlogic.gdx.math.Interpolation.exp5Out.apply(0, distanceX, subProgress);
                    float y = com.badlogic.gdx.math.Interpolation.exp5Out.apply(0, distanceY, subProgress);
                    attacker.setVisualOffset(x, y);
                }
                // Phase 2: Impact and return
                else {
                    if (!damageApplied) {
                        attacker.performDirectAttack(target);
                        int actualDealt = target.getDamageTaken();
                        addDamagePopup(actualDealt, target.getX(), target.getY(), attacker.isCritical(), false);
                        attacker.resetCritical();

                        if(!target.isAlive() && !target.isFriendly()){
                            int goldGained = (game.dungeonDifficulty.getDifficulty() +
                                RNG.nextInt(game.dungeonDifficulty.getDifficulty() + 1)) * 40;
                            game.addGold(goldGained);
                        }
                        damageApplied = true;
                    }

                    if (progress < 1.0f) {
                        float subProgress = (progress - 0.5f) * 2;
                        attacker.setVisualOffset(
                            com.badlogic.gdx.math.Interpolation.exp5In.apply(distanceX, 0, subProgress),
                            com.badlogic.gdx.math.Interpolation.exp5In.apply(distanceY, 0, subProgress)
                        );
                    } else {
                        attacker.setVisualOffset(0, 0);
                        this.cancel();
                    }
                }
            }
        }, 0, 0.02f);
    }

    /**
     * Dispatches specialized animations for unique character abilities.
     */
    public void startAbilityAnimation(AbilityStrategy ability, Character target, Character attacker) {
        switch(ability.getId()){
            case "healer_heal":
                healAbilityAnimation(target);
                break;
            case "tank_stun":
                stunAbilityAnimation(target, attacker);
                break;
            case "archer_multishot":
                multishotAbilityAnimation(target, attacker);
                break;
        }
    }

    /**
     * Logic for Archer's multishot - fires multiple projectiles in sequence.
     */
    private void multishotAbilityAnimation(final Character target, final Character attacker) {
        fireSingleMultishot(attacker, target, 1, 3);
    }

    private void fireSingleMultishot(final Character attacker, final Character target, final int currentShot, final int totalShots) {
        final Projectile p = new Projectile(attacker.getProjectileTexture(), attacker.getX(), attacker.getY(), target.getX(), target.getY());
        activeProjectiles.add(p);

        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                float delta = 0.04f;
                float dx = p.targetX - p.x;
                float dy = p.targetY - p.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < 25f) {
                    attacker.performDirectAttack(target);
                    int actualDealt = target.getDamageTaken();
                    addDamagePopup(actualDealt, target.getX(), target.getY(), attacker.isCritical(), false);
                    attacker.resetCritical();

                    p.active = false;
                    activeProjectiles.removeValue(p, true);
                    this.cancel();

                    if (!target.isAlive() && !target.isFriendly()){
                        int goldGained = (game.dungeonDifficulty.getDifficulty() +
                            RNG.nextInt(game.dungeonDifficulty.getDifficulty() + 1)) * 40;
                        game.addGold(goldGained);
                        return;
                    }

                    // Chain next shot if target survives
                    if (currentShot < totalShots) {
                        fireSingleMultishot(attacker, target, currentShot + 1, totalShots);
                    }
                } else {
                    p.x += (dx / distance) * p.speed * delta;
                    p.y += (dy / distance) * p.speed * delta;
                }
            }
        }, 0, 0.02f);
    }

    private void healAbilityAnimation(Character target) {
        addDamagePopup(target.getHealedAmount(), target.getX(), target.getY(), false, true);
    }

    private void stunAbilityAnimation(Character target, Character attacker) {
        // Similar lunge logic to melee attack but specifically for stun-based effects
        final float distanceX = (target.getX() - attacker.getX()) * 0.8f;
        final float distanceY = (target.getY() - attacker.getY()) * 0.8f;
        stunApplied = false;

        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            float elapsed = 0;
            float duration = 0.8f;

            @Override
            public void run() {
                elapsed += 0.02f;
                float progress = elapsed / duration;

                if (progress < 0.5f) {
                    float subProgress = progress * 2;
                    float x = com.badlogic.gdx.math.Interpolation.exp5Out.apply(0, distanceX, subProgress);
                    float y = com.badlogic.gdx.math.Interpolation.exp5Out.apply(0, distanceY, subProgress);
                    attacker.setVisualOffset(x, y);
                }
                else {
                    if (!stunApplied) stunApplied = true;

                    if (progress < 1.0f) {
                        float subProgress = (progress - 0.5f) * 2;
                        attacker.setVisualOffset(
                            com.badlogic.gdx.math.Interpolation.exp5In.apply(distanceX, 0, subProgress),
                            com.badlogic.gdx.math.Interpolation.exp5In.apply(distanceY, 0, subProgress)
                        );
                    } else {
                        attacker.setVisualOffset(0, 0);
                        this.cancel();
                    }
                }
            }
        }, 0, 0.02f);
    }
}
