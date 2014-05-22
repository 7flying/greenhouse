package com.sevenflying.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sevenflying.server.domain.BlossomSensor;
import com.sevenflying.server.domain.Sensor;

public class DBManager {

	private static DBManager manager = null;
	private Connection conn = null;
	
	public static DBManager getInstance() {
		if(manager == null)
			manager = new DBManager();
		return manager;
	}
	
	public void createDatabase() {
		try {
			Statement sta = conn.createStatement();
			sta.executeUpdate("DROP TABLE IF EXISTS Sensors;");
			sta.executeUpdate("CREATE TABLE Sensors ("
					+ "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "name TEXT NOT NULL,"
					+ "pinid TEXT NOT NULL," // cannot be unique since a sensor may give two different type of readings (temp+humi)
					+ "type char(1) NOT NULL,"
					+ "refresh INTEGER NOT NULL check (refresh > 0)"
					+ ");");
			
			sta.executeUpdate("DROP TABLE IF EXISTS Readings;");
			sta.executeUpdate("CREATE TABLE Readings ("
					+ "id INTEGER NOT NULL,"
					+ "idsensor INTEGER NOT NULL references Sensors(id) ON DELETE CASCADE,"
					+ "value REAL NOT NULL,"
					+ "PRIMARY KEY (id, idsensor)"
					+ ");");
			
			sta.close();
		} catch(SQLException e) {
			System.err.println("Error at database creation.");
			e.printStackTrace();
		}
	}
	
	public void connect(String pathToDB) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + pathToDB);
	}
	
	public void disconnect() throws SQLException {
		if(conn != null)
			conn.close();
	}
	
	/** Inserts the given sensor into the database
	 * @param sensor - to insert
	 * @throws SQLException
	 */
	public void insertSensor(Sensor sensor) throws SQLException {
		PreparedStatement pre = conn.prepareStatement("INSERT into Sensors (name, pinid, type, refresh) values (?,?,?,?);");
		pre.setString(1, sensor.getName());
		pre.setString(2, sensor.getPinId());
		pre.setString(3, Character.toString(sensor.getType().getIdentifier()));
		pre.setDouble(4, sensor.getRefreshRate());
		pre.executeUpdate();
	}
	
	private int getSensorDBid(Sensor sensor) throws SQLException {
		PreparedStatement pre = conn.prepareStatement("SELECT id FROM Sensors WHERE pinid = ? AND type = ?;");
		pre.setString(1, sensor.getPinId());
		pre.setString(2, Character.toString(sensor.getType().getIdentifier()));
		ResultSet result = pre.executeQuery();
		int ret = -1;
		if (result != null) {
			ret = result.getInt(1);
		}
		result.close();
		pre.close();
				
		return ret;
	}
	
	public void insertReading(Sensor sensor, double value) throws SQLException {
		System.out.println(getSensorDBid(sensor));
	}
	
}
