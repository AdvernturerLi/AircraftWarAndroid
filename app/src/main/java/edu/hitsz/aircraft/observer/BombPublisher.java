package edu.hitsz.aircraft.observer;
/**
 * 炸弹道具的发布者接口 (Subject)
 */
public interface BombPublisher {
    /**
     * 注册观察者
     */
    void addSubscriber(BombSubscriber subscriber);

    /**
     * 移除观察者
     */
    void removeSubscriber(BombSubscriber subscriber);

    /**
     * 通知所有观察者
     * @return 消除的敌机和子弹的总分数
     */
    int notifySubscribers();
}
