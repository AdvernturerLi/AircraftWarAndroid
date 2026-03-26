package edu.hitsz.aircraft;

import edu.hitsz.application.Game;

public class MobEnemyFactory implements AircraftFactory {
    private final int hp;
    private final Game game;

    public MobEnemyFactory(int hp, Game game) {
        this.hp = hp;
        this.game = game;
    }

    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY) {
        // 降低基础速度以适应 60fps 刷新率 (原本 25 太快了)
        int baseSpeed = 7; 
        int speedIncrement = game != null ? game.getEnemySpeedIncrement() : 0;
        return new MobEnemy(locationX, locationY, 0, baseSpeed + speedIncrement, hp);
    }
}
