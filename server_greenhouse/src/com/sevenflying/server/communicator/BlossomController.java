package com.sevenflying.server.communicator;

import java.util.HashMap;

import com.sevenflying.server.Env;
import com.sevenflying.server.communicator.Communicator;
import com.sevenflying.server.communicator.PortEvent;
import com.sevenflying.server.domain.Actuator;
import com.sevenflying.server.domain.Sensor;

/** Handles the communication requests.
 * @author 7flying
 */
public class BlossomController implements PortEvent {

	private static BlossomController controller = null;
	private Communicator communicator;
	private String portName;
	private static final char TERMINATION_CHAR = 'X', READ_PREFIX = 'R',
			WRITE_PREFIX = 'W';
	// Map containing the sensors. Key: Type + pinId
	private HashMap<String, Sensor> sensorMap;
	// Map containing the actuators
	private HashMap<String, Actuator> actuatorMap;

	private BlossomController(String portName) {
		this.portName = portName;
		this.communicator = new Communicator(this);
		this.sensorMap = new HashMap<String, Sensor>();
		this.actuatorMap = new HashMap<String, Actuator>();
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
		if(!sensorMap.containsKey(sensor.getType().getIdentifier()
				+ sensor.getPinId()))
			sensorMap.put(sensor.getType().getIdentifier()
					+ sensor.getPinId(), sensor);
	}

	public void addActuator(Actuator actuator) {
		if(!actuatorMap.containsKey(actuator.getType().getIdentifier()
				+ actuator.getPinId()))
			actuatorMap.put(actuator.getType().getIdentifier()
					+ actuator.getPinId(), actuator);
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
	 * @param sensorKey - (sensor.getType().getIdentifier() + sensor.getPinId())
	 */
	public void requestUpdate(String sensorKey) {
		if (sensorMap.containsKey(sensorKey)) {
			// Before asking we wait the required time
			if (sensorMap.get(sensorKey).isRefreshEnsured()) {
				if (Env.DEBUG)
					System.out.println(" - Waiting...");
				try {
					Thread.sleep(sensorMap.get(sensorKey).getRefreshRate());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// R{Type}{id}X
			communicator.sendData(READ_PREFIX + sensorKey + TERMINATION_CHAR);
		}
	}

	public void dataReceived(String data) {
		// It returns an echo of the command (with the termination char)
		// and: the read value (if update requested), ERROR: if something went
		// wrong; just the echo if it was an actuator.
		// So: {R|W}{Type-1char}{PinPin-2chars}X{|ERROR|{floatReading}}
		if (Env.DEBUG)
			System.out.println(" $ Echo: " + data);
		if (data != null && data.length() > 4) {
			if (data.charAt(4) != 'X' || data.substring(5) == null) {
				if(Env.DEBUG)
					System.err.println(" $ Error parsing command.");
			} else {
				switch(data.charAt(0)) {
				case 'R':
					if (sensorMap.containsKey(data.substring(1, 4))) {
						try {
							double lastValue = Double.parseDouble(data
													  .substring(5));
							sensorMap.get(data.substring(1, 4))
								.update(lastValue);
						} catch (NumberFormatException e) {
							if (Env.DEBUG)
								System.err.println(" $ Error parsing value: "
										+ data);
						}
					}
					break;
				case 'W':
					if (actuatorMap.containsKey(data.substring(1, 4))) {
						if (data.substring(5).equals("ERROR")) {
							//TODO throw error launching actuator, propagate to
							// client ?¿
						}
					}
					break;
				default:
					System.err.println("$ Error at: " + data);
				}
			}
			
		} else {
			if (Env.DEBUG)
				System.err.println("$ Error at " + data);
		}
	}

	public void sendDataTESTINGMETHOD(String data) {
		communicator.sendData(data);
	}

	public void setDebugMode(boolean activate) {
		communicator.setDebugMode(activate);
	}
	
	public void launch(String pinid) {
		if (actuatorMap.containsKey(pinid)) {
			communicator.sendData(WRITE_PREFIX + pinid + TERMINATION_CHAR);
		}
	}


}
