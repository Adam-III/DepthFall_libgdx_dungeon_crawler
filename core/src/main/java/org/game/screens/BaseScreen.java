package org.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.game.coreGame.GameManager;

/**
 * An abstract base class for all game screens.
 * Implements common LibGDX Screen lifecycle methods and provides a
 * built-in screen transition (fade-in/fade-out) system.
 */
public abstract class BaseScreen implements Screen {
    protected final GameManager game;

    // Transition variables
    protected float fadeAlpha = 1;      // Current transparency of the fade overlay
    protected float fadeSpeed = 2.5f;   // How fast the fade happens
    protected boolean isFading = true;  // Flag to check if a transition is active
    protected boolean fadingOut = false; // True = going to black, False = revealing screen
    private Texture blackOverlay;
    protected boolean disposed = false; // Set once dispose() has run

    public BaseScreen(GameManager game) {
        this.game = game;

        // Generate a 1x1 black pixel texture programmatically to use for fade effects
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fill();
        blackOverlay = new Texture(pixmap);
        pixmap.dispose(); // Clean up pixmap as the texture is now in VRAM
    }

    /**
     * Initiates the fade-out process (screen turns black).
     * Typically called when switching to another screen.
     */
    protected void startTransition() {
        isFading = true;
        fadingOut = true;
        fadeAlpha = 0;
    }

    /**
     * Handles the logic for updating transition transparency over time.
     * @param delta Time passed since the last frame.
     * @param speed The desired speed of the fade.
     */
    protected void updateTransition(float delta, float speed) {
        this.fadeSpeed = speed;
        if (!isFading) return;

        if (fadingOut) {
            fadeAlpha += delta * fadeSpeed;
            if (fadeAlpha >= 1) {
                fadeAlpha = 1;
                onFadeOutComplete(); // Trigger screen switch or logic
                fadingOut = false;
            }
        } else {
            // Fade-in logic (revealing the screen)
            fadeAlpha -= delta * fadeSpeed;
            if (fadeAlpha <= 0) {
                fadeAlpha = 0;
                isFading = false;
            }
        }
    }

    /**
     * Renders the black overlay based on current fadeAlpha.
     * Must be called inside a SpriteBatch.begin() / end() block.
     */
    protected void drawTransition() {
        if (disposed) return; // Screen may be disposed mid-frame by a state change
        if (isFading || fadeAlpha > 0) {
            game.spriteBatch.setColor(1, 1, 1, fadeAlpha);
            // Draw the 1x1 texture stretched to cover the internal resolution (960x540)
            game.spriteBatch.draw(blackOverlay, 0, 0, 960, 540);
            game.spriteBatch.setColor(1, 1, 1, 1); // Reset batch color to opaque
        }
    }

    /**
     * Abstract method called automatically when the screen is fully black.
     * Use this to change screens or reset game states.
     */
    public abstract void onFadeOutComplete();

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        blackOverlay.dispose(); // Crucial for preventing memory leaks
    }

    @Override
    public void resize(int width, int height) {
        // Ensure the game world scales correctly to new window sizes
        game.viewport.update(width, height, true);
    }

    // LibGDX Screen Interface stubs
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
