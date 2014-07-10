package com.sevenflying.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import com.sevenflying.server.domain.Alert;
import com.sevenflying.server.domain.Client;
import com.sevenflying.server.net.domain.Notification;

public class NetServer {

	private HashMap<String, Client> clients;

	public NetServer() {
		clients = new HashMap<String, Client>();
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
				System.out.println("$ GETSENSORS received");
				String cs = "DHT22:D04:T:2000:26.5\n";
				oos.writeObject(cs);
				oos.flush();
				System.out.println("$ SENSOR sent");
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
	public static void main(String [] args) throws IOException {
		NetServer ns = new NetServer();
		ns.launch();
	}

}

