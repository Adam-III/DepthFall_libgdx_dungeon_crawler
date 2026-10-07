package org.game.world.objects;

public class Chest {
    private float x, y, width, height;
    private int goldAmount;
    private boolean isLooted = false;

    public Chest(float x, float y, int difficulty) {
        this.x = x;
        this.y = y;
        this.width = 64;
        this.height = 64;
        this.goldAmount = (500 + (int)(Math.random() * 100)) * difficulty;
    }

    public boolean containsMouse(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    // Getters and Setters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getGoldAmount() { return goldAmount; }
    public boolean isLooted() { return isLooted; }
    public void setLooted(boolean looted) { isLooted = looted; }
}
