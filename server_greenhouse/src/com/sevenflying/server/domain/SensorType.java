package com.sevenflying.server.domain;

/** Defines the types of sensors, their measuring units and identifiers.
 * @author 7flying
 */
public enum SensorType {

	HUMIDITY, LIGHT, TEMPERATURE, PRESSURE, STEAM, UNKNOWN;
	
	/** Gets the measuring unit of the sensor.
	 * @return measuring unit
	 */
	public String getUnit() {
		switch (this) {
			case HUMIDITY: case STEAM:
				return "%";
			case LIGHT:
				return "lux";
			case TEMPERATURE:
				return "C";
			case PRESSURE:
				return "N";
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
			case PRESSURE:
				return 'P';
			case STEAM:
				return 'S';
			default:
				return '?';
		}
	}
	
	public static SensorType getType(char s) {
        switch (s) {
            case 'H':
                return HUMIDITY;
            case 'L':
                return LIGHT;
            case 'T':
                return TEMPERATURE;
            case 'P':
                return PRESSURE;
            case 'S':
                return STEAM;
            default:
                return UNKNOWN;
        }
    }
}
