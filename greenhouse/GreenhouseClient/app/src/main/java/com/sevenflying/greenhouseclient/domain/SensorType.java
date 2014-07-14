package com.sevenflying.greenhouseclient.domain;

/** Defines the types of sensors, their measuring units and identifiers.
 * Created by 7flying on 10/07/2014.
 */
public enum SensorType {

	HUMIDITY, LIGHT, TEMPERATURE, UNKNOWN;
	
	/** Gets the measuring unit of the sensor.
	 * @return meassuring unit
	 */
	public String getUnit() {
		switch (this) {
			case HUMIDITY:
				return "%";
			case LIGHT:
				return "lux";
			case TEMPERATURE:
				return "C";
			default:
				return "UNKNOWN";
		}
	}
	/** Gets the type identifier of the sensor
	 * @return type identifier
	 */
	public char getIdentifier() {
		switch (this) {
			case HUMIDITY:
				return 'H';
			case LIGHT:
				return 'L';
			case TEMPERATURE:
				return 'T';
			default:
				return '?';
		}
	}

    public String toString() {
        switch (this) {
            case HUMIDITY:
                return "Humidity";
            case LIGHT:
                return "Light";
            case TEMPERATURE:
                return "Temperature";
            default:
                return "Unknown";
        }
    }
}
