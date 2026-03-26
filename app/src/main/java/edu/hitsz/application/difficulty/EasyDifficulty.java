package edu.hitsz.application.difficulty;

/**
 * 简单难度
 */
public class EasyDifficulty extends DifficultyTemplate {

    @Override
    protected void setEnemyMaxNumber() {
        enemyMaxNumber = 4;  // 减少敌机数量，让屏幕不拥挤
    }

    @Override
    protected void setHeroHp() {
        heroHp = 1000;  // 适中的生命值
    }

    @Override
    protected void setShootDuration() {
        shootDuration = 480;  // 较慢的射击频率 (30 * 16ms)
    }

    @Override
    protected void setEnemyHp() {
        mobEnemyHp = 20;      // 敌机生命值极低，容易击落
        eliteEnemyHp = 40;
        superEliteEnemyHp = 60;
        bossEnemyHp = 150;
    }

    @Override
    protected void setPropDuration() {
        propDuration = 8000;  // 道具持续时间很长
    }

    @Override
    protected void setDynamicDifficulty() {
        increaseDifficultyOverTime = false;  // 不随时间增加难度
        bossHpIncrease = false;              // Boss生命值不增长
    }
}
