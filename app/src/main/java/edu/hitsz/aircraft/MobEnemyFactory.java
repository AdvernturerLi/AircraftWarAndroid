package edu.hitsz.aircraft;

import edu.hitsz.application.Game;
import edu.hitsz.application.Main;

public class MobEnemyFactory implements AircraftFactory {
    private final int hp;
    private final Game game;

    public MobEnemyFactory(int hp, Game game) {
        this.hp = hp;
        this.game = game;
    }

    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY) {
        int baseSpeed = 25; // 基础速度
        int speedIncrement = game != null ? game.getEnemySpeedIncrement() : 0;
        return new MobEnemy(locationX, locationY, 0, baseSpeed + speedIncrement, hp);
    }
}