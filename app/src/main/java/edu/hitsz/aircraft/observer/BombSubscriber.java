package edu.hitsz.aircraft.observer;
/**
 * 炸弹道具的订阅者接口 (Observer)
 * 所有能被炸弹影响的对象都应实现此接口
 */
public interface BombSubscriber {
    /**
     * 接收到炸弹生效的通知并执行相应的动作
     * @return 被清除的敌机或子弹所对应的分数，如果不是敌机或不需要计算分数则返回 0
     */
    int update();
}
