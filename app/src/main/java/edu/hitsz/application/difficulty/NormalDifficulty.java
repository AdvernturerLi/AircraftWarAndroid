package edu.hitsz.application.difficulty;

/**
 * 普通难度
 */
public class NormalDifficulty extends DifficultyTemplate {

    @Override
    protected void setEnemyMaxNumber() {
        enemyMaxNumber = 8;  // 中等敌机数量
    }

    @Override
    protected void setHeroHp() {
        heroHp = 1000;  // 中等生命值
    }

    @Override
    protected void setShootDuration() {
        shootDuration = 480;  // 中等射击频率
    }

    @Override
    protected void setEnemyHp() {
        mobEnemyHp = 30;      // 敌机生命值中等
        eliteEnemyHp = 90;
        superEliteEnemyHp = 120;
        bossEnemyHp = 280;
    }

    @Override
    protected void setPropDuration() {
        propDuration = 4000;  // 道具持续时间中等
    }

    @Override
    protected void setDynamicDifficulty() {
        increaseDifficultyOverTime = true;   // 随时间增加难度
        bossHpIncrease = false;              // Boss生命值不增长
    }
}