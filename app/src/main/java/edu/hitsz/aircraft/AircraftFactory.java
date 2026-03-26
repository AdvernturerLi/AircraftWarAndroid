package edu.hitsz.aircraft;
// 抽象工厂接口：定义了创建飞机的标准方法
public interface AircraftFactory {
    /**
     * 创建并返回一个抽象飞机对象
     * @param locationX 飞机的X坐标
     * @param locationY 飞机的Y坐标
     * @return AbstractAircraft
     */
    AbstractAircraft createAircraft(int locationX, int locationY);
}
