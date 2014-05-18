package com.sevenflying.server.domain;

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
	
	public void update(String value){
		// TODO call to db manager and update value
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
	
	
	/*
	public String getData() {
		lastRefresh = new SimpleDateFormat("dd/MM/yy - HH:mm:ss").format(new GregorianCalendar().getTime());
		return null;
	}
	*/
	
}
