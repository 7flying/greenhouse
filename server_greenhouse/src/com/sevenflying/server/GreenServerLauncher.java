package com.sevenflying.server;

import java.sql.SQLException;

import com.sevenflying.server.communicator.BlossomController;
import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.SensorType;
import com.sevenflying.server.domain.exceptions.DuplicatedSensorException;

public class GreenServerLauncher {

	public static void main(String [] args) throws ClassNotFoundException,
	SQLException, DuplicatedSensorException
	{
		DBManager manager = DBManager.getInstance();
		manager.connect(DBManager.DBPath);
		manager.createDatabase();

		Sensor dhtTemp = new Sensor("DHT22", "D07",
				SensorType.TEMPERATURE, 2100, true);
		Sensor dhtHumi = new Sensor("DHT22", "D07",
				SensorType.HUMIDITY, 4100, true);
		Sensor photo = new Sensor("Photoresistor ", "A05",
				SensorType.LIGHT, 1000, false);
		manager.insertSensor(dhtTemp);
		manager.insertSensor(dhtHumi);
		manager.insertSensor(photo);
		manager.disconnect();

		BlossomController controller = BlossomController.getInstance("COM5");
		controller.addSensor(dhtTemp);
		controller.addSensor(dhtHumi);
		controller.addSensor(photo);
		
		GreenServer server = new GreenServer("COM5");
		server.run();
	}
}
