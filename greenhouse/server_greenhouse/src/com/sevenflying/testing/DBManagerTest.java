package com.sevenflying.testing;

import java.sql.SQLException;

import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.SensorType;
import com.sevenflying.server.domain.exceptions.GreenhouseDatabaseException;

public class DBManagerTest {

	public static void main(String [] args) throws SQLException, ClassNotFoundException {
		DBManager manager = DBManager.getInstance();
		manager.connect("F:\\dump\\greenhouse\\db.sqlite");
		manager.createDatabase();
		Sensor s = new Sensor("whatever", "A05", SensorType.TEMPERATURE, 2100, true);
		Sensor s2 = new Sensor("second sensor", "A05", SensorType.HUMIDITY, 2100, true);
		manager.insertSensor(s);
		manager.insertSensor(s2);
		manager.insertReading(s2, 34);
		manager.insertReading(s, 1);
		manager.insertReading(s, 2);
		manager.insertReading(s, 3);
		manager.insertReading(s, 4);
		manager.insertReading(s, 5);
		
		try {
			System.out.println(s2 + "\n" + manager.getLastReading(s2));
		} catch (GreenhouseDatabaseException e) {
			System.out.println("No readings from sensor " + s2.getName());
		}
		
		try {
			System.out.println(s + "\n" + manager.getLastReading(s));
		} catch (GreenhouseDatabaseException e) {
			System.out.println("No readings from sensor " + s.getName());
		}
		manager.disconnect();
		System.out.println( "$ Test ends");
	}
}
