package edu.hitsz.aircraft.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 散射策略：同时发射3颗子弹，呈扇形 (SuperEliteEnemy, 火力道具)
 */
public class ScatterShoot implements ShootStrategy {
    @Override
    public List<BaseBullet> executeShoot(AbstractAircraft aircraft, int direction, int power) {
        List<BaseBullet> res = new LinkedList<>();
        int deltaSpeedX = 3; // 子弹横向速度增量

        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction * 10;
        
        // 子弹纵向速度：在飞机速度基础上加上方向因子。
        // 注意：英雄机向上(direction=-1)，敌机向下(direction=1)
        int speedY = aircraft.getSpeedY() + direction * 20;

        // 根据方向判断子弹类型
        if (direction > 0) { // 敌机向下 (direction = 1)
            // 1. 中间子弹
            res.add(new EnemyBullet(x, y, 0, speedY, power));
            // 2. 左边子弹 (向左下方飞)
            res.add(new EnemyBullet(x, y, -deltaSpeedX, speedY, power));
            // 3. 右边子弹 (向右下方飞)
            res.add(new EnemyBullet(x, y, deltaSpeedX, speedY, power));
        } else { // 英雄机向上 (direction = -1)
            // 1. 中间子弹
            res.add(new HeroBullet(x, y, 0, speedY, power));
            // 2. 左边子弹 (向左上方飞)
            res.add(new HeroBullet(x, y, -deltaSpeedX, speedY, power));
            // 3. 右边子弹 (向右上方飞)
            res.add(new HeroBullet(x, y, deltaSpeedX, speedY, power));
        }

        return res;
    }
}
