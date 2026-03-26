package edu.hitsz.aircraft;

import edu.hitsz.aircraft.shoot.ShootStrategy;
import edu.hitsz.aircraft.shoot.StraightShoot;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    private static final int INIT_HP = 1000;

    // 2. 静态实例，改为懒汉式，以确保在 ImageManager 初始化后创建
    private static HeroAircraft instance = null;

    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    // 2. 私有构造函数
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp){
        // 调用父类构造函数进行初始化
        super(locationX, locationY, speedX, speedY, hp);
        // 设置默认策略：直射
        this.shootStrategy = new StraightShoot();
        this.shootDirection = this.direction;
    }

    // 3. 公有静态方法：懒汉式单例，解决 ImageManager 为空的问题
    public static synchronized HeroAircraft getInstance() {
        if (instance == null) {
            // 默认初始位置，在 Game.surfaceChanged 中会重新设置
            int initX = Main.WINDOW_WIDTH / 2;
            int initY = Main.WINDOW_HEIGHT - 200; 
            instance = new HeroAircraft(initX, initY, 0, 0, INIT_HP);
        }
        return instance;
    }

    /**攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 1;

    /**
     * 子弹伤害
     */
    private int power = 30;

    /**
     * 子弹射击方向 (向上发射：1，向下发射：-1)
     */
    private int direction = -1;

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        return this.shootStrategy.executeShoot(this, this.shootDirection, this.power);
    }
    /**
     * 英雄机切换射击策略的方法
     * @param newStrategy 新的射击策略
     */
    public void setShootStrategy(ShootStrategy newStrategy) {
        this.shootStrategy = newStrategy;
    }

    public void reset() {
        this.hp = INIT_HP;
        // 位置由 Game 动态设置
        this.setShootStrategy(new StraightShoot());
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
