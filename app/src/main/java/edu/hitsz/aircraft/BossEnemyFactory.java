package edu.hitsz.aircraft;

import edu.hitsz.application.Game;

public class BossEnemyFactory implements AircraftFactory {
    private final Game game;

    public BossEnemyFactory(Game game) {
        this.game = game;
    }

    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY) {
        int bossSpawnCount = game.getBossSpawnCount();
        // 直接在 Game 中由生成逻辑计算 HP，工厂仅负责创建实例
        // 这里的逻辑已简化，Game 类中会处理具体的 HP 传入
        return new BossEnemy(locationX, locationY, 2, 0, 100);
    }
}
