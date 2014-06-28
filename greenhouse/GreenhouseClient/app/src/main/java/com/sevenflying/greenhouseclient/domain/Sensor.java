package com.sevenflying.greenhouseclient.domain;

/**
 * Created by user on 27/06/2014.
 */
public class Sensor {

    private String name;
    private String value;
    private String unit;

    public Sensor(String name, String value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
