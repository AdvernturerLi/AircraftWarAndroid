package edu.hitsz.aircraft;

import edu.hitsz.aircraft.shoot.ShootStrategy;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;

import java.util.List;

/**
 * 所有种类飞机的抽象父类：
 * 敌机（BOSS, ELITE, MOB），英雄飞机
 *
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {
    /**
     * 生命值
     */
    protected int maxHp;
    protected int hp;

    // 射击策略
    protected ShootStrategy shootStrategy;
    // 子弹方向 (1:向下, -1:向上)
    protected int shootDirection = 1;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp <= 0){
            hp=0;
            vanish();
        } else if(hp >= maxHp){
            hp = maxHp;
        }
    }

    public int getHp() {
        return hp;
    }

    // 设置射击策略的方法
    public void setShootStrategy(ShootStrategy strategy) {
        this.shootStrategy = strategy;
    }

    // 抽象方法 shoot() 更改为使用策略
    // 英雄机和敌机有不同的 power 和 direction，具体在子类中初始化
    public abstract List<BaseBullet> shoot();

    public void increaseSpeed(int increment) {
        this.speedY += increment;
    }
}


