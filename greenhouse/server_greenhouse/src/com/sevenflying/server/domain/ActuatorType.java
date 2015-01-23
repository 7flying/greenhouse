package com.sevenflying.server.domain;

public enum ActuatorType {

	PUMP;
	
	/** Gets the type identifier of the actuator
	 * @return type identifier
	 */
	public char getIdentifier() {
		switch (this) {
			case PUMP:
				return 'P';
			default:
				return '?';
		}
	}
}
