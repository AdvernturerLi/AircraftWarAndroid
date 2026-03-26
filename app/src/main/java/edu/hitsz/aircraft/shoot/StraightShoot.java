package edu.hitsz.aircraft.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet; 
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
        
        // 子弹纵向速度：在飞机纵向速度基础上加上方向因子 * 相对速度。
        // 英雄机向上(direction=-1)，其子弹速度应为 - (自身速度 + 20) 左右。
        // 敌机向下(direction=1)，其子弹速度应为 (自身速度 + 20) 左右。
        int speedY = aircraft.getSpeedY() + direction * 20;
        int speedX = 0; 

        // 根据方向判断子弹类型 (敌机子弹向下，英雄机子弹向上)
        if (direction > 0) { // 敌机向下
            res.add(new EnemyBullet(x, y, speedX, speedY, power));
        } else { // 英雄机向上
            res.add(new HeroBullet(x, y, speedX, speedY, power));
        }

        return res;
    }
}
