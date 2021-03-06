package com.sevenflying.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;

import com.sevenflying.server.Env;
import com.sevenflying.server.GreenServer;
import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.Actuator;
import com.sevenflying.server.domain.ActuatorType;
import com.sevenflying.server.domain.CompareType;
import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.SensorType;
import com.sevenflying.server.domain.exceptions.DuplicatedActuatorException;
import com.sevenflying.server.domain.exceptions.DuplicatedSensorException;
import com.sevenflying.server.domain.exceptions.NoDataException;
import com.sevenflying.server.domain.exceptions.NoSuchActuatorException;
import com.sevenflying.server.domain.exceptions.NoSuchSensorException;
import com.sevenflying.utils.Utils;

/** Manages the communications.
 * @author 7flying
 */
public class NetServer {

	private static String pathToDB = Env.DB_PATH;

	private static GreenServer greenDaemon = null;
	
	public NetServer(GreenServer greenDaemon) {
		if (NetServer.greenDaemon == null)
			NetServer.greenDaemon = greenDaemon; 
	}

	public void launch() throws Exception {
		Socket s = null;
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(5432);
		System.out.println(" $ Server alive ");
		while(true) {
			try {
				s = ss.accept();
				processConnection(s);
			} catch(Exception e) {
				e.printStackTrace();
			}
			Thread.sleep(100);
		}
	}

	public void processConnection(final Socket s) {
		System.out.println("\n $ Incoming connection:" +
		 s.getInetAddress().toString() + " " + s.getLocalAddress());
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectOutputStream oos = new ObjectOutputStream(
						s.getOutputStream());
					ObjectInputStream ois = new ObjectInputStream(
						s.getInputStream());
					String command = (String) ois.readObject();
					System.out.println(" $ Received '" + command + "'");
					switch (command) {
					case Constants.GETSENSORS:
						getSensorValues(ois, oos);
						break;
					case Constants.HISTORY:
						getSensorHistory(ois, oos);
						break;
					case Constants.CHECK:	
						getSensorLastValue(ois, oos);
						break;
					case Constants.NEW_SENSOR:
						createSensor(ois, oos);
						break;
					case Constants.DELETE_SENSOR:
						deleteSensor(ois, oos);
						break;
					case Constants.UPDATE_SENSOR:
						updateSensor(ois, oos);
						break;
					case Constants.POWSAV:
						setPowerSaving(ois, oos);
						break;
					case Constants.POWSAV_STATUS:
						getPowerSavingStatus(ois, oos);
						break;
					case Constants.GETACTUATORS:
						getActuators(ois, oos);
						break;
					case Constants.NEW_ACTUATOR:
						createActuator(ois, oos);
						break;
					case Constants.DELETE_ACTUATOR:
						deleteActuator(ois, oos);
						break;
					case Constants.UPDATE_ACTUATOR:
						updateActuator(ois, oos);
						break;
					case Constants.LAUNCH:
						launchActuator(ois, oos);
						break;
					case Constants.TEST_CONNECTION:
						// Send an ok
						oos.writeObject(Constants.OK);
						oos.flush();	

						oos.close();
						ois.close();
						break;
					default:
						System.out.println("$$ Command " + command + " UNKNOWN");
						oos.close();
						ois.close();
						break;
					}
					s.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.run();
	}

	/** Processes GETSENSORS command. Get the last values of all sensors 
	 * @return a list with the last values
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	private void getSensorValues(ObjectInputStream ois, ObjectOutputStream oos)
			throws ClassNotFoundException, SQLException, IOException
	{
		DBManager manager = DBManager.getInstance();
		manager.connect(pathToDB);
		List<Sensor> sensorList = manager.getSensors();
		List<String> ret = new ArrayList<String>();
		for (Sensor s : sensorList)
			ret.add(Utils.encode64(s.getName()) + ":" +
					Utils.encode64(s.getPinId()) + ":" + 
					Utils.encode64(s.getType().getIdentifier()) + ":" + 
					Utils.encode64(s.getRefreshRate()) + ":" + 
					Utils.encode64(s.getLastValue()));
		manager.disconnect();

		int index = 0, error = 0;
		int number = ret.size();
		System.out.println("\t Returning: " + ret.size() + " sensors");
		// Tell to the client how many sensors it has to expect
		oos.writeObject(Integer.valueOf(number).toString());
		oos.flush();
		while (number > 0) {
			oos.writeObject(ret.get(index));
			oos.flush();
			String control = (String) ois.readObject();
			if(control.equals("ACK")){
				number--;
				index++;
				error = 0;
			} else {
				if(error < 3)
					error++;
				else {
					// tell error to client
					// continue
					error = 0;
					number--;
					index++;
				}
			}
		}
		oos.close();
		ois.close();
	}

	/** Processes HISTORY command. Gets the historical values of a sensor
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void getSensorHistory(ObjectInputStream ois, ObjectOutputStream oos)
	throws Exception
	{
		// Read sensor pinid and type
		String pinidType = (String) ois.readObject();
		System.out.println("\t -Params: " + pinidType);
		DBManager manager = DBManager.getInstance();
		manager.connect(pathToDB);
		List<Map<String, Double>> history = manager.getLastXFromSensor(
			15, pinidType.substring(0, pinidType.indexOf(':')),
			pinidType.substring(pinidType.indexOf(':') + 1));
		manager.disconnect();
		// Tell to the client how many values it has to expect
		int number = history.size();
		oos.writeObject(Integer.valueOf(number).toString());
		oos.flush();
		// Send data
		int index = 0, error = 0;

		while(number > 0) {
			Map<String, Double> map = history.get(index);
			String toWrite = Utils.encode64((String) map.keySet().toArray()[0])
			 + ":" + Utils.encode64(map.get((String) map.keySet().toArray()[0]));
			oos.writeObject(toWrite);
			oos.flush();
			String control = (String) ois.readObject();
			if(control.equals("ACK")) {
				System.out.println("\t ACK "
				 + (String) map.keySet().toArray()[0]
				 + ":" + map.get((String) map.keySet().toArray()[0]));
				number--;
				index++;
				error = 0;
			} else {
				System.out.println("\t NACK "
					+ (String) map.keySet().toArray()[0]
					+ ":" + map.get((String) map.keySet().toArray()[0]));
				if(error < 3)
					error++;
				else {
					// tell error to client
					// continue
					error = 0;
					number--;
					index++;
				}
			}
		}
		oos.close();
		ois.close();
	}

	/** Processes CHECK command. Gets the last value of a sensor.
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void getSensorLastValue(ObjectInputStream ois,
	 ObjectOutputStream oos) throws Exception
	{
		// Read sensor pinid and type
		String pinidType = (String) ois.readObject();
		DBManager manager = DBManager.getInstance();
		manager.connect(pathToDB);
		double reading = -1;
		try {
			reading = manager.getLastReading(
				pinidType.substring(0, pinidType.indexOf(':')),
				pinidType.substring(pinidType.indexOf(':') + 1));
			System.out.println("\t value: " + reading);
			oos.writeObject(Utils.encode64(Double.valueOf(reading).toString()));
			oos.flush();
			System.out.println(reading);
		} catch(NoDataException e) {
			System.out.println("\t no data");
			oos.writeObject(Utils.encode64(Double.valueOf(reading).toString()));
			oos.flush();
			System.out.println(reading);
		} catch(SQLException e1) {
			System.out.println("\t SQL exception");
			e1.printStackTrace();
			oos.writeObject("X");
			oos.flush();
		}
		manager.disconnect();
		oos.close();
		ois.close();
	}
	
	/** Processes NEW_SENSOR command. Creates a new sensor.
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void createSensor(ObjectInputStream ois, ObjectOutputStream oos)
	throws Exception
	{
		String raw = (String) ois.readObject();
		System.out.println(" $ params: " + raw);
		StringTokenizer tokenizer = new StringTokenizer(raw, ":");
		String [] temp = new String[5];
		int index = 0;
		while(tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken().trim();
			System.out.println(temp[index]);
			index++;
		}
		String errorCode = null;
		if (index == 5) {
			DBManager manager = DBManager.getInstance();
			try {
				manager.connect(pathToDB);				
				manager.insertSensor(new Sensor(
					new String(Base64.decodeBase64(temp[0])),
					temp[1],
					SensorType.valueOf(temp[2].toUpperCase()),
					Long.valueOf(temp[3]),
					Boolean.valueOf(temp[4])));
				
			} catch (DuplicatedSensorException ex) {
				errorCode = Constants.DUPLICATED_SENSOR;
				manager.disconnect();
			} catch (NumberFormatException | SQLException
					| ClassNotFoundException e)
			{
				e.printStackTrace(); 
				errorCode = Constants.INTERNAL_SERVER_ERROR;
			} finally {
				manager.disconnect();
			}
		} else {
			// Incorrect number of parameters
			errorCode = Constants.INCORRECT_NUMBER_OF_PARAMS;
		}
		oos.writeObject(errorCode == null ? Constants.OK : errorCode);
		oos.flush();	

		oos.close();
		ois.close();
	}
	
	/** Processes UPDATE_SENSOR command. Updates the given sensor
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void updateSensor(ObjectInputStream ois, ObjectOutputStream oos)
	throws Exception
	{
		String raw = (String) ois.readObject();
		System.out.println(" $ params: " + raw);
		StringTokenizer tokenizer = new StringTokenizer(raw, ":");
		String [] temp = new String[5];
		int index = 0;
		while(tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken();
			index++;
		}
		String errorCode = null;
		if(index == 5) {
			DBManager manager = DBManager.getInstance();
			try {
				manager.connect(pathToDB);				
				manager.updateSensor(new Sensor(
					new String(Base64.decodeBase64(temp[0])),
					temp[1],
					SensorType.valueOf(temp[2].toUpperCase()),
					Long.valueOf(temp[3]),
					Boolean.valueOf(temp[4])));
				manager.disconnect();
			} catch (NumberFormatException | SQLException |
				ClassNotFoundException e)
			{
				e.printStackTrace(); 
				errorCode = Constants.INTERNAL_SERVER_ERROR;
			}
		} else {
			errorCode = Constants.INCORRECT_NUMBER_OF_PARAMS;
		}
		oos.writeObject(errorCode == null ? Constants.OK : errorCode);
		oos.flush();	

		oos.close();
		ois.close();
	}
	
	/** Processes POWSAV command.
	 * Activates/deactivates power saving mode on a sensor.
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void setPowerSaving(ObjectInputStream ois, ObjectOutputStream oos)
	throws Exception
	{
		String raw = (String) ois.readObject();
		StringTokenizer tokenizer = new StringTokenizer(raw, ":");
		String [] temp = new String[3];
		int index = 0;
		while (tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken();
			index++;
		}
		String errorCode = null;
		if (index == 3) {
			if (greenDaemon != null)
				greenDaemon.setPowerSaving(temp[0], temp[1],
					Boolean.valueOf(temp[2])); // TODO handle error codes here
		} else {
			errorCode = Constants.INCORRECT_NUMBER_OF_PARAMS;
		}
		oos.writeObject(errorCode == null ? Constants.OK : errorCode);
		oos.flush();	

		oos.close();
		ois.close();
	}
	
	/** Returns the power saving state of a sensor.
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void getPowerSavingStatus(ObjectInputStream ois,
	ObjectOutputStream oos) throws Exception
	{
		String raw = (String) ois.readObject();
		StringTokenizer tokenizer  = new StringTokenizer(raw, ":");
		String [] temp = new String[2];
		int index = 0;
		while (tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken();
			index++;
		}
		String errorCode = Constants.INTERNAL_SERVER_ERROR, ret = null;
		if (index == 2) {
			if (greenDaemon != null) {
				ret = Boolean.toString(greenDaemon
						.isPowerSavinEnabled(temp[0], temp[1]));
			} else {
				if (Env.DEBUG)
					ret = new Random(System.currentTimeMillis()).nextInt(2) == 1 ?
						"true" : "false";
			}
				
		} else {
			errorCode = Constants.INCORRECT_NUMBER_OF_PARAMS;
		}
		System.out.println("\t " + ret);
		oos.writeObject(ret != null ? ret : errorCode);
		oos.flush();
		oos.close();
		ois.close();
	}
	
	/** Processes DELETE_SENSOR command. Deletes the given sensor and its readings.
	 * Expects sensor's pinid and type
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void deleteSensor(ObjectInputStream ois, ObjectOutputStream oos)
	throws Exception
	{
		String raw = (String) ois.readObject();
		StringTokenizer tokenizer = new StringTokenizer(raw, ":");
		String [] temp = new String[2];
		int index = 0;
		while (tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken();
			index++;
		}
		String errorCode = null;
		if (index == 2) {
			DBManager manager = DBManager.getInstance();
			try {
				System.out.println("\t -Params: " + temp[0] + ":" + temp[1]);
				manager.connect(pathToDB);				
				manager.deleteSensor(temp[0], temp[1]);
				manager.disconnect();
			} catch (NumberFormatException | SQLException
					| ClassNotFoundException e)
			{
				e.printStackTrace(); 
				errorCode = Constants.INTERNAL_SERVER_ERROR;
			}
		} else {
			errorCode = Constants.INCORRECT_NUMBER_OF_PARAMS;
		}
		oos.writeObject(errorCode == null ? Constants.OK : errorCode);
		oos.flush();	

		oos.close();
		ois.close();
	}
	
	// --- Actuators ---
	
	/** Returns the actuators from the database 
	 * @param ois
	 * @param oos
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	private void getActuators(ObjectInputStream ois, ObjectOutputStream oos)
	throws ClassNotFoundException, SQLException, IOException
	{
		DBManager manager = DBManager.getInstance();
		manager.connect(pathToDB);
		String errorCode = null;
		try {
			List<Actuator> listActuator = manager.getActuators();
			List<String> listRet = new ArrayList<String>();
			for (Actuator act : listActuator) {
				// 0- name (encoded)
		        // 1- id (encoded)
		        // 2- (optional) sensor type (encoded)
		        // 3- (optional) sensor id (encoded)
		        // 4- (optional) compare type (encoded)
		        // 5- (optional) compare value (encoded)
				String temp = Utils.encode64(act.getName()) + ":" +
							  Utils.encode64(act.getPinId());
				if (act.hasControlSensor()) {
					temp += ":" + Utils.encode64(act.getControlSensor()
								  .getType().getIdentifier())
						  + ":" + Utils.encode64(act.getControlSensor()
								  .getPinId())
						  + ":" + Utils.encode64(act.getCompareType()
								  .toString())
						  + ":" + Utils.encode64(act.getCompareValue());
				}
				listRet.add(temp);
			}
			int retNumber = listRet.size(), current = 0, error = 0;
			System.out.println("\t # Returning: " + retNumber + " actuators");
			System.out.println("\t # " + listActuator.toString());
			// Tell the client how many actuator it has to expect
			oos.writeObject(Integer.valueOf(retNumber).toString());
			oos.flush();
			while (retNumber > 0) {
				oos.writeObject(listRet.get(current));
				oos.flush();
				String control = (String) ois.readObject();
				if (control.equals(Constants.ACK)) {
					retNumber--;
					current++;
				} else {
					// Retry if error count < 3
					if (error < 3)
						error++;
					else {
						// discard the actuator
						error = 0;
						retNumber--;
						current++;
					}
				}
			}
		} catch (NoSuchSensorException e) {
			e.printStackTrace();
			errorCode = Constants.INTERNAL_SERVER_ERROR;
		}
		oos.writeObject(errorCode == null ? Constants.ACK : errorCode);
		
		oos.flush();
		oos.close();
		ois.close();
	}
	
	/** Handles the creation of an actuator.
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void createActuator(ObjectInputStream ois, ObjectOutputStream oos)
	throws Exception
	{
		String raw = (String) ois.readObject();
		System.out.println(" $ params: " + raw);
		StringTokenizer tokenizer = new StringTokenizer(raw, ":");
		String [] temp = new String[6]; // Actuator is variable, max = 6
		int index = 0;
		while(tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken().trim();
			index++;
		}
		// 0- name (encoded)
        // 1- id
        // 2- (optional) sensor type
        // 3- (optional) sensor id
        // 4- (optional) compare type
        // 5- (optional) compare value
		String errorCode = null;
		if (index == 6 || index == 2) {
			DBManager manager = DBManager.getInstance();
			try {
				manager.connect(pathToDB);
				Actuator act = new Actuator(
						new String(Base64.decodeBase64(temp[0])),
						temp[1],
						ActuatorType.PUMP);
				if (index == 6) {
					Sensor sensor = manager.getSensor(
							SensorType.valueOf(temp[2].toUpperCase()), // type
							temp[3]);								   // id
					act.setControlSensor(sensor);
					act.setCompareType(CompareType.valueOf(
							temp[4].toUpperCase()));
					act.setCompareValue(Double.parseDouble(temp[5]));
				}
				manager.insertActuator(act);
				
			} catch (DuplicatedActuatorException ex) {
				errorCode = Constants.DUPLICATED_ACTUATOR;
				manager.disconnect();
			} catch (NumberFormatException | SQLException
					| ClassNotFoundException e)
			{
				e.printStackTrace(); 
				errorCode = Constants.INTERNAL_SERVER_ERROR;
			} catch (NoSuchSensorException e) {
				e.printStackTrace();
				errorCode = Constants.INTERNAL_SERVER_ERROR;
			} finally {
				manager.disconnect();
			}
		} else {
			// Incorrect number of parameters
			errorCode = Constants.INCORRECT_NUMBER_OF_PARAMS;
		}
		oos.writeObject(errorCode == null ? Constants.OK : errorCode);
		oos.flush();	

		oos.close();
		ois.close();
	}
	
	/** Handles the deletion of an actuator.
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void deleteActuator(ObjectInputStream ois, ObjectOutputStream oos)
	 throws Exception
	{
		String raw = (String) ois.readObject();
		StringTokenizer tokenizer = new StringTokenizer(raw, ":");
		String temp = tokenizer.nextToken();
		String errorCode = null;
		if (temp != null) {
			DBManager manager = DBManager.getInstance();
			try {
				System.out.println("\t -Params: " + temp);
				manager.connect(pathToDB);				
				manager.deleteActuator(temp);
				manager.disconnect();
			} catch (NumberFormatException | SQLException
					| ClassNotFoundException e)
			{
				e.printStackTrace(); 
				errorCode = Constants.INTERNAL_SERVER_ERROR;
			}
		} else {
			errorCode = Constants.INCORRECT_NUMBER_OF_PARAMS;
		}
		oos.writeObject(errorCode == null ? Constants.OK : errorCode);
		oos.flush();	

		oos.close();
		ois.close();
	}
	
	/** Handles the update of an actuator.
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void updateActuator(ObjectInputStream ois, ObjectOutputStream oos)
	 throws Exception
	{
		String raw = (String) ois.readObject();
		System.out.println(" $ params: " + raw);
		StringTokenizer tokenizer = new StringTokenizer(raw, ":");
		String [] temp = new String[6]; // max is 6
		int index = 0;
		while (tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken();
			index++;
		}
		// 0- name (encoded)
        // 1- id
        // 2- (optional) sensor type
        // 3- (optional) sensor id
        // 4- (optional) compare type
        // 5- (optional) compare value
		String errorCode = null;
		if (index == 6 || index == 2) {
			DBManager manager = DBManager.getInstance();
			try {
				manager.connect(pathToDB);
				manager.connect(pathToDB);
				Actuator act = new Actuator(
						new String(Base64.decodeBase64(temp[0])),
						temp[1],
						ActuatorType.PUMP);
				if (index == 6) {
					Sensor sensor = manager.getSensor(
							SensorType.valueOf(temp[2].toUpperCase()), // type
							temp[3]);								   // id
					act.setControlSensor(sensor);
					act.setCompareType(CompareType.valueOf(
							temp[4].toUpperCase()));
					act.setCompareValue(Double.parseDouble(temp[5]));
				}
				manager.updateActuator(act);
			} catch (NoSuchSensorException | NoSuchActuatorException
					| DuplicatedActuatorException ex)
			{
				ex.printStackTrace();
				errorCode = Constants.INTERNAL_SERVER_ERROR;
			} catch (NumberFormatException | SQLException |
				ClassNotFoundException e)
			{
				e.printStackTrace(); 
				errorCode = Constants.INTERNAL_SERVER_ERROR;
			} finally {
				manager.disconnect();
			}
		} else {
			System.out.println("\t index: " + index);
			errorCode = Constants.INCORRECT_NUMBER_OF_PARAMS;
		}
		
		if (errorCode != null) { 
			oos.writeObject(errorCode);
			System.out.println("\t " + errorCode);
		} else {
			oos.writeObject(Constants.OK);
			System.out.println("\t " + Constants.OK);
		}oos.flush();	

		oos.close();
		ois.close();
	}
	
	/** Launches an Actuator
	 * @param ois
	 * @param oos
	 * @throws IOException
	 */
	private void launchActuator(ObjectInputStream ois, ObjectOutputStream oos)
	throws IOException
	{
		String errorCode = null;
		try {
			String raw = (String) ois.readObject();
			// Read actuator pinid
			if (raw != null) {
				DBManager manager = DBManager.getInstance();
				manager.connect(pathToDB);
				if (greenDaemon == null) {
					if (!manager.isActuatorCreated(raw))
						errorCode = Constants.INTERNAL_SERVER_ERROR;
				} else {
					if (!manager.isActuatorCreated(raw)
						|| !greenDaemon.launchActuator(raw)) 
						{
							errorCode = Constants.INTERNAL_SERVER_ERROR;
						}
				}
				manager.disconnect();
			}
		} catch (ClassNotFoundException | IOException | SQLException e) {
			errorCode = Constants.INTERNAL_SERVER_ERROR;
		}
		oos.writeObject(errorCode == null ? Constants.OK : errorCode);
		oos.flush();
		oos.close();
		ois.close();
	}
	
	public static void main(String [] args) throws Exception {
		NetServer ns = new NetServer(null);
		ns.launch();
	}

}

