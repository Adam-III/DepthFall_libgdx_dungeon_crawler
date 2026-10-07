package org.game.entities.base;

import com.badlogic.gdx.graphics.Texture;
import org.game.entities.types.AttackType;
import org.game.entities.types.EnemyType;

import java.util.function.BiFunction;


public class EnemyBlueprint {
    public String name;
    public int cost, minFloor, maxFloor;
    public AttackType type;
    public EnemyType enemyType;
    public Texture texture;
    public BiFunction<EnemyBlueprint, Integer, Character> constructor;

    public int hp, hpInc, atk, atkInc, speed;
    public float defense;

    public EnemyBlueprint(String name, int cost, int minFloor, int maxFloor, int hp, int hpInc,
                          int atk, int atkInc, int speed, float defense, Texture texture,
                          EnemyType eType, AttackType aType, BiFunction<EnemyBlueprint,
            Integer, Character> constructor) {
        this.name = name; this.cost = cost; this.minFloor = minFloor; this.maxFloor = maxFloor;
        this.hp = hp; this.hpInc = hpInc; this.atk = atk; this.atkInc = atkInc;
        this.speed = speed; this.defense = defense; this.texture = texture;
        this.enemyType = eType; this.type = aType; this.constructor = constructor;
    }
}

