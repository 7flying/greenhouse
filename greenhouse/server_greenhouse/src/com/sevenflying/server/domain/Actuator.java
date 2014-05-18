package com.sevenflying.server.domain;

public class Actuator extends BlossomSensor {

	private ActuatorType type;
	
	public Actuator(String name, String id, ActuatorType type) {
		super(name, id);
		this.type = type;
	}

	public void launch() {
		//TODO
	}

	public ActuatorType getType() {
		return type;
	}
}
