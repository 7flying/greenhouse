package com.sevenflying.server.communicator;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;


/**
 * Manages the communication between the greenhouse and the server.
 * @author 7flying
 */
public class Communicator implements SerialPortEventListener {

	// Holds a map of portname - port
	private HashMap<String, CommPortIdentifier> portMap = null;
	// The port identifier
	private CommPortIdentifier selectedPortId = null;
	// The port that we are using to communicate
	private SerialPort serialPort = null;
	// Input/output writers
	private BufferedReader input = null;
	private BufferedWriter output = null;
	// For the callbacks when data is received
	private PortEvent portEvent;

	// Bits per second
	public static final int DATA_RATE = 115200;
	// Timeout for connecting to a port
	private static final int PORT_CONNECT_TIMEOUT = 2000;

	private boolean debugMode = false;
	
	public Communicator(PortEvent portEvent) {
		this.portEvent = portEvent;
		this.portMap = new HashMap<String, CommPortIdentifier>();
		searchPorts();
	}

	/** Searches ports and stores the serial ports in the portname - port hashmap */
	@SuppressWarnings("unchecked")
	private void searchPorts() {
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier
				.getPortIdentifiers();

		while (ports.hasMoreElements()) {
			// Insert ports in the map
			CommPortIdentifier tempPort = (CommPortIdentifier) ports
					.nextElement();
			if (tempPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				portMap.put(tempPort.getName(), tempPort);
				System.out.println(" Port name: " + tempPort.getName()
						+ " object: " + tempPort);
			} else {
				System.out.println(" Port: " + tempPort.getName() 
						+ " not serial");
			}
		}
	}

	/** Connects to the given portname.
	 * @param portName - portname to connect to 
	 * @throws NoSuchPortNameException - when the provided portName does no exist
	 */
	public void connect(String portName, int dataRate) {
		
			selectedPortId = (CommPortIdentifier) portMap.get(portName);
			try {
				serialPort = (SerialPort) selectedPortId.open(portName,
						PORT_CONNECT_TIMEOUT);
				serialPort.setSerialPortParams(
						dataRate,
						SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE); 
				//serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
				// | SerialPort.FLOWCONTROL_RTSCTS_OUT);
				//serialPort.setRTS(true);
				// Open the stream
				input = new BufferedReader(new InputStreamReader(
						serialPort.getInputStream()));
				output = new BufferedWriter(new OutputStreamWriter(
						serialPort.getOutputStream()));
				// Add listeners
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
			} catch(PortInUseException | TooManyListenersException
					| UnsupportedCommOperationException | IOException e)
			{
				e.printStackTrace();
			} 
		
	}

	/** Closes the port */
	public synchronized void close() {
		if (serialPort != null) {
			try {
				input.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			serialPort.removeEventListener();
			serialPort.close();
		}
	}
	
	@Override
	/** Called when data is comming from the serial port */
	public void serialEvent(SerialPortEvent anEvent) {
		
		if (anEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				if (debugMode)
					System.out.println(input.readLine());
				else
					portEvent.dataReceived(input.readLine());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Sends data to the port
	 * @param data - data to sent
	 */
	public void sendData(String data) {
		try {
			output.write(data);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 
	public void setDebugMode(boolean activate) {
		this.debugMode = activate;
	}
}
