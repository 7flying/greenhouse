package com.sevenflying.greenhouseclient.domain;

import android.util.Base64;

import com.sevenflying.greenhouseclient.app.R;

import java.io.Serializable;

/** Sensor class
 * Created by 7flying on 10/07/2014.
 */
public class Sensor implements Serializable {

    private String name;
    private String pinId;
	// Rate at which data is gathered from this sensor (ms)
	private long refreshRate;
	private SensorType type;
    // Sensor's last obtained value
    private double value;
    // When the sensor was updated
    private String updatedAt;

	public Sensor(String name, String pinId, SensorType type, long refreshRate, double value,
                  String updatedAt)
    {
		this.name = name;
        this.pinId = pinId;
		this.type = type;
		this.refreshRate = refreshRate;
        this.value = value;
        this.updatedAt = updatedAt;
	}

    public Sensor() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinId() {
        return  pinId;
    }

    public void setPinId(String pinId) {
        this.pinId = pinId;
    }

    public void setType(char type) {
        this.type = SensorType.getType(type);
    }
    public void setType(SensorType type) {
        this.type = type;
    }

    public SensorType getType() {
        return type;
    }

    /** Returns the sensor's refresh rate (in ms)
     * @return
     */
	public long getRefreshRate() {
		return refreshRate;
	}

	public void setRefreshRate(long refreshRate) {
		this.refreshRate = refreshRate;
	}

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getDrawableId() {
        switch (type){
            case HUMIDITY:
                return R.drawable.humidity_sensor_b;
            case TEMPERATURE:
                return R.drawable.temperature_sensor_b;
            case LIGHT:
                return R.drawable.light_sensor_b;
            case PRESSURE:
                return R.drawable.pressure_sensor_b;
            case STEAM:
                return R.drawable.rain_sensor_b;
            default:
                return R.drawable.sensor_b;
        }
    }

    public static int getDrawableIdClearFromType(SensorType type) {
        switch (type){
            case HUMIDITY:
                return R.drawable.humidity_sensor_w;
            case TEMPERATURE:
                return R.drawable.temperature_sensor_w;
            case LIGHT:
                return R.drawable.light_sensor_w;
            case PRESSURE:
                return R.drawable.pressure_sensor_w;
            case STEAM:
                return R.drawable.rain_sensor_w;
            default:
                return R.drawable.sensor_w;
        }
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sensor sensor = (Sensor) o;

        if (pinId != null ? !pinId.equals(sensor.pinId) : sensor.pinId != null) return false;
        if (type != sensor.type) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "name='" + name + '\'' +
                ", pinId='" + pinId + '\'' +
                ", refreshRate=" + refreshRate +
                ", type=" + type +
                ", value=" + value +
                '}';
    }

    public String toStoreString() {
        String ret = "";
        ret += Base64.encodeToString(getPinId().getBytes(), Base64.DEFAULT) + ":";
        ret += Base64.encodeToString(getName().getBytes(), Base64.DEFAULT) + ":";
        ret += Base64.encodeToString(Character.toString(getType().getIdentifier()).getBytes(),
                Base64.DEFAULT);
        return ret;
    }

}
