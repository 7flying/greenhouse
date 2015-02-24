package com.sevenflying.greenhouseclient.domain;

/** Defines the types of sensors, their measuring units and identifiers.
 * Created by 7flying on 10/07/2014.
 */
public enum SensorType {

    HUMIDITY, LIGHT, TEMPERATURE, PRESSURE, STEAM, UNKNOWN;
    public static final SensorType [] sensorTypeArray = { SensorType.HUMIDITY, SensorType.LIGHT,
            SensorType.TEMPERATURE, SensorType.PRESSURE, SensorType.STEAM
    };

    public int getIndex() {
        switch (this) {
            case HUMIDITY:
                return 0;
            case LIGHT:
                return 1;
            case TEMPERATURE:
                return 2;
            case PRESSURE:
                return 3;
            case STEAM:
                return 4;
            default:
                return -1;
        }
    }

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
                return "--";
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
