package com.sevenflying.server;

import java.sql.SQLException;

import com.sevenflying.server.communicator.BlossomController;
import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.SensorType;

public class GreenServerLauncher {

	public static void main(String [] args) throws ClassNotFoundException, SQLException {
		DBManager manager = DBManager.getInstance();
		manager.connect(DBManager.DBPath);
		manager.createDatabase();

		Sensor dht = new Sensor("DHT22", "D07", SensorType.TEMPERATURE, 2100);
		Sensor photo = new Sensor("Photoresistor ", "A05", SensorType.LIGHT, 1000);
		manager.insertSensor(dht);
		manager.insertSensor(photo);
		manager.disconnect();

		BlossomController controller = BlossomController.getInstance("COM5");
		controller.addSensor(dht);
		controller.addSensor(photo);
		
		GreenServer server = new GreenServer("COM5");
		server.run();
	}
}
