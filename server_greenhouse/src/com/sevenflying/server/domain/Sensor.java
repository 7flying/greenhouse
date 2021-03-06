package com.sevenflying.server.domain;

import java.sql.SQLException;

import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.exceptions.NoDataException;

/** Represents a sensor.
 * @author 7flying
 */
public class Sensor extends BlossomSensor {

	// Rate at which data is gathered from this sensor (ms)
	private long refreshRate;
	// If the power saving mode is enabled this sensor is ignored
	private boolean powerSavingMode = false;
	// Hour-date of the last refresh, format: 'dd/MM/yy - HH:mm:ss'
	private String lastRefresh;	
	private SensorType type;
	// If this flag is enabled it the refresh rate must be waited to avoid
	// halting the sensor
	private boolean ensureRefresh = false;

	public Sensor(String name, String pinId, SensorType type, long refreshRate,
	 boolean ensureRefresh)
	{
		super(name, pinId);
		this.type = type;
		this.refreshRate = refreshRate;
		this.ensureRefresh = ensureRefresh;
	}
	
	public Sensor() {
		super();
	}

	/** Updates the db with the sensor's last reading
	 * @param value - last read value
	 */
	public void update(double value){
		SensorUpdater updater = new SensorUpdater(this, value);
		updater.run();
	}

	/** Gets the sensor's last stored value
	 * @return sensor's last value
	 * @throws SQLException
	 */
	public double getLastValue() throws SQLException {
		double ret = -2366; 
		DBManager manager = DBManager.getInstance();
		try {
			manager.connect(DBManager.DBPath);
			ret = manager.getLastReading(this);
			manager.disconnect();
		} catch (NoDataException ee) {
			ret = -2366;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			manager.disconnect();
		}
		return ret;
	}

	public synchronized void setPowerSavingMode(boolean activate) {
		powerSavingMode = activate;
	}

	public boolean isPowerSavingOn() {
		return powerSavingMode;
	}

	public long getRefreshRate() {
		return refreshRate;
	}

	public void setRefreshRate(long refreshRate) {
		this.refreshRate = refreshRate;
	}

	public String getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(String lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	public SensorType getType() {
		return type;
	}

	public boolean isRefreshEnsured() {
		return ensureRefresh;
	}
	
	public void setEnsureRefresh(boolean ensureRefresh) {
		this.ensureRefresh = ensureRefresh;
	}
	
	public String toString() {	
		return super.toString() + " refreshRate=" + refreshRate
				+ ", powerSavingMode="
				+ powerSavingMode + ", lastRefresh=" + lastRefresh + ", type="
				+ type + ", ensureRefresh=" + ensureRefresh + "]";
	}
	
	public void setType(char type) {
		this.type = SensorType.getType(type);
    }

}

/**  Manages the update of the sensor */
class SensorUpdater extends Thread {
	
	private Sensor sensor;
	private double value;
	
	public SensorUpdater(Sensor sensor, double value) {
		this.sensor = sensor;
		this.value = value;
	}
	
	public void run() {
		try {
			DBManager manager = DBManager.getInstance();
			manager.connect(DBManager.DBPath);
			manager.insertReading(sensor, value);
			manager.disconnect();
			System.out.println(sensor + " value: " + value + " updated.");
		} catch(Exception e) { e.printStackTrace();	}
	}
}
