package com.sevenflying.server.communicator;

/** Interface that defines the method called when a port receives data.
 * @author 7flying
*/
public interface PortEvent {

	/** Callback method
	 * @param data
	 */
	public void dataReceived(String data);
}
