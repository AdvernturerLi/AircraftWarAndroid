package edu.hitsz.aircraft;

import edu.hitsz.aircraft.shoot.CircularShoot;
import edu.hitsz.aircraft.shoot.StraightShoot;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

public class BossEnemy extends AbstractAircraft{
    private int shootNum = 20;
    private int power = 10;
    private int shootInterval = 4000;
    private int shootTimer = 0;
    private int bulletSpeed = 10;
    private int maxHp; // 最大生命值

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.maxHp = hp; // 记录最大生命值
        this.shootStrategy = new CircularShoot();
        this.shootDirection = 1;
    }

    /**
     * 获取最大生命值
     */
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public void forward() {
        // 调用父类方法，实现边界反转左右移动
        super.forward();

        // 运动轨迹：悬浮于界面上方左右移动
        // 限制 Boss 的 Y 轴位置，避免它下移太多
        if (getLocationY() > Main.WINDOW_HEIGHT * 0.15) {
            setLocation(getLocationX(), (int)(Main.WINDOW_HEIGHT * 0.15));
        }

        // 更新计时器，假设 timeInterval = 40ms
        shootTimer += 40;
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        // 检查射击计时器是否归零
        if (shootTimer >= shootInterval) {
            shootTimer = 0; // 重置计时器
            // 使用策略模式执行射击
            res.addAll(this.shootStrategy.executeShoot(this, this.shootDirection, this.power));
        }
        return res;
    }

}
