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
						default:
							break;
					}
					s.close();
					oos.close();
					ois.close();
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
		DBManager manager = DBManager.getInstance();
		manager.connect(pathToDB);
		Map<String, Double> history = manager.getLastXFromSensor(100, pinidType.substring(0, pinidType.indexOf(':')), pinidType.substring(pinidType.indexOf(':') + 1));
		manager.disconnect();
		// Tell to the client how many values it has to expect
		int number = history.keySet().size();
		oos.writeObject(Integer.valueOf(number).toString());
		oos.flush();
		// Send data
		int index = 0, error = 0;
		@SuppressWarnings("unchecked")
		List<String> keys = (List<String>) history.keySet();
		while(number > 0) {
			oos.writeObject(Utils.encode64(keys.get(index)) + ":" + Utils.encode64(history.get(keys.get(index))));
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
	
	public static void main(String [] args) throws Exception {
		NetServer ns = new NetServer();
		ns.launch();
	}

}

