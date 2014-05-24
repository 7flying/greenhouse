package com.sevenflying.server.domain;

import com.sevenflying.server.database.DBManager;

public class Sensor extends BlossomSensor {

	// Rate at which data is gathered from this sensor (ms)
	private long refreshRate;
	// If the power saving mode is enabled this sensor is ignored
	private boolean powerSavingMode = false;
	// Hour-date of the last refresh, format: 'dd/MM/yy - HH:mm:ss'
	private String lastRefresh;	
	private SensorType type;

	public Sensor(String name, String id, SensorType type, long refreshRate) {
		super(name, id);
		this.type = type;
		this.refreshRate = refreshRate;
	}

	/** Updates the db with the sensor's last reading
	 * @param value - last read value
	 */
	public void update(double value){
		try {
			DBManager manager = DBManager.getInstance();
			manager.connect(DBManager.DBPath);
			manager.insertReading(this, value);
			manager.disconnect();
		} catch(Exception e) { e.printStackTrace();	}
	}

	/** Gets the sensor's last stored value
	 * @return sensor's last value
	 * @throws Exception
	 */
	public double getLastValue() throws Exception {
		double ret; 
		DBManager manager = DBManager.getInstance();
		manager.connect(DBManager.DBPath);
		ret = manager.getLastReading(this);
		manager.disconnect();
		return ret;
	}

	public void setPowerSavingMode(boolean activate) {
		powerSavingMode = activate;
	}

	public boolean getPowerSavingMode() {
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

	public String toString() {	
		return super.toString() + " refreshRate=" + refreshRate + ", powerSavingMode="
				+ powerSavingMode + ", lastRefresh=" + lastRefresh + ", type="
				+ type + "]";
	}

}
