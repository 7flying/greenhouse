package com.sevenflying.server;

import java.util.Scanner;

/**
 * Uses the Communicator to send/receive data
 */
public class CommunicationsHandler implements PortEvent {

	private Communicator communicator;

	public CommunicationsHandler() {
		communicator = new Communicator(this);
	}

	public void connect(String portName, int dataRate) {
		communicator.connect(portName, dataRate);
	}

	public void close() {
		communicator.close();
	}

	public String dataReceived(String data) {
		// TODO
		System.out.println("Received: " + data);
		return data;
	}

	public void sendData(String data) {
		communicator.sendData(data);
	}

	public static void main(String[] args) {
		final CommunicationsHandler handler = new CommunicationsHandler();
		final Scanner sca = new Scanner(System.in);
		try {
			handler.connect("COM5", Communicator.DATA_RATE);
			Thread t = new Thread(new Runnable() {
				public void run() {
					// Simple echo to see that this works
					String toWrite = sca.nextLine();
					while(!toWrite.equals("END")) {
						handler.sendData(toWrite);
						toWrite = sca.nextLine();
					}
					sca.close();
				}
			});
			t.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
