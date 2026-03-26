package edu.hitsz.application.difficulty;

/**
 * 普通难度
 */
public class NormalDifficulty extends DifficultyTemplate {

    @Override
    protected void setEnemyMaxNumber() {
        enemyMaxNumber = 6;  // 中等敌机数量
    }

    @Override
    protected void setHeroHp() {
        heroHp = 800;  // 中等生命值
    }

    @Override
    protected void setShootDuration() {
        shootDuration = 416;  // 中等射击频率 (26 * 16ms)
    }

    @Override
    protected void setEnemyHp() {
        mobEnemyHp = 40;      // 敌机生命值中等
        eliteEnemyHp = 80;
        superEliteEnemyHp = 120;
        bossEnemyHp = 300;
    }

    @Override
    protected void setPropDuration() {
        propDuration = 5000;  // 道具持续时间中等
    }

    @Override
    protected void setDynamicDifficulty() {
        increaseDifficultyOverTime = true;   // 随时间增加难度
        bossHpIncrease = false;              // Boss生命值不增长
    }
}
