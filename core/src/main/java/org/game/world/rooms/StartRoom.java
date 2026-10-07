package org.game.world.rooms;

public class StartRoom extends Room {

    public StartRoom(int x, int y) {
        super(x, y, RoomType.START);
    }

    @Override
    public void onEnter() {
    }
}
