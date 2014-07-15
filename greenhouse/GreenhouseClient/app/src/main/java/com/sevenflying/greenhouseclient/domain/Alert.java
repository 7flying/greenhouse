package com.sevenflying.greenhouseclient.domain;

/**
 * Created by 7flying on 15/07/2014.
 */
public class Alert {
	
	private AlertType type;
	private double compareValue;
    private boolean active;
    private String sensorPinId;
    private String sensorName;
    private SensorType sensorType;

	public Alert(AlertType type, double compareValue, boolean active, String sensorPinId,
           String sensorName, SensorType sensorType)
    {
		this.type = type;
		this.compareValue = compareValue;
        this.active = active;
        this.sensorPinId = sensorPinId;
        this.sensorName = sensorName;
        this.sensorType = sensorType;
	}

	/** Checks if the Alert has to be fired */
	public boolean isFired(double lastValue) {
        if(active) {
            switch(type) {
                case GREATER:
                    return (lastValue > compareValue);
                case LESS:
                    return  (lastValue < compareValue);
                case EQUAL:
                    return  (lastValue == compareValue);
                case GREATER_EQUAL:
                    return (lastValue >= compareValue);
                case LESS_EQUAL:
                    return  (lastValue <= compareValue);
            }
        }
        return false;
	}

	public AlertType getAlertType() {
		return type;
	}

	public void setAlertType(AlertType type) {
		this.type = type;
	}

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType type) {
        this.sensorType = type;
    }

    public void setSensorPinId(String pinId) {
        this.sensorPinId = pinId;
    }

    public String getSensorPinId() { return  sensorPinId; }

    public void setSensorName(String name) {
        this.sensorName = name;
    }

    public String getSensorName() { return sensorName; }

    public double getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(double compareValue) {
		this.compareValue = compareValue;
	}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean equals(Object al) {
		if(this.type == ((Alert) al).getAlertType() &&
		   this.compareValue == ((Alert) al).compareValue &&
           this.sensorPinId.equals(((Alert) al ).getSensorPinId()) &&
           this.sensorType == ((Alert) al).getSensorType())
			return true;
		else
			return false;
	}
	
}
