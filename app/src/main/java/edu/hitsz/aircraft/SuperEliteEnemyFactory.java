package edu.hitsz.aircraft;

import edu.hitsz.application.Game;

public class SuperEliteEnemyFactory implements AircraftFactory{
    private final int hp;
    private final Game game;

    public SuperEliteEnemyFactory(int hp, Game game) {
        this.hp = hp;
        this.game = game;
    }

    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY) {
        int baseSpeed = 25; // 基础速度
        int speedIncrement = game != null ? game.getEnemySpeedIncrement() : 0;
        return new SuperEliteEnemy(locationX, locationY, 0, baseSpeed + speedIncrement, hp);
    }
}
