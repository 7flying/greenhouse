package com.sevenflying.server.domain;

import java.sql.SQLException;
import com.sevenflying.server.domain.exceptions.GreenhouseDatabaseException;

public class Alert {
	
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
					fire = (lastValue > compareValue) ? true : false;
				case LESS:
					fire = (lastValue < compareValue) ? true : false;
				case EQUAL:
					fire = (lastValue == compareValue) ? true : false;
				case GREATER_EQUAL:
					fire = (lastValue >= compareValue) ? true : false;
				case LESS_EQUAL:
					fire = (lastValue <= compareValue) ? true : false;
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
	
}
