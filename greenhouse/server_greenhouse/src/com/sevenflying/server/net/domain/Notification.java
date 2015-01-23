package com.sevenflying.server.net.domain;

import java.io.Serializable;

import com.sevenflying.server.domain.SensorType;

/** Notification sent to the client with simple data to show sensor info - value */
public class Notification implements Serializable { // TODO makes more sense at the client

	private static final long serialVersionUID = -8306022585591964551L;
	private String clientId;
	private double sensorValue;
	private String sensorName;
	private SensorType type;
	
	public Notification(String clientId, double sensorValue, String sensorName, SensorType type) {
		this.clientId = clientId;
		this.sensorValue = sensorValue;
		this.sensorName = sensorName;
		this.type = type;
	}

	public String getClientId() {
		return clientId;
	}

	public double getSensorValue() {
		return sensorValue;
	}

	public String getSensorName() {
		return sensorName;
	}

	public SensorType getType() {
		return type;
	}	

	public boolean equals(Object not) {
		if(this.clientId.equals(((Notification) not).getClientId()) &&
		   this.sensorName.equals(((Notification) not).sensorName) &&
		   this.type == ((Notification) not).getType())
			return true;
		else
			return false;
	}
}
