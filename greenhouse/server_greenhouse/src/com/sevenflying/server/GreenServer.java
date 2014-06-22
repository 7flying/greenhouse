package com.sevenflying.server;

import java.util.HashMap;
import java.util.Set;

import com.sevenflying.server.communicator.BlossomController;

/** Server that continuously gathers data from the sensors and logs it into the db */
public class GreenServer extends Thread {

	private BlossomController controller;

	public GreenServer(String portName) {
		controller = BlossomController.getInstance(portName);
	}

	public void run() {
		controller.connect();
		long testRun = 60000, timeOn = 0;
		HashMap<String, Long> timeMap = new HashMap<String, Long>();
		Set<String> sensorKeys = controller.getSensorMap().keySet();
		for(String key : sensorKeys)
			timeMap.put(key, System.currentTimeMillis());

		while(timeOn < testRun) {
			long current = System.currentTimeMillis();
			synchronized(controller) {
				sensorKeys = controller.getSensorMap().keySet();
				for(String key : sensorKeys) {
					// If the sensor is not in power saving mode and the refresh rate (wait time) comes, gather data
					if(controller.getSensor(key) != null) {
						if(!controller.getSensor(key).isPowerSavingOn()) {
							if(System.currentTimeMillis() - timeMap.get(key) >= controller.getSensor(key).getRefreshRate()) {
								System.out.println("$ Requesting update of: " + key);
								controller.requestUpdate(key);
								timeMap.put(key, new Long(System.currentTimeMillis()));
							}
						}
					} else {
						// get rid of that pair at timeMap
						timeMap.remove(key);
					}
				}
			}
			try {
				sleep(10);
			} catch (InterruptedException e) { e.printStackTrace(); }
			timeOn += System.currentTimeMillis() - current;
		}
		controller.close();
	}
}
