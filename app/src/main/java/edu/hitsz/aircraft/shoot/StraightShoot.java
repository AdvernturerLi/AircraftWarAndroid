package edu.hitsz.aircraft.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet; // 假设 HeroBullet 和 EnemyBullet 都继承 BaseBullet
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 直射策略：单颗子弹垂直发射 (EliteEnemy, 默认HeroAircraft)
 */
public class StraightShoot implements ShootStrategy {
    @Override
    public List<BaseBullet> executeShoot(AbstractAircraft aircraft, int direction, int power) {
        List<BaseBullet> res = new LinkedList<>();

        int x = aircraft.getLocationX();
        // 确保子弹从飞机中心稍微向前或向后一点发射
        int y = aircraft.getLocationY() + direction * 10;
        // 子弹速度应在飞机的基准速度基础上加上方向因子
        int speedY = aircraft.getSpeedY() + direction * 25;
        int speedX = 0; // 默认垂直发射

        // 根据方向判断子弹类型 (敌机子弹向下，英雄机子弹向上)
        BaseBullet bullet;
        if (direction > 0) { // 敌机向下
            bullet = new EnemyBullet(x, y, speedX, speedY, power);
        } else { // 英雄机向上
            bullet = new HeroBullet(x, y, speedX, speedY, power);
        }

        res.add(bullet);
        return res;
    }
}