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
		ServerSocket ss = new ServerSocket(5432);
		System.out.println(" $ Server alive ");
		while(true) {
			try {
				s = ss.accept();
				processConnection(s);
				// moveThisToThread()
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void processConnection(Socket s) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			String command = (String) ois.readObject();
			
			
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
	
}

