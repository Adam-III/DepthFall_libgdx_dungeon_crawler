package org.game.entities.abilities;

import com.badlogic.gdx.utils.Array;
import org.game.entities.base.Character;

public class MultishotAbility implements AbilityStrategy{
    @Override
    public void execute(Character user, Character target, Array<Character> allies, Array<Character> enemies) {
        // Damage is applied per projectile by AnimationManager when each arrow hits,
        // so there is no instant effect here.
    }

    @Override public String getId(){ return "archer_multishot"; }
    @Override public String getName() { return "Multishot"; }
    @Override public int getManaCost() { return 20; }
    @Override public String getDescription() { return "Shoots three projectiles at an enemy. Costs 20 Mana."; }
    @Override public boolean targetsAlly() { return false; }
}
