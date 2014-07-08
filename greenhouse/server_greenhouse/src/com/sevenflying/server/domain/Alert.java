package com.sevenflying.server.domain;

import java.sql.SQLException;

import com.sevenflying.server.domain.exceptions.GreenhouseDatabaseException;

/** This Class holds the Alerts that a given Client is subscribed to */
public class Alert { // TODO makes more sense at the client
	
	private AlertType type;
	private Sensor sensor;
	private double compareValue;
	private double lastValue;
	
	public Alert(AlertType type, Sensor sensor, double compareValue) {
		this.type = type;
		this.sensor = sensor;
		this.compareValue = compareValue;
	}

	/** Checks if the Alert has to be fired */
	public boolean check() { // TODO looks dirty, change it 
		boolean check = false, fire = false;
		double lastValue = 0;
		try {
			lastValue = sensor.getLastValue();
			check = true;
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(GreenhouseDatabaseException ee) {
			check = false;
		}
		if(check) {
			switch(type) {
				case GREATER:
					fire = (lastValue > compareValue);
				case LESS:
					fire = (lastValue < compareValue);
				case EQUAL:
					fire = (lastValue == compareValue);
				case GREATER_EQUAL:
					fire = (lastValue >= compareValue);
				case LESS_EQUAL:
					fire = (lastValue <= compareValue);
			}
			this.lastValue = lastValue;
		}
		return fire;
	}

	public AlertType getType() {
		return type;
	}

	public void setType(AlertType type) {
		this.type = type;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public double getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(double compareValue) {
		this.compareValue = compareValue;
	}

	public double getLastValue() {
		return lastValue;
	}

	public void setLastValue(double lastValue) {
		this.lastValue = lastValue;
	}

	public boolean equals(Object al) {
		if(this.type == ((Alert) al).getType() &&
		   this.sensor.equals(((Alert) al).getSensor()) &&
		   this.compareValue == ((Alert) al).compareValue &&
		   this.lastValue == ((Alert) al).lastValue)
			return true;
		else
			return false;
	}
	
}
