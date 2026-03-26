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
        heroHp = 600;  // 较低的生命值，增加挑战
    }

    @Override
    protected void setShootDuration() {
        // 降低数值以增加射击频率 (数值越小，射击越快)
        // 原本 560 太慢了，改为 360 以获得更好的战斗体验
        shootDuration = 360;  
    }

    @Override
    protected void setEnemyHp() {
        mobEnemyHp = 60;      // 敌机生命值较高
        eliteEnemyHp = 120;
        superEliteEnemyHp = 180;
        bossEnemyHp = 400;
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
