package org.game.entities.base;

import com.badlogic.gdx.graphics.Texture;
import org.game.entities.types.AttackType;
import org.game.entities.types.EnemyType;

import java.util.function.BiFunction;

public class EnemyBlueprintRanged extends EnemyBlueprint {
    public Texture projectileTexture;

    public EnemyBlueprintRanged(String name, int cost, int minFloor, int maxFloor, int hp, int hpInc,
                                int atk, int atkInc, int speed, float defense, Texture texture,
                                EnemyType eType, Texture projectileTexture, BiFunction<EnemyBlueprint, Integer, Character> constructor) {
        super(name, cost, minFloor, maxFloor, hp, hpInc, atk, atkInc, speed, defense, texture, eType, AttackType.RANGE, constructor);
        this.projectileTexture = projectileTexture;
    }
}
