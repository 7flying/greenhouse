package com.sevenflying.server.domain;


public abstract class BlossomSensor {

	// Name of the sensor
	private String name;
	// Maps to port (Axx or Dxx, analogic, digital)
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
}
