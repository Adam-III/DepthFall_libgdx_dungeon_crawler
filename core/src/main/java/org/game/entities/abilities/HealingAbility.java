package org.game.entities.abilities;

import com.badlogic.gdx.utils.Array;
import org.game.entities.base.Character;


/**
 * Concrete strategy for a healing spell.
 * Restores a percentage of the target's maximum health.
 */
public class HealingAbility implements AbilityStrategy {
    @Override
    public void execute(Character user, Character target, Array<Character> allies, Array<Character> enemies) {
        // Heal for 10% of max HP, but at least 10 HP
        int healPower = Math.max(10, (int)(target.getMaxHP() * 0.10));
        target.heal(healPower);
    }

    @Override public String getId(){ return "healer_heal"; }
    @Override public String getName() { return "Holy Light"; }
    @Override public int getManaCost() { return 10; }
    @Override public String getDescription() { return "Heals an ally for a percentage of their max health. Costs 10 Mana."; }
    @Override public boolean targetsAlly() { return true; }
}
