package com.sevenflying.server.sensors;

import java.util.HashMap;

import com.sevenflying.server.communicator.Communicator;
import com.sevenflying.server.communicator.PortEvent;
import com.sevenflying.server.domain.Actuator;
import com.sevenflying.server.domain.Sensor;

public class BlossomController implements PortEvent {

	private static BlossomController controller = null;
	private Communicator communicator;

	// Map containing the sensors
	private HashMap<String, Sensor> sensorMap;
	// Map containing the actuators
	private HashMap<String, Actuator> actuatorMap;

	private BlossomController(String portName) {
		communicator = new Communicator(this);
		communicator.connect(portName, Communicator.DATA_RATE);
		sensorMap = new HashMap<String, Sensor>();
		actuatorMap = new HashMap<String, Actuator>();
	}

	public static BlossomController getInstance(String portName) {
		if(controller == null)
			controller = new BlossomController(portName);
		return controller;
	}

	public void addSensor(Sensor sensor) {
		if(!sensorMap.containsKey(sensor.getType() + sensor.getPinId()))
			sensorMap.put(sensor.getType() + sensor.getPinId(), sensor);
	}

	public void addActuator(Actuator actuator) {
		if(!actuatorMap.containsKey(actuator.getType() + actuator.getPinId()))
			actuatorMap.put(actuator.getType() + actuator.getPinId(), actuator);
	}

	/** Requests the update of the given sensor
	 * @param sensorName 
	 */
	public void requestUpdate(String sensorName) {
		if(sensorMap.containsKey(sensorName)) {
			// Type+id
			communicator.sendData(sensorMap.get(sensorName).getType() +
					new Integer(sensorMap.get(sensorName).getPinId()).toString());
		}
	}

	public void dataReceived(String data) {
		// first 4 chars -> Type + id
		String type = data.substring(0, 1);
		String id = data.substring(1, 3);
		if(sensorMap.containsKey(type + id)) {
			sensorMap.get(type + id).update(data.substring(3));
		}
	}


	public static void main(String[] args) {
		/*
		final Scanner sca = new Scanner(System.in);
		SensorsController handler = new SensorsController();
		try {
			handler.connect("COM5", Communicator.DATA_RATE);
			Thread t = new Thread(new Runnable() {
				public void run() {
					// Simple echo to see that this works
					String toWrite = sca.nextLine();
					while(!toWrite.equals("END")) {
					//	handler.sendData(toWrite);
						toWrite = sca.nextLine();
					}
					sca.close();
				}
			});
			t.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
		 */
	}
}