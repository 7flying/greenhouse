package com.sevenflying.server.domain;

import java.sql.SQLException;

import com.sevenflying.server.domain.exceptions.GreenhouseDatabaseException;

public class Alert {
	
	private AlertType type;
	private Sensor sensor;
	private double compareValue;
	
	public Alert(AlertType type, Sensor sensor, double compareValue) {
		this.type = type;
		this.sensor = sensor;
		this.compareValue = compareValue;
	}

	/** Checks if the Alert has to be fired */
	public boolean check() {
		boolean check = false, ret = false;
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
					ret = (lastValue > compareValue) ? true : false;
				case LESS:
					ret = (lastValue < compareValue) ? true : false;
				case EQUAL:
					ret = (lastValue == compareValue) ? true : false;
				case GREATER_EQUAL:
					ret = (lastValue >= compareValue) ? true : false;
				case LESS_EQUAL:
					ret = (lastValue <= compareValue) ? true : false;
			}
		}
		return ret;
	}
	
	public void fire() {
		// TODO
	}
	
}
