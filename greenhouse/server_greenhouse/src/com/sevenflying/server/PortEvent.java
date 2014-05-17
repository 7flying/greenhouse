package com.sevenflying.server;

/** Interface that defines the method called when a port receives data
*/
public interface PortEvent {

	public String dataReceived(String data);
}
