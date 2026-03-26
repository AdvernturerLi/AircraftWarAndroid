package edu.hitsz.prop;
// 抽象工厂接口：定义了创建道具的标准方法
public interface PropFactory {
    /**
     * 创建并返回一个抽象道具对象
     * 道具的位置通常由被击毁的敌机决定
     * @param locationX 道具生成X坐标
     * @param locationY 道具生成Y坐标
     * @return AbstractProp
     */
    AbstractProp createProp(int locationX, int locationY);
}
