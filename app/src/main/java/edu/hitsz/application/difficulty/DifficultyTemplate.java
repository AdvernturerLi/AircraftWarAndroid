package edu.hitsz.application.difficulty;


import edu.hitsz.application.Game;

/**
 * 难度模板抽象类
 */
public abstract class DifficultyTemplate {
    protected int enemyMaxNumber;
    protected int heroHp;
    protected int shootDuration;
    protected int mobEnemyHp;
    protected int eliteEnemyHp;
    protected int superEliteEnemyHp;
    protected int bossEnemyHp;
    protected int propDuration;

    // 是否随时间增加难度
    protected boolean increaseDifficultyOverTime;
    // Boss是否随出现次数增加生命值
    protected boolean bossHpIncrease = false;
    /**
     * 模板方法：初始化游戏难度设置
     */
    public final void setupDifficulty() {
        setEnemyMaxNumber();
        setHeroHp();
        setShootDuration();
        setEnemyHp();
        setPropDuration();
        setDynamicDifficulty();
    }

    // 抽象方法，由具体子类实现
    protected abstract void setEnemyMaxNumber();
    protected abstract void setHeroHp();
    protected abstract void setShootDuration();
    protected abstract void setEnemyHp();
    protected abstract void setPropDuration();
    protected abstract void setDynamicDifficulty();

    /**
     * Boss敌机生命值增长（在困难模式下调用）
     */
    public int getBossHp(int bossSpawnCount) {
        if (bossHpIncrease) {
            // 每次Boss出现，生命值增加10%
            return (int) (bossEnemyHp * (1 + 0.1 * bossSpawnCount));
        }
        return bossEnemyHp;
    }

    // Getter方法
    public int getEnemyMaxNumber() { return enemyMaxNumber; }
    public int getHeroHp() { return heroHp; }
    public int getShootDuration() { return shootDuration; }
    public int getMobEnemyHp() { return mobEnemyHp; }
    public int getEliteEnemyHp() { return eliteEnemyHp; }
    public int getSuperEliteEnemyHp() { return superEliteEnemyHp; }
    public int getPropDuration() { return propDuration; }
    public boolean shouldIncreaseDifficultyOverTime() { return increaseDifficultyOverTime; }
    public boolean shouldBossHpIncrease() { return bossHpIncrease; }
}
