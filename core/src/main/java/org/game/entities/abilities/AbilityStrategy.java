package org.game.entities.abilities;

import com.badlogic.gdx.utils.Array;
import org.game.entities.base.Character;


/**
 * Interface defining the structure for all combat abilities.
 * Uses the Strategy Pattern to allow dynamic behavior for different skills.
 */
public interface AbilityStrategy {
    /**
     * Executes the skill logic.
     * @param user The character casting the ability.
     * @param target The chosen target for the ability.
     * @param allies List of the caster's teammates.
     * @param enemies List of the opposing team.
     */
    void execute(Character user, Character target, Array<Character> allies, Array<Character> enemies);

    String getId();
    String getName();
    int getManaCost();
    String getDescription();
    boolean targetsAlly(); // Returns true if the skill is helpful, false if it's an attack
}
