package edu.hitsz.aircraft.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 环射策略：同时发射多颗子弹，呈环形 (BossEnemy, 超级火力道具)
 * 注意：由于 BossEnemy.java 中环射逻辑使用了固定的速度和子弹数量，
 * 这里简化实现，固定 Boss 的环形参数。
 */
public class CircularShoot implements ShootStrategy {
    // 环形参数
    private static final int SHOOT_NUM = 20; // 固定的子弹数量
    private static final int BULLET_SPEED = 20; // 固定的子弹速度

    @Override
    public List<BaseBullet> executeShoot(AbstractAircraft aircraft, int direction, int power) {
        // Boss 机的环射逻辑通常不需要依赖方向（方向只影响子弹类型，不影响环形散开方式）
        // 且其子弹速度通常是固定的
        List<BaseBullet> res = new LinkedList<>();

        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();

        double deltaAngle = 360.0 / SHOOT_NUM; // 每颗子弹的角度间隔

        for (int i = 0; i < SHOOT_NUM; i++) {
            double angle = i * deltaAngle;
            // 计算子弹的 x, y 速度分量
            int speedX = (int) (BULLET_SPEED * Math.cos(Math.toRadians(angle)));
            int speedY = (int) (BULLET_SPEED * Math.sin(Math.toRadians(angle)));

            // 子弹初始位置在飞机中心
            if (direction > 0) {
                res.add(new EnemyBullet(x, y, speedX, speedY, power));
            } else {
                res.add(new HeroBullet(x, y, speedX, speedY, power));
            }
        }
        return res;
    }
}