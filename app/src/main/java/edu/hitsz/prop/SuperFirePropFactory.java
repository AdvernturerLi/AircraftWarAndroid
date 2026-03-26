package edu.hitsz.prop;

public class SuperFirePropFactory implements PropFactory{
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new SuperFireProp(locationX, locationY, 0, 10);
    }
}
