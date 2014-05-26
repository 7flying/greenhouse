package com.sevenflying.server.communicator;

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

	public void close() {
		communicator.close();
	}
	
	public void addSensor(Sensor sensor) {
		if(!sensorMap.containsKey(sensor.getType() + sensor.getPinId()))
			sensorMap.put(sensor.getType() + sensor.getPinId(), sensor);
	}

	public void addActuator(Actuator actuator) {
		if(!actuatorMap.containsKey(actuator.getType() + actuator.getPinId()))
			actuatorMap.put(actuator.getType() + actuator.getPinId(), actuator);
	}
	
	public Sensor getSensor(String name) {
		return sensorMap.get(name);
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
	 * @param sensorName 
	 */
	public void requestUpdate(String sensorName) {
		if(sensorMap.containsKey(sensorName)) {
			// Type+id
			communicator.sendData(sensorMap.get(sensorName).getType() +
					sensorMap.get(sensorName).getPinId());
		}
	}

	public void dataReceived(String data) {
		// first 4 chars -> Type + id
		String type = data.substring(0, 1);
		String id = data.substring(1, 4);
		if(sensorMap.containsKey(type + id)) {
			sensorMap.get(type + id).update(Double.parseDouble(data.substring(4)));
		}
	}

	public void sendDataTESTINGMETHOD(String data) {
		communicator.sendData(data);
	}
	
	// TODO launch actuator

}
