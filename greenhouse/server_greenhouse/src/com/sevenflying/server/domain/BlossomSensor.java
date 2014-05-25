package com.sevenflying.server.domain;

public abstract class BlossomSensor {

	// Name of the sensor
	private String name;
	// Maps to port (Axx or Dxx, analog, digital)
	private String pinId;
	
	public BlossomSensor(String name, String pinId) {
		this.name = name;
		this.pinId = pinId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPinId() {
		return pinId;
	}

	public String toString() {
		return "BlossomSensor [name=" + name + ", pinId=" + pinId;
	}
	
}
