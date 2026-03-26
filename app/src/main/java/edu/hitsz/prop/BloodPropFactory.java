package edu.hitsz.prop;

public class BloodPropFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new BloodProp(locationX, locationY, 0, 10);
    }
}