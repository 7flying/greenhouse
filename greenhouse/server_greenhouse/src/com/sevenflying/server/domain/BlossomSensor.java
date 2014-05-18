package com.sevenflying.server.domain;


public abstract class BlossomSensor {

	// Name of the sensor
	private String name;
	// Maps to port
	private String id;
	
	public BlossomSensor(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
}
