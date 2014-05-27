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
	
	public void notify(Alert alert) {
		//TODO Check if the notification is repeated before adding it
		notifications.add(new Notification(id, alert.getLastValue(), alert.getSensor().getName(), alert.getSensor().getType()));
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