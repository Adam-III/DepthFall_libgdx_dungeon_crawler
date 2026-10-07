package org.game.ui.components;

import com.badlogic.gdx.math.MathUtils;

public class DamagePopup {
    public String text;
    public float x, y;
    public float alpha = 1.0f;
    public float lifeTime = 1.0f;
    public float elapsed = 0;
    public boolean isCritical = false, isHeal = false;

    public DamagePopup(String text, float startX, float startY, boolean isCritical, boolean isHeal) {
        this.text = text;
        this.x = startX + 16 + MathUtils.random(-20, 20);
        this.y = startY + 32;
        this.isCritical = isCritical;
        this.isHeal = isHeal;
    }

    public void update(float delta) {
        elapsed += delta;
        y += 50 * delta;
        alpha = 1.0f - (elapsed / lifeTime);
    }
}
