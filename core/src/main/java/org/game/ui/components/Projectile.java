package org.game.ui.components;

import com.badlogic.gdx.graphics.Texture;

public class Projectile {
    public Texture texture;
    public float x, y;
    public float targetX, targetY;
    public float speed = 800f; // Pixels per second
    public boolean active = true;

    public Projectile(Texture tex, float startX, float startY, float tX, float tY) {
        this.texture = tex;
        this.x = startX;
        this.y = startY;
        this.targetX = tX;
        this.targetY = tY;
    }
}
