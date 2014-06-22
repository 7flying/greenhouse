package com.sevenflying.server.domain;

import java.util.ArrayList;

import com.sevenflying.server.net.domain.Notification;

public class Client {

	private String id;
	private ArrayList<Notification> notifications;
	private ArrayList<Alert> alerts;
	
	public Client(String id) {
		this.id = id;
		this.notifications = new ArrayList<Notification>();
		this.alerts = new ArrayList<Alert>();
	}
	
	/** Adds a Notification to the client's notification list.
	 *  If there was a previous one the value is updated.
	 * @param alert - Alert 
	 */
	public void notify(Alert alert) {
		Notification temp = new Notification(id, alert.getLastValue(), alert.getSensor().getName(), alert.getSensor().getType());
		// Check if the notification is repeated if so, update the sensor's value
		if(notifications.contains(temp)) {
			notifications.remove(temp);
		}
		notifications.add(temp);
	}

	public String getId() {
		return id;
	}

	public Notification getNotification() {
		return notifications.remove(0);
	}
	
	public boolean hasMoreNotifications() {
		return (notifications.size() > 0) ? true : false;
	}
	
	public ArrayList<Alert> getAlerts() {
		return alerts;
	}

	public void setAlert(Alert alert) {
		this.alerts.add(alert);
	}	
}
