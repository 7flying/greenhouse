package com.sevenflying.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.Sensor;

public class NetServer {

	private static String pathToDB = "somepath"; //TODOs
	
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

	public void processConnection(Socket s) {
		System.out.println(" $ Incoming connection:" +  s.getInetAddress().toString());
		try {
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			String command = (String) ois.readObject();
			System.out.println("$ Received '" + command + "'");
			if(command.contains(Constants.GETSENSORS)) {
				// TODO call getSensorValues(ois, oos);
				Random r = new Random();
				int number = r.nextInt(5) + 1;
				System.out.println(" $ Generating " + number + " sensors");
				oos.writeObject(Integer.valueOf(number).toString());
				oos.flush();
				while(number > 0) {
					System.out.println("------");
					String [] types = {"T", "H", "L"};
					String [] pinType = {"A", "D"};
					String chosenSensorType = types[r.nextInt(3)];
					String chosenPinType = pinType[r.nextInt(2)];
					String chosenPinNumber = Integer.toString(r.nextInt(10));
					String s1 = "DHT"+ number +":" + chosenPinType + "0" + chosenPinNumber + ":" + chosenSensorType +":2000:" + number;
					System.out.print("$ Generated : " + s1);
					oos.writeObject(s1);
					oos.flush();
					System.out.print(", SENT");
					String control = (String) ois.readObject();
					if(control.equals("ACK")){
						System.out.println(": ACK");
						number--;
					} else {
						System.out.println(": NACK");
					}
					System.out.println("------");
				}
			}
			s.close();
			oos.close();
			ois.close();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
			ret.add(s.getName() + ":" + s.getPinId() + ":" + s.getType().getIdentifier() + ":" + s.getRefreshRate() + ":" + s.getLastValue());
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
	
	public static void main(String [] args) throws Exception {
		NetServer ns = new NetServer();
		ns.launch();
	}

}

