package com.sevenflying.server.domain;


public abstract class BlossomSensor {

	// Name of the sensor
	private String name;
	
	public BlossomSensor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
