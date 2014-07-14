package com.sevenflying.greenhouseclient.domain;

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

	public Sensor(String name, String pinId, SensorType type, long refreshRate, double value, String updatedAt) {
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
        switch (type){
            case 'H':
                this.type = SensorType.HUMIDITY;
                break;
            case 'T':
                this.type = SensorType.TEMPERATURE;
                break;
            case 'L':
                this.type = SensorType.LIGHT;
                break;
            default:
                this.type = SensorType.UNKNOWN;
                break;
        }
    }
    public void setType(SensorType type) {
        this.type = type;
    }

    public SensorType getType() {
        return type;
    }

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
                return R.drawable.humidity_sensor;
            case TEMPERATURE:
                return R.drawable.temperature_sensor;
            case LIGHT:
                return R.drawable.light_sensor;
            default:
                return R.drawable.sensor;
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

        if (name != null ? !name.equals(sensor.name) : sensor.name != null) return false;
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
}
