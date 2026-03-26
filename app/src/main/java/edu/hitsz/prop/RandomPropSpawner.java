package edu.hitsz.prop;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomPropSpawner {

    // 存储所有具体工厂
    private final List<PropFactory> factoryList;
    private final Random random = new Random();

    public RandomPropSpawner() {
        // 初始化时，注册所有具体道具工厂实例
        this.factoryList = Arrays.asList(
                new BloodPropFactory(), // 可以显式向下转型
                new FirePropFactory(),
                new SuperFirePropFactory(),
                new BombPropFactory()
        );
    }

    private final List<Double> rateList = Arrays.asList(0.4, 0.6, 0.8, 1.0);

    /**
     * 随机选择一个道具工厂来创建道具（假设等概率）
     * @param locationX 道具生成X坐标
     * @param locationY 道具生成Y坐标
     * @return AbstractProp
     */
    public AbstractProp spawnProp(int locationX, int locationY) {
        double select = random.nextDouble();
        int index = 0;

        for (int i = 0; i < 4; i++) {
            if (select < rateList.get(i)) {
                index = i;
                break;
            }
        }

        // 增加偏移逻辑，为多道具掉落增加随机偏移，防止堆叠
        int offsetX = random.nextInt(40) - 20;
        int offsetY = random.nextInt(40) - 20;

        // 调用选中的工厂来创建道具
        return factoryList.get(index).createProp(locationX + offsetX, locationY + offsetY);
    }

    /**
     * 随机生成多个道具，并在初始位置上增加随机偏移量
     * @param locationX 初始X坐标
     * @param locationY 初始Y坐标
     * @param count 掉落的道具数量
     * @return List<AbstractProp>
     */
    public List<AbstractProp> spawnMultipleProps(int locationX, int locationY, int count) {
        List<AbstractProp> droppedProps = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            // 1. 计算随机偏移量 (例如，最大偏移范围 +/- 40 像素)
            int offsetX = random.nextInt(81) - 40; // -40 到 40 之间
            int offsetY = random.nextInt(81) - 40; // -40 到 40 之间

            // 2. 计算新的初始位置
            int newX = locationX + offsetX;
            int newY = locationY + offsetY;

            // 3. 随机选择道具工厂 (使用旧的 spawnProp 逻辑)
            AbstractProp prop = this.spawnProp(newX, newY);

            // 【可选增强】：为道具添加随机的水平速度
            // 让道具不是垂直下落，而是分散飞出，效果更像“爆炸”
            int randomSpeedX = random.nextInt(6) - 3; // -3 到 3 之间的随机水平速度
            prop.setSpeedX(randomSpeedX); // 假设 AbstractFlyingObject/AbstractProp 有 setSpeedX 方法

            droppedProps.add(prop);
        }
        return droppedProps;
    }
}