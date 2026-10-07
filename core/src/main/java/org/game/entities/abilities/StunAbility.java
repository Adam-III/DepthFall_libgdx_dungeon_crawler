package org.game.entities.abilities;

import com.badlogic.gdx.utils.Array;
import org.game.entities.base.Character;


public class StunAbility implements AbilityStrategy{
    @Override
    public void execute(Character user, Character target, Array<Character> allies, Array<Character> enemies) {
        target.stun();
    }

    @Override public String getId(){ return "tank_stun"; }
    @Override public String getName() { return "Heavy Blow"; }
    @Override public int getManaCost() { return 20; }
    @Override public String getDescription() { return "Stuns enemy for one round. Costs 20 Mana."; }
    @Override public boolean targetsAlly() { return false; }
}

