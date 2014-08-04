package com.sevenflying.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.exceptions.GreenhouseDatabaseException;

public class DBManager {

	public static String DBPath = "F:\\dump\\greenhouse\\db.sqlite";
	private static DBManager manager = null;
	private Connection conn = null;
	private static String TIME_FORMAT = "HH:mm:ss";
	private static String DATE_FORMAT = "dd/MM/yy";
	private static final String READINGS_TABLE_NAME = "Readings";
	
	public synchronized static DBManager getInstance() {
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
					+ "time TEXT NOT NULL,"
					+ "date TEXT NOT NULL,"
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
	public synchronized void insertSensor(Sensor sensor) throws SQLException {
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
		return getSensorBDid(sensor.getPinId(), Character.toString(sensor.getType().getIdentifier()));
	}
	
	/** Gets the db id of a sensor */
	private int getSensorBDid(String pinId, String type) throws SQLException {
		PreparedStatement pre = conn.prepareStatement("SELECT id FROM Sensors WHERE pinid = ? AND type = ?");
		pre.setString(1, pinId);
		pre.setString(2, type);
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
	private String[] getTimeDate() {
		return new String [] { new SimpleDateFormat(TIME_FORMAT).format(new GregorianCalendar().getTime()), new SimpleDateFormat(DATE_FORMAT).format(new GregorianCalendar().getTime()) };
	}
	
	/** Inserts a new reading from a sensor into the db
	 * @param sensor - sensor that made the reading
	 * @param value - reading
	 * @throws SQLException 	 */
	public synchronized void insertReading(Sensor sensor, double value) throws SQLException {
		int idSensor = getSensorDBid(sensor);
		int maxId = getMaxId(READINGS_TABLE_NAME);
		if(idSensor != -1 && maxId != -1) {
			PreparedStatement pre = conn.prepareStatement("INSERT into Readings values (?, ?, ?, ?, ?);");
			pre.setInt(1, maxId + 1);
			pre.setInt(2, idSensor);
			pre.setDouble(3, value);
			String[] timedate = getTimeDate();
			pre.setString(4, timedate[0] );
			pre.setString(5, timedate[1]);
			pre.executeUpdate();
			pre.close();
			sensor.setLastRefresh(timedate[0] + " - " +timedate[1]);
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
	
	/** Returns all the sensors from the database.
	 * @return list of sensors
	 * @throws SQLException
	 */
	public List<Sensor> getSensors() throws SQLException {
		List<Sensor> ret = new ArrayList<Sensor>();
		Statement sta = conn.createStatement();
		ResultSet result = sta.executeQuery("SELECT * FROM Sensors;");
		while(result.next()){
			Sensor s = new Sensor();
			s.setName(result.getString(2));
			s.setPinId(result.getString(3));
			s.setType(result.getString(4).charAt(0));
			s.setRefreshRate(result.getLong(5));
			ret.add(s);			
		}
		result.close();
		return ret;
	}
	
	/** Retrieves the last X values from a given sensor.
	 * @param lastX - number of maximum values to retrieve
	 * @param sensor - sensor to check
	 * @return list with maps with date-value
	 * @throws SQLException 
	 */
	public List<Map<String, Double>> getLastXFromSensor(int lastX, String pinId, String type) throws SQLException {
		List<Map<String, Double>> ret = new ArrayList<Map<String, Double>>();
		int id = getSensorBDid(pinId, type);
		PreparedStatement pre = conn.prepareStatement("SELECT time, date, value FROM Readings WHERE idsensor = ? ORDER BY id DESC LIMIT ?;");
		pre.setInt(1, id);
		pre.setInt(2, lastX);
		ResultSet result = pre.executeQuery();
		while(result.next()) {
			Map<String, Double> map = new HashMap<String, Double>();
			map.put(result.getString(1) + " - " + result.getString(2), result.getDouble(3));
			ret.add(map);
		}
		result.close();
		pre.close();
		return ret;
	}
	
}
