package com.sevenflying.server.communicator;

import java.util.HashMap;

import com.sevenflying.server.communicator.Communicator;
import com.sevenflying.server.communicator.PortEvent;
import com.sevenflying.server.domain.Actuator;
import com.sevenflying.server.domain.Sensor;

public class BlossomController implements PortEvent {

	private static BlossomController controller = null;
	private Communicator communicator;
	private String portName;
	private static final char TERMINATION_CHAR = 'X';
	// Map containing the sensors. Key: Type + pinId
	private HashMap<String, Sensor> sensorMap;
	// Map containing the actuators
	private HashMap<String, Actuator> actuatorMap;

	private BlossomController(String portName) {
		this.portName = portName;
		communicator = new Communicator(this);
		sensorMap = new HashMap<String, Sensor>();
		actuatorMap = new HashMap<String, Actuator>();
	}

	public static BlossomController getInstance(String portName) {
		if(controller == null)
			controller = new BlossomController(portName);
		return controller;
	}

	public void connect() {
		communicator.connect(portName, Communicator.DATA_RATE);
	}
	
	public void close() {
		communicator.close();
	}

	public void addSensor(Sensor sensor) {
		if(!sensorMap.containsKey(sensor.getType().getIdentifier() + sensor.getPinId()))
			sensorMap.put(sensor.getType().getIdentifier() + sensor.getPinId(), sensor);
	}

	public void addActuator(Actuator actuator) {
		if(!actuatorMap.containsKey(actuator.getType().getIdentifier() + actuator.getPinId()))
			actuatorMap.put(actuator.getType().getIdentifier() + actuator.getPinId(), actuator);
	}

	public Sensor getSensor(String key) {
		return sensorMap.get(key);
	}

	public Actuator getActuator(String name) {
		return actuatorMap.get(name);
	}


	public HashMap<String, Sensor> getSensorMap() {
		return sensorMap;
	}

	public void setSensorMap(HashMap<String, Sensor> sensorMap) {
		this.sensorMap = sensorMap;
	}

	public HashMap<String, Actuator> getActuatorMap() {
		return actuatorMap;
	}

	public void setActuatorMap(HashMap<String, Actuator> actuatorMap) {
		this.actuatorMap = actuatorMap;
	}

	/** Requests the update of the given sensor
	 * @param sensorKey 
	 */
	public void requestUpdate(String sensorKey) {
		if(sensorMap.containsKey(sensorKey)) {
			// Before asking we wait the required time
			if(sensorMap.get(sensorKey).isRefreshEnsured()) {
				System.out.println(" - Waiting...");
				try {
					Thread.sleep(sensorMap.get(sensorKey).getRefreshRate());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// Type+id+X
			communicator.sendData(sensorKey + TERMINATION_CHAR);
		}
	}

	public void dataReceived(String data) {
		// It returns:
		// type-id-X-data: if everything went fine,
		// type-id-X: error
		if(data.length() > 5) {
			System.out.println("$ Echo: " + data);
			// first 4 chars -> Type + id
			if(sensorMap.containsKey(data.substring(0, 4))) {
				// pos 4 char -> X
				sensorMap.get(data.substring(0, 4)).update(Double.parseDouble(data.substring(5)));
			}
		} else {
			System.out.println("$ Error at " + data);
		}
	}

	public void sendDataTESTINGMETHOD(String data) {
		communicator.sendData(data);
	}

	public void setDebugMode(boolean activate) {
		communicator.setDebugMode(activate);
	}
	// TODO launch actuator

}
