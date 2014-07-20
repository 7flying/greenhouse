package com.sevenflying.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class NetServer {

	public NetServer() {
		
	}

	public void launch() throws IOException {
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
				// TODO get sensors from database
				System.out.println("$ GETSENSORS received");
				// first tell to the client how many we are sending
				Random r = new Random();
				int number = r.nextInt(10) + 10;
				System.out.println(" $ Generating " + number + " sensors");
				oos.writeObject(Integer.valueOf(number).toString());
				oos.flush();
				while(number > 0) {
					System.out.println("------");
					String [] types = {"T", "H", "L"};
					String [] pinType = {"A", "D"};
					String chosenType = types[r.nextInt(3)];
					String chosenPin = pinType[r.nextInt(2)];
					String s1 = "DHT"+ number +":" + chosenPin + "04:" + chosenType +":2000:" + number;
					System.out.println("$ Generated : " + s1);
					oos.writeObject(s1);
					oos.flush();
					System.out.println("\t$ SENSOR sent");
					String control = (String) ois.readObject();
					if(control.equals("ACK")){
						System.out.println("\t ACK");
						number--;
					} else {
						System.out.println("\t NACK");
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
/*
	public void moveThisToThread() {
		// Read client id from connection
		String id = "";
		if(clients.get(id).hasMoreNotifications()) {
			while(clients.get(id).hasMoreNotifications()) {
				Notification temp = clients.get(id).getNotification();
				// Send notification
			}
		}
	}

	public void moveThisToAnotherThread() {
		// Checks if the alerts of the clients are fired
		// to add a notification to them
		Set<String> keys = clients.keySet();
		for(String key : keys) {
			for(Alert alert : clients.get(key).getAlerts()) {
				if(alert.check())
					clients.get(key).notify(alert);
			}
		}
	}
	*/
	public static void main(String [] args) throws IOException {
		NetServer ns = new NetServer();
		ns.launch();
	}

}

