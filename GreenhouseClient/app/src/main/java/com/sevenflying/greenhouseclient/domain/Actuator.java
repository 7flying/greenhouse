package com.sevenflying.greenhouseclient.domain;

import com.sevenflying.greenhouseclient.app.R;

import java.io.Serializable;

/** Actuator class.
 * Created by 7flying on 11/08/2014.
 */
public class Actuator implements Serializable {

    private String name;
    private String pinId;
    private Sensor controlSensor;
    private AlertType compareType;
    private double compareValue;
    private int icon;

    public Actuator(){}

    public  Actuator(String name, String pinId) {
        this.name = name;
        this.pinId = pinId;
        this.controlSensor = null;
        this.compareType = null;
        this.compareType = null;
        icon = R.drawable.ic_launch_orange;
    }

    public Actuator(String name, String pinId, Sensor controlSensor, AlertType compareType,
                    double compareValue)
    {
        this.name = name;
        this.pinId = pinId;
        this.controlSensor = controlSensor;
        this.compareType = compareType;
        this.compareValue = compareValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinId() {
        return pinId;
    }

    public void setPinId(String pinId) {
        this.pinId = pinId;
    }

    public int getIcon() {
        return icon;
    }

    /** Checks whether the Actuator has a control sensor or not.
     * @return boolean     */
    public boolean hasControlSensor() {
        return controlSensor != null;
    }

    public AlertType getCompareType() {
        return compareType;
    }

    public void setCompareType(AlertType compareType) {
        this.compareType = compareType;
    }

    public Sensor getControlSensor() {
        return controlSensor;
    }

    public void setControlSensor(Sensor controlSensor) {
        this.controlSensor = controlSensor;
    }

    public double getCompareValue() {
        return compareValue;
    }

    public void setCompareValue(double compareValue) {
        this.compareValue = compareValue;
    }

    @Override
    public String toString() {
        return "Actuator{" +
                "name='" + name + '\'' +
                ", pinId='" + pinId + '\'' +
                ", controlSensor=" + controlSensor +
                ", compareType=" + compareType +
                ", compareValue=" + compareValue +
                ", icon=" + icon +
                '}';
    }
}
