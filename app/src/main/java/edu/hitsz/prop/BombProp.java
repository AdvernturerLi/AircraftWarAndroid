package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.observer.BombPublisher;
import edu.hitsz.aircraft.observer.BombSubscriber;

import java.util.ArrayList;
import java.util.List;

public class BombProp extends AbstractProp implements BombPublisher {

    private final List<BombSubscriber> subscribers = new ArrayList<>();
    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        // 调用父类的构造函数来初始化位置和速度
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 实现父类的抽象方法，定义炸弹道具的效果
     * @param heroAircraft 英雄机对象
     */
    @Override
    public void effect(HeroAircraft heroAircraft) {
        System.out.println("BombSupply active!");
        // 炸弹生效时，通知所有注册的订阅者，并获取清除敌机的分数
        // 注意：实际的订阅者注册逻辑在 Game.java 中实现 (见步骤 4)
        // int clearedScore = notifySubscribers();
        // heroAircraft.increaseScore(clearedScore); // 假设 heroAircraft 有增加分数的方法

        // 因为 BombProp 现在在 Game.java 中不再持有订阅者列表，
        // 而是由 Game.java 来执行通知和分数累加，
        // 所以这里不再调用 notifySubscribers()，而是将逻辑委托给 Game.java

        // 调用 vanish() 方法让道具从游戏中消失
        this.vanish();
    }

    @Override
    public void addSubscriber(BombSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(BombSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public int notifySubscribers() {
        int totalScore = 0;
        // 遍历列表，通知所有订阅者
        for (BombSubscriber subscriber : subscribers) {
            totalScore += subscriber.update();
        }
        return totalScore;
    }
}