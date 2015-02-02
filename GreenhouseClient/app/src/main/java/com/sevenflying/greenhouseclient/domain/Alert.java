package com.sevenflying.greenhouseclient.domain;

import android.util.Base64;
import android.util.Log;

import com.sevenflying.greenhouseclient.net.Constants;

import java.io.Serializable;

/** Alert class.
 * Created by 7flying on 15/07/2014.
 */
public class Alert implements Serializable {
	
	private AlertType type;
	private double compareValue;
    private boolean isOn;
    private String sensorPinId;
    private String sensorName;
    private SensorType sensorType;

	public Alert(AlertType type, double compareValue, boolean isOn, String sensorPinId,
           String sensorName, SensorType sensorType)
    {
		this.type = type;
		this.compareValue = compareValue;
        this.isOn = isOn;
        this.sensorPinId = sensorPinId;
        this.sensorName = sensorName;
        this.sensorType = sensorType;
	}

    public Alert(){}

	/** Checks if the Alert has to be fired */
	public boolean isFired(double lastValue) {
	    if(isOn) {
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

    public void setAlertTypeSymbol(String symbol) throws Exception {
        Log.d(Constants.DEBUGTAG, " $ setAlertType  arg:" + symbol);
        if(symbol.equals(">"))
            this.type = AlertType.GREATER;
        else{
            if(symbol.equals(">="))
                this.type = AlertType.GREATER_EQUAL;
            else {
                if(symbol.equals("="))
                    this.type = AlertType.EQUAL;
                else {
                    if(symbol.equals("<"))
                        this.type = AlertType.LESS;
                    else {
                        if(symbol.equals("<="))
                            this.type = AlertType.LESS_EQUAL;
                        else
                            throw new Exception("Alert type unknown");
                    }
                }
            }
        }

    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType type) {
        this.sensorType = type;
    }

    public void setSensorType(char type) throws Exception {
        switch (type){
            case 'T':
                this.sensorType = SensorType.TEMPERATURE;
                break;
            case 'L':
                this.sensorType = SensorType.LIGHT;
                break;
            case 'H':
                this.sensorType = SensorType.HUMIDITY;
                break;
            default:
                throw new Exception("Unknown sensor type");
        }
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

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        this.isOn = on;
    }

    public boolean equals(Object al) {
		return (this.type == ((Alert) al).getAlertType() &&
           this.sensorPinId.equals(((Alert) al ).getSensorPinId()) &&
           this.sensorType == ((Alert) al).getSensorType());
	}

    public String toStoreString() {
        String toWrite = "";
        toWrite += Base64.encodeToString(getAlertType().getSymbol().getBytes(),
                Base64.DEFAULT) + ":";
        toWrite += Base64.encodeToString(Double.toString(getCompareValue()).getBytes(),
                Base64.DEFAULT ) +":";
        toWrite += Base64.encodeToString((isOn() ? "1" : "0").getBytes(),
                Base64.DEFAULT) + ":";
        toWrite += Base64.encodeToString(getSensorPinId().getBytes(),Base64.DEFAULT) + ":";
        toWrite += Base64.encodeToString(getSensorName().getBytes(), Base64.DEFAULT) + ":";
        toWrite += Base64.encodeToString(Character.toString(getSensorType().getIdentifier()).getBytes(),
                Base64.DEFAULT);
        return toWrite;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "type=" + type +
                ", compareValue=" + compareValue +
                ", isOn=" + isOn +
                ", sensorPinId='" + sensorPinId + '\'' +
                ", sensorName='" + sensorName + '\'' +
                ", sensorType=" + sensorType +
                '}';
    }
}
