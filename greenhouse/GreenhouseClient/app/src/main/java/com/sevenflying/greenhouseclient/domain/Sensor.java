package com.sevenflying.greenhouseclient.domain;

/**
 * Created by 7flying on 10/07/2014.
 */
public class Sensor {

    private String name;
    private String pinId;
	// Rate at which data is gathered from this sensor (ms)
	private long refreshRate;
	private SensorType type;
    // Sensor's last obtained value
    private double value;

	public Sensor(String name, String pinId, SensorType type, long refreshRate, double value) {
		this.name = name;
        this.pinId = pinId;
		this.type = type;
		this.refreshRate = refreshRate;
        this.value = value;
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

}
