package edu.hitsz.application.difficulty;

/**
 * 困难难度
 */
public class HardDifficulty extends DifficultyTemplate {

    @Override
    protected void setEnemyMaxNumber() {
        enemyMaxNumber = 12;  // 较多的敌机数量
    }

    @Override
    protected void setHeroHp() {
        heroHp = 800;  // 较低的生命值
    }

    @Override
    protected void setShootDuration() {
        shootDuration = 560;  // 较慢的射击频率
    }

    @Override
    protected void setEnemyHp() {
        mobEnemyHp = 60;      // 敌机生命值较高
        eliteEnemyHp = 90;
        superEliteEnemyHp = 120;
        bossEnemyHp = 300;
    }

    @Override
    protected void setPropDuration() {
        propDuration = 2000;  // 道具持续时间较短
    }

    @Override
    protected void setDynamicDifficulty() {
        increaseDifficultyOverTime = true;   // 随时间增加难度
        bossHpIncrease = true;               // Boss生命值随次数增长
    }
}