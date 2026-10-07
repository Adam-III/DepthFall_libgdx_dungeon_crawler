package org.game.entities.factories;

import org.game.entities.base.Character;
import org.game.entities.friendly.*;

public class FriendlyFactory {
    public Character createNewCharacterByName(String name) {
        return switch (name) {
            case "Swordsman" -> new Swordsman();
            case "Mage" -> new Mage();
            case "Archer" -> new Archer();
            case "Healer" -> new Healer();
            case "Tank" -> new Tank();
            case "Knight" -> new Knight();
            default -> null;
        };
    }

}
