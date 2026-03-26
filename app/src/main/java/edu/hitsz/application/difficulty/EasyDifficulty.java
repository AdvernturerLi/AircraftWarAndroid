package edu.hitsz.application.difficulty;

/**
 * 简单难度
 */
public class EasyDifficulty extends DifficultyTemplate {

    @Override
    protected void setEnemyMaxNumber() {
        enemyMaxNumber = 5;  // 较少的敌机数量
    }

    @Override
    protected void setHeroHp() {
        heroHp = 1500;  // 较高的生命值
    }

    @Override
    protected void setShootDuration() {
        shootDuration = 300;  // 较快的射击频率
    }

    @Override
    protected void setEnemyHp() {
        mobEnemyHp = 30;      // 敌机生命值较低
        eliteEnemyHp = 60;
        superEliteEnemyHp = 90;
        bossEnemyHp = 240;
    }

    @Override
    protected void setPropDuration() {
        propDuration = 6000;  // 道具持续时间较长
    }

    @Override
    protected void setDynamicDifficulty() {
        increaseDifficultyOverTime = false;  // 不随时间增加难度
        bossHpIncrease = false;              // Boss生命值不增长
    }
}