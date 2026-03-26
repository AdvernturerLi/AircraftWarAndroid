package edu.hitsz.prop;

public class FirePropFactory implements PropFactory{
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new FireProp(locationX, locationY, 0, 10);
    }
}
