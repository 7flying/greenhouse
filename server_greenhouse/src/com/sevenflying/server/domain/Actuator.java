package com.sevenflying.server.domain;

import java.sql.SQLException;

/** Class representing an actuator
 * @author 7flying
 */
public class Actuator extends BlossomSensor {

	private ActuatorType type;
	private Sensor controlSensor;
	private CompareType compareType;
	private double compareValue;

	public Actuator() {
		super();
	}
	
	public Actuator(String name, String id, ActuatorType type) {
		super(name, id);
		this.type = type;
	}

	public Actuator(String name, String id, ActuatorType type,
			Sensor controlSensor, CompareType compareType, double compareValue)
	{
		super(name, id);
		this.type = type;
		this.controlSensor = controlSensor;
		this.compareType = compareType;
		this.compareValue = compareValue;
	}

	/** Checks whether an actuator is safe to launch.
	 * @return
	 */
	public boolean isSafeToLaunch() {
		if (controlSensor == null)
			return true;
		else {
			double lastValue = 0;
			Exception ex = null;
			try {
				lastValue = controlSensor.getLastValue();
			} catch (SQLException e) {
				ex = e;
				e.printStackTrace(); // TODO LOG THIS!
			}
			if (ex != null)
				return false;
			else {
				switch(compareType) {
				case GREATER:
					return (compareValue > lastValue);
				case LESS:
					return  (compareValue < lastValue);
				case EQUAL:
					return  (lastValue == compareValue);
				case GREATER_EQUAL:
					return (compareValue >= lastValue);
				case LESS_EQUAL:
					return  (compareValue <= lastValue);
				default:
					return false;
				}
			}
		}
	}

	public void launch() {
		if (isSafeToLaunch()) {
			// do something
		}
	}

	public ActuatorType getType() {
		return type;
	}

	public Sensor getControlSensor() {
		return controlSensor;
	}
	
	public boolean hasControlSensor() {
		return controlSensor != null;
	}

	public CompareType getCompareType() {
		return compareType;
	}

	public double getCompareValue() {
		return compareValue;
	}

	public void setType(ActuatorType type) {
		this.type = type;
	}

	public void setControlSensor(Sensor controlSensor) {
		this.controlSensor = controlSensor;
	}

	public void setCompareType(CompareType compareType) {
		this.compareType = compareType;
	}

	public void setCompareValue(double compareValue) {
		this.compareValue = compareValue;
	}

	@Override
	public String toString() {
		return "Actuator [type=" + type + ", controlSensor=" + controlSensor + ", compareType=" + compareType
				+ ", compareValue=" + compareValue + ", name=" + getName() + ", pinid()=" + getPinId() + "]";
	}
	
	
}
