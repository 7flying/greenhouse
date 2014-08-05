package com.sevenflying.server.net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utils.Utils;

import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.exceptions.GreenhouseDatabaseException;

public class NetServer {

	private static String pathToDB = "F:\\dump\\greenhouse\\db.sqlite"; //TODO

	public NetServer() {

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
		System.out.println(" $ Incoming connection:" +  s.getInetAddress().toString() + " " + s.getLocalAddress());
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
					ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
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
					default:
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
	public void getSensorValues(ObjectInputStream ois, ObjectOutputStream oos) throws Exception {
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
	public void getSensorHistory(ObjectInputStream ois, ObjectOutputStream oos) throws Exception {
		// Read sensor pinid and type
		String pinidType = (String) ois.readObject();
		System.out.println("\t -Params: " + pinidType);
		DBManager manager = DBManager.getInstance();
		manager.connect(pathToDB);
		List<Map<String, Double>> history = manager.getLastXFromSensor(5, pinidType.substring(0, pinidType.indexOf(':')), pinidType.substring(pinidType.indexOf(':') + 1));
		manager.disconnect();
		// Tell to the client how many values it has to expect
		int number = history.size();
		oos.writeObject(Integer.valueOf(number).toString());
		oos.flush();
		// Send data
		int index = 0, error = 0;

		while(number > 0) {
			Map<String, Double> map = history.get(index);
			String toWrite = Utils.encode64((String) map.keySet().toArray()[0]) + ":" + Utils.encode64(map.get((String) map.keySet().toArray()[0]));
			oos.writeObject(toWrite);
			oos.flush();
			String control = (String) ois.readObject();
			if(control.equals("ACK")){
				System.out.println("\t ACK " + (String) map.keySet().toArray()[0] + ":" + map.get((String) map.keySet().toArray()[0]));
				number--;
				index++;
				error = 0;
			} else {
				System.out.println("\t NACK " + (String) map.keySet().toArray()[0] + ":" + map.get((String) map.keySet().toArray()[0]));
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
	public void getSensorLastValue(ObjectInputStream ois, ObjectOutputStream oos) throws Exception {
		// Read sensor pinid and type
		String pinidType = (String) ois.readObject();
		DBManager manager = DBManager.getInstance();
		manager.connect(pathToDB);
		try{
			double reading = manager.getLastReading(pinidType.substring(0, pinidType.indexOf(':')), pinidType.substring(pinidType.indexOf(':') + 1));
			oos.writeObject(Utils.encode64(Double.valueOf(reading).toString()));
			oos.flush();
			System.out.println(reading);
		}catch(GreenhouseDatabaseException e) {
			oos.writeObject("X");
			oos.flush();
		}
		manager.disconnect();
		oos.close();
		ois.close();
	}

	public static void main(String [] args) throws Exception {
		NetServer ns = new NetServer();
		ns.launch();
	}

}

