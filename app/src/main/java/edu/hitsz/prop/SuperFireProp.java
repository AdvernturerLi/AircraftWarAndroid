package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.shoot.CircularShoot;

public class SuperFireProp extends AbstractProp {

    public SuperFireProp(int locationX, int locationY, int speedX, int speedY) {
        // 调用父类的构造函数来初始化位置和速度
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 实现父类的抽象方法，定义炸弹道具的效果
     * @param heroAircraft 英雄机对象
     */
    @Override
    public void effect(HeroAircraft heroAircraft) {
        System.out.println("FireSupply active!");
        // 功能: 环射
        heroAircraft.setShootStrategy(new CircularShoot());
        // 调用 vanish() 方法让道具从游戏中消失
        this.vanish();
    }
}