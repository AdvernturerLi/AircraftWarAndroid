package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

public class BloodProp extends AbstractProp {

    // 恢复的血量值，可以根据游戏难度调整
    private final int recoverHp = 30;

    public BloodProp(int locationX, int locationY, int speedX, int speedY) {
        // 调用父类的构造函数来初始化位置和速度
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 实现父类的抽象方法，定义加血道具的效果
     * @param heroAircraft 英雄机对象
     */
    @Override
    public void effect(HeroAircraft heroAircraft) {
        System.out.println("BloodSupply active!");
        // 增加英雄机的生命值
        heroAircraft.decreaseHp(-this.recoverHp);
        // 调用 vanish() 方法让道具从游戏中消失
        this.vanish();
    }
}