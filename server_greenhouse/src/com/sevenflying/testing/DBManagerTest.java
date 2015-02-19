package com.sevenflying.testing;

import java.util.List;
import java.util.Map;

import com.sevenflying.server.Env;
import com.sevenflying.server.database.DBManager;
import com.sevenflying.server.domain.Actuator;
import com.sevenflying.server.domain.ActuatorType;
import com.sevenflying.server.domain.CompareType;
import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.SensorType;

public class DBManagerTest {

	public static void main(String [] args) throws Exception {
		test_1();
		System.out.println(" $ Tests finished");
	}

	public static void test_1() throws Exception {
		/*
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
		 */
		DBManager manager = DBManager.getInstance();
		manager.connect(Env.DB_PATH);
		manager.createDatabase();
		Sensor s1 = new Sensor("DHT22", "D03", SensorType.TEMPERATURE,60000, true);
		Sensor s2 = new Sensor("DHT22", "D03", SensorType.HUMIDITY, 60000, true);
		Sensor s3 = new Sensor("Photo", "A04", SensorType.LIGHT, 60000, true);
		manager.insertSensor(s1);
		manager.insertSensor(s2);
		manager.insertSensor(s3);
		// Values
		for(int i = 1; i < 20 ; i++) {
			manager.insertReading(s1, i);
			manager.insertReading(s2, i * 10);
			manager.insertReading(s3, i * 100);
		}


		System.out.println("$ Testing data created");
		List<Sensor> list = manager.getSensors();
		for(Sensor se : list) {
			System.out.println(se.toString());
			System.out.println(manager.getLastReading(se));
		}

		//Map<String, Double> map = manager.getLastXFromSensor(1, "D03", "T");
		//	for(String key : map.keySet())
		//		System.out.println( map.get(key) + " @ " + key);

	}

	public static void test_2() throws Exception {
		DBManager manager = DBManager.getInstance();
		manager.connect(Env.DB_PATH);
		// manager.updateSensor(new Sensor("Anything can happen", "D07",
		// SensorType.HUMIDITY, 60000, true));
		manager.deleteSensor(new Sensor("Whatever", "D07", SensorType.HUMIDITY,
				60000, true));
		manager.disconnect();
	}
	
	public static void test_3() throws Exception {
		DBManager manager = DBManager.getInstance();
		manager.connect(Env.DB_PATH);
		manager.createDatabase();
		Sensor sensor = new Sensor("Whatever", "D07", SensorType.HUMIDITY,
				60000, true);
		manager.insertSensor(sensor);
		Actuator actuator = new Actuator("Some actuator", "A45",
				ActuatorType.PUMP);
		Actuator actuatorTwo = new Actuator("Some other", "D44", 
				ActuatorType.PUMP, sensor,CompareType.EQUAL, 34);
		// Inserts
		manager.insertActuator(actuator);
		manager.insertActuator(actuatorTwo);
		System.out.println(" -- INSERTS --");
		for (Actuator a : manager.getActuators())
			System.out.println(a);
		// Updates
		actuator.setName("CHANGED");
		manager.updateActuator(actuator);
		System.out.println(" -- UPDATE 1 --");
		for (Actuator a : manager.getActuators())
			System.out.println(a);
		actuator.setControlSensor(sensor);
		actuator.setCompareType(CompareType.LESS);
		actuator.setCompareValue(33333);
		manager.updateActuator(actuator);
		System.out.println("-- UPDATE 2 --");
		for (Actuator a : manager.getActuators())
			System.out.println(a);
		// Deletes
		manager.deleteActuator(actuator);
		System.out.println("-- DELETE --");
		for (Actuator a : manager.getActuators())
			System.out.println(a);
		manager.deleteSensor(sensor);
		System.out.println("-- DELETE CASCADE --");
		for (Actuator a : manager.getActuators())
			System.out.println(a);
		manager.disconnect();
	}
}
