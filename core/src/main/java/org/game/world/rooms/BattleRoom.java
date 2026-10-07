package org.game.world.rooms;

public class BattleRoom extends Room {

    public BattleRoom(int x, int y)  {
        super(x, y, RoomType.MAIN_PATH);
    }

    @Override
    public void onEnter() {
    }
}
