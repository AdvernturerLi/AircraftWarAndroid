package edu.hitsz.aircraft.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

/**
 * 射击策略接口
 */
public interface ShootStrategy {
    /**
     * 执行射击
     * @param aircraft 射击的飞机对象
     * @param direction 子弹的飞行方向 (1:向下, -1:向上)
     * @param power 子弹的伤害
     * @return 射击产生的子弹列表
     */
    List<BaseBullet> executeShoot(AbstractAircraft aircraft, int direction, int power);
}
