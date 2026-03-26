package edu.hitsz.prop;

public class BombPropFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new BombProp(locationX, locationY, 0, 10);
    }
}
