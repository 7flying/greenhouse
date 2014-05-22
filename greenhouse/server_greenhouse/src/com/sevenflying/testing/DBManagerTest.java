package com.sevenflying.testing;

import java.sql.SQLException;

import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.SensorType;

public class DBManagerTest {

	public static void main(String [] args) throws ClassNotFoundException, SQLException {
		DBManager manager = DBManager.getInstance();
		manager.connect("F:\\dump\\greenhouse\\db.sqlite");
		manager.createDatabase();
		Sensor s = new Sensor("whatever", "A05", SensorType.TEMPERATURE, 200);
		manager.insertSensor(s);
		manager.insertReading(s, 5);
		manager.disconnect();
		System.out.println( " $ Test ends");
	}
}
