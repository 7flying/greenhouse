package com.sevenflying.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.exceptions.GreenhouseDatabaseException;

public class DBManager {

	public static String DBPath = "F:\\dump\\greenhouse\\db.sqlite";
	private static DBManager manager = null;
	private Connection conn = null;
	private static String TIME_DATE_FORMAT = "dd/MM/yy - HH:mm:ss";
	private static final String READINGS_TABLE_NAME = "Readings";
	
	public static DBManager getInstance() {
		if(manager == null)
			manager = new DBManager();
		return manager;
	}
	
	/** Creates the db, deletes previous tables if they exist */
	public void createDatabase() {
		try {
			Statement sta = conn.createStatement();
			sta.executeUpdate("DROP TABLE IF EXISTS Sensors;");
			sta.executeUpdate("CREATE TABLE Sensors ("
					+ "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "name TEXT NOT NULL,"
					+ "pinid TEXT NOT NULL," // cannot be unique since a sensor may give two different type of readings (temp+humi)
											 // a 'real' sensor may count as two different db sensors with the same pinid
					+ "type char(1) NOT NULL,"
					+ "refresh INTEGER NOT NULL check (refresh > 0)"
					+ ");");
			
			sta.executeUpdate("DROP TABLE IF EXISTS Readings;");
			sta.executeUpdate("CREATE TABLE Readings ("
					+ "id INTEGER NOT NULL,"
					+ "idsensor INTEGER NOT NULL references Sensors(id) ON DELETE CASCADE,"
					+ "value REAL NOT NULL,"
					+ "datetime TEXT NOT NULL,"
					+ "PRIMARY KEY (id, idsensor)"
					+ ");");
			
			sta.close();
			System.out.println("$ Db created.");
		} catch(SQLException e) {
			System.err.println("Error at database creation.");
			e.printStackTrace();
		}
	}
	
	/** Connects to the given db
	 * @param pathToDB - path of the database
	 * @throws ClassNotFoundException
	 * @throws SQLException	 */
	public void connect(String pathToDB) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + pathToDB);
	}
	
	/** Disconnects from the db
	 * @throws SQLException	 */
	public void disconnect() throws SQLException {
		if(conn != null)
			conn.close();
	}
	
	/** Inserts the given sensor into the database
	 * @param sensor - to insert
	 * @throws SQLException	 */
	public void insertSensor(Sensor sensor) throws SQLException {
		PreparedStatement pre = conn.prepareStatement("INSERT into Sensors (name, pinid, type, refresh) values (?,?,?,?);");
		pre.setString(1, sensor.getName());
		pre.setString(2, sensor.getPinId());
		pre.setString(3, Character.toString(sensor.getType().getIdentifier()));
		pre.setDouble(4, sensor.getRefreshRate());
		pre.executeUpdate();
		pre.close();
	}
	
	/** Gets the db id of a sensor */
	private int getSensorDBid(Sensor sensor) throws SQLException {
		PreparedStatement pre = conn.prepareStatement("SELECT id FROM Sensors WHERE pinid = ? AND type = ?;");
		pre.setString(1, sensor.getPinId());
		pre.setString(2, Character.toString(sensor.getType().getIdentifier()));
		ResultSet result = pre.executeQuery();
		int ret = -1;
		if (result.next()) {
			ret = result.getInt(1);
		}
		result.close();
		pre.close();
				
		return ret;
	}
	
	/** Gets the maximun id of a table */
	private int getMaxId(String tableName) throws SQLException {
		Statement sta = conn.createStatement();
		String query = "SELECT max(id) from " + tableName + ";";
		ResultSet result = sta.executeQuery(query);
		int ret = -1;
		if(result.next()) 
			ret = result.getInt(1);
		result.close();
		sta.close();
		return ret;
	}
	
	/** Returns current time-date */
	private String getTimeDate() {
		return new SimpleDateFormat(TIME_DATE_FORMAT).format(new GregorianCalendar().getTime());
	}
	
	/** Sets the db's time-date format for storing time-dates, default: 'dd/MM/yy - HH:mm:ss' */
	public void setTimeDateFormat(String format) {
		TIME_DATE_FORMAT = format;
	}
	
	/** Inserts a new reading from a sensor into the db
	 * @param sensor - sensor that made the reading
	 * @param value - reading
	 * @throws SQLException 	 */
	public void insertReading(Sensor sensor, double value) throws SQLException {
		int idSensor = getSensorDBid(sensor);
		int maxId = getMaxId(READINGS_TABLE_NAME);
		if(idSensor != -1 && maxId != -1) {
			PreparedStatement pre = conn.prepareStatement("INSERT into Readings values (?, ?, ?, ?);");
			pre.setInt(1, maxId + 1);
			pre.setInt(2, idSensor);
			pre.setDouble(3, value);
			String timedate = getTimeDate();
			pre.setString(4, timedate);
			pre.executeUpdate();
			pre.close();
			sensor.setLastRefresh(timedate);
		}
	}
	
	/** Returns the last reading of a sensor
	 * @param sensor - sensor to take the stored last reading from
	 * @return last reading
	 * @throws SQLException
	 * @throws GreenhouseDatabaseException - when there aren't readings from a sensor
	 */
	public double getLastReading(Sensor sensor) throws SQLException, GreenhouseDatabaseException {
		int idSensor = getSensorDBid(sensor);
		PreparedStatement pre = conn.prepareStatement("SELECT value FROM Readings where idsensor = ? and id = (SELECT max(id) from Readings where idsensor = ?);");
		pre.setInt(1, idSensor);
		pre.setInt(2, idSensor);
		ResultSet result = pre.executeQuery();
		double ret = -2323;
		boolean reading = false;
		if(result.next()) {
			ret = result.getDouble(1);
			reading = true;
		}
		result.close();
		pre.close();
		if(reading)
			return ret;
		else 
			throw new GreenhouseDatabaseException();
	}
	
}
