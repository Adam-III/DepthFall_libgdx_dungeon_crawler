package org.game.world.rooms;

public class Hallway extends Room {

    public Hallway(int x, int y) {
        super(x, y, RoomType.MAIN_PATH);
    }

    @Override
    public void onEnter() {
    }
}
