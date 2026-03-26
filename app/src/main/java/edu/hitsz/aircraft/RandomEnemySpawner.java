package edu.hitsz.aircraft;

import edu.hitsz.application.Game;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomEnemySpawner {

    // 存储所有具体工厂
    private final List<AircraftFactory> factoryList;
    // 存储每个工厂对应的概率（累计概率）
    // 假设 Mob: 80%, Elite: 15%, Super:5%
    private final List<Double> rateList = Arrays.asList(0.6, 0.85, 1.0); // 0.7为Mob的界限，1.0为Elite的界限

    private final Random random = new Random();
    private final Game game;

    public RandomEnemySpawner(int mobHp, int eliteHp, int superEliteHp, Game game) {
        this.game = game;
        // 初始化工厂，传入对应的生命值和Game引用
        this.factoryList = Arrays.asList(
                new MobEnemyFactory(mobHp, game),
                new EliteEnemyFactory(eliteHp, game),
                new SuperEliteEnemyFactory(superEliteHp, game)
        );
    }

    /**
     * 根据概率随机选择一个工厂来创建敌机
     * @return AbstractAircraft 
     */
    public AbstractAircraft spawnEnemy() {
        double select = random.nextDouble();

        AircraftFactory selectedFactory;
        int width;
        // 根据随机数和累计概率选择工厂
        if (select < rateList.get(0)) { // MobEnemy
            selectedFactory = factoryList.get(0);
            width = ImageManager.MOB_ENEMY_IMAGE.getWidth();
        } else if (select < rateList.get(1)) { // EliteEnemy
            selectedFactory = factoryList.get(1);
            width = ImageManager.ELITE_ENEMY_IMAGE.getWidth();
        } else { // EliteEnemy
            selectedFactory = factoryList.get(2);
            width = ImageManager.ELITE_ENEMY_IMAGE.getWidth();
        }

        // 计算随机X坐标
        int minX = width / 2;
        int maxX = Main.WINDOW_WIDTH - width / 2;
        int randomX = random.nextInt(maxX - minX + 1) + minX;

        // 随机Y坐标 (从顶部生成)
        int randomY = (int) (random.nextDouble() * Main.WINDOW_HEIGHT * 0.05);

        // 调用选中的工厂，传入计算好的位置
        return selectedFactory.createAircraft(randomX, randomY);

    }
}