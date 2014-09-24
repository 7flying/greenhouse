package com.sevenflying.server.net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;

import com.sevenflying.server.GreenServer;
import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.SensorType;
import com.sevenflying.server.domain.exceptions.GreenhouseDatabaseException;
import com.sevenflying.server.domain.exceptions.NoDataException;
import com.sevenflying.utils.Utils;


/** Manages the communications. */
public class NetServer {

	private static String pathToDB = "F:\\dump\\greenhouse\\db.sqlite"; //TODO

	private static GreenServer greenDaemon = null;
	
	public NetServer(GreenServer greenDaemon) {
		if(NetServer.greenDaemon == null)
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
		System.out.println(" $ Incoming connection:" +
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
					case Constants.NEW:
						createSensor(ois, oos);
						break;
					case Constants.DELETE:
						deleteSensor(ois, oos);
						break;
					case Constants.UPDATE:
						updateSensor(ois, oos);
						break;
					case Constants.POWSAV:
						setPowerSaving(ois, oos);
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
	 */
	private void getSensorValues(ObjectInputStream ois, ObjectOutputStream oos)
	throws Exception
	{
		DBManager manager = DBManager.getInstance();
		manager.connect(pathToDB);
		List<Sensor> sensorList = manager.getSensors();
		List<String> ret = new ArrayList<String>();
		for(Sensor s : sensorList)
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
		while(number > 0) {
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
			5, pinidType.substring(0, pinidType.indexOf(':')),
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
			oos.writeObject(Utils.encode64(Double.valueOf(reading).toString()));
			oos.flush();
			System.out.println(reading);
		} catch(NoDataException e) {
			oos.writeObject(Utils.encode64(Double.valueOf(reading).toString()));
			oos.flush();
			System.out.println(reading);
		} catch(SQLException e1) {
			oos.writeObject("X");
			oos.flush();
		}
		manager.disconnect();
		oos.close();
		ois.close();
	}
	
	/** Processes NEW command. Creates a new sensor.
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void createSensor(ObjectInputStream ois, ObjectOutputStream oos)
	throws Exception
	{
		String raw = (String) ois.readObject();
		StringTokenizer tokenizer = new StringTokenizer(raw, ":");
		String [] temp = new String[5];
		int index = 0;
		while(tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken().trim();
			System.out.println(temp[index]);
			index++;
		}
		String errorCode = null;
		if(index == 5) {
			DBManager manager = DBManager.getInstance();
			try {
				manager.connect(pathToDB);				
				manager.insertSensor(new Sensor(
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
				errorCode = "Internal Server Error";
			}
		} else {
			errorCode = "Error with the number of params.";
		}
		if(errorCode != null) 
			oos.writeObject(errorCode);
		else
			oos.writeObject("OK\n");
		oos.flush();	

		oos.close();
		ois.close();
	}
	
	/** Processes UPDATE command. Updates the given sensor
	 * @param ois
	 * @param oos
	 * @throws Exception
	 */
	private void updateSensor(ObjectInputStream ois, ObjectOutputStream oos)
	throws Exception
	{
		String raw = (String) ois.readObject();
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
					SensorType.valueOf(temp[2]),
					Long.valueOf(temp[3]),
					Boolean.valueOf(temp[4])));
				manager.disconnect();
			} catch (NumberFormatException | SQLException |
				ClassNotFoundException e)
			{
				e.printStackTrace(); 
				errorCode = "Internal Server Error";
			}
		} else {
			errorCode = "Error with the number of params.";
		}
		if(errorCode != null) 
			oos.writeObject(errorCode);
		else
			oos.writeObject("OK\n");
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
		while(tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken();
			index++;
		}
		String errorCode = null;
		if(index == 4) {
			greenDaemon.setPowerSaving(temp[0], temp[1], Boolean.valueOf(temp[2])); // TODO handle error codes here
		} else {
			errorCode = "Error with the number of params.";
		}
		if(errorCode != null) 
			oos.writeObject(errorCode);
		else
			oos.writeObject("OK\n");
		oos.flush();	

		oos.close();
		ois.close();
	}
	
	/** Processes DELETE command. Deletes the given sensor and its readings.
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
		while(tokenizer.hasMoreTokens()) {
			temp[index] = tokenizer.nextToken();
			index++;
		}
		String errorCode = null;
		if(index == 3) {
			DBManager manager = DBManager.getInstance();
			try {
				manager.connect(pathToDB);				
				manager.deleteSensor(temp[0], temp[1]);
				manager.disconnect();
			} catch (NumberFormatException | SQLException | ClassNotFoundException e) {
				e.printStackTrace(); 
				errorCode = "Internal Server Error";
			}
		} else {
			errorCode = "Error with the number of params.";
		}
		if(errorCode != null) 
			oos.writeObject(errorCode);
		else
			oos.writeObject("OK\n");
		oos.flush();	

		oos.close();
		ois.close();
	}

	public static void main(String [] args) throws Exception {
		NetServer ns = new NetServer(null);
		ns.launch();
	}

}

