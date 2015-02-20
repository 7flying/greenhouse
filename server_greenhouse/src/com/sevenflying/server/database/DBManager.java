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

import com.sevenflying.server.Env;
import com.sevenflying.server.domain.Actuator;
import com.sevenflying.server.domain.ActuatorType;
import com.sevenflying.server.domain.CompareType;
import com.sevenflying.server.domain.Sensor;
import com.sevenflying.server.domain.SensorType;
import com.sevenflying.server.domain.exceptions.DuplicatedActuatorException;
import com.sevenflying.server.domain.exceptions.DuplicatedSensorException;
import com.sevenflying.server.domain.exceptions.GreenhouseDatabaseException;
import com.sevenflying.server.domain.exceptions.NoDataException;
import com.sevenflying.server.domain.exceptions.NoSuchActuatorException;
import com.sevenflying.server.domain.exceptions.NoSuchSensorException;

/** DB manager.
 * @author flying
 */
public class DBManager {

	public static String DBPath = Env.DB_PATH;
	private static DBManager manager = null;
	private Connection conn = null;
	private static String TIME_FORMAT = "HH:mm:ss";
	private static String DATE_FORMAT = "dd/MM/yy";
	private static final String READINGS_TABLE_NAME = "Readings";
	
	public synchronized static DBManager getInstance() {
		if (manager == null)
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
		// Pinid cannot be unique since a sensor may give two different type of
		// readings (temp+humi). A 'real' sensor may count as two different
		// db sensors with the same pinid
					+ "pinid TEXT NOT NULL,"
					+ "type char(1) NOT NULL,"
					+ "refresh INTEGER NOT NULL check (refresh > 0)"
					+ ");");
			
			sta.executeUpdate("DROP TABLE IF EXISTS Readings;");
			sta.executeUpdate("CREATE TABLE Readings ("
					+ "id INTEGER NOT NULL,"
					+ "idsensor INTEGER NOT NULL references Sensors(id)"
						+ " ON DELETE CASCADE,"
					+ "value REAL NOT NULL,"
					+ "time TEXT NOT NULL,"
					+ "date TEXT NOT NULL,"
					+ "PRIMARY KEY (id, idsensor)"
					+ ");");
			
			sta.executeUpdate("DROP TABLE IF EXISTS Actuators;");
			sta.executeUpdate("CREATE TABLE Actuators ("
					+ "pinid TEXT NOT NULL PRIMARY KEY,"
					+ "name TEXT NOT NULL,"
					+ "type char(1) NOT NULL,"
					+ "idSensor INTEGER references Sensors(id)"
						+ " ON DELETE CASCADE,"
					+ "compareType TEXT,"
					+ "compareValue REAL)");
			sta.close();
			System.out.println("$ Db created.");
		} catch(SQLException e) {
			System.err.println("Error at database creation.");
			e.printStackTrace();
		}
	}
	
	// -- Admin --
	
	/** Connects to the given db
	 * @param pathToDB - path of the database
	 * @throws ClassNotFoundException
	 * @throws SQLException	 */
	public void connect(String pathToDB) throws ClassNotFoundException,
	SQLException
	{
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + pathToDB);
		// General settings
		Statement sta= conn.createStatement();
		sta.execute("PRAGMA foreign_keys = ON"); // to enable ON DELETE CASCADE
		sta.close();
		
	}
	
	/** Disconnects from the db
	 * @throws SQLException	 */
	public void disconnect() throws SQLException {
		if(conn != null)
			conn.close();
	}
	
	/** Gets the maximum id of a table */
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
		return new String [] { new SimpleDateFormat(TIME_FORMAT)
			.format(new GregorianCalendar().getTime()),
					new SimpleDateFormat(DATE_FORMAT)
						.format(new GregorianCalendar().getTime()) };
	}
	
	// -- Sensors --
	
	/** Returns a sensor given its db id.
	 * @param dbId - id of the sensor
	 * @return sensor
	 * @throws SQLException
	 * @throws NoSuchSensorException 
	 */
	public Sensor getSensor(int dbId) throws SQLException,
	NoSuchSensorException
	{
		Sensor ret = null;
		if (dbId != -1) {
			PreparedStatement pre = conn.prepareStatement("SELECT * FROM Sensors"
					+ " WHERE id = ?");
			pre.setInt(1, dbId);
			ResultSet result = pre.executeQuery();
			if (result.next()) {
				ret = new Sensor();
				ret.setName(result.getString(2));
				ret.setPinId(result.getString(3));
				ret.setType(result.getString(4).charAt(0));
				ret.setRefreshRate(result.getLong(5));
			}
			result.close();
			pre.close();
		} else
			throw new NoSuchSensorException();
		return ret;
	}
	
	/** Returns a sensor given its pinId and type
	 * @param type - type of the sensor
	 * @param pinId - pinid of the sensor
	 * @return
	 * @throws SQLException
	 * @throws NoSuchSensorException 
	 */
	public Sensor getSensor(SensorType type, String pinId) throws SQLException,
	NoSuchSensorException
	{
		Sensor ret = null;
		PreparedStatement pre = conn.prepareStatement("SELECT * FROM Sensors "
				+ " WHERE pinid = ? AND type = ?;");
		pre.setString(1, pinId);
		pre.setString(2, Character.toString(type.getIdentifier()));
		ResultSet result = pre.executeQuery();
		if (result.next()) {
			ret = new Sensor();
			ret.setName(result.getString(2));
			ret.setPinId(result.getString(3));
			ret.setType(result.getString(4).charAt(0));
			ret.setRefreshRate(result.getLong(5));
		}
		result.close();
		pre.close();
		if (ret == null)
			throw new NoSuchSensorException();
		return ret;
	}
	
	/** Inserts the given sensor into the database
	 * @param sensor - to insert
	 * @throws SQLException	 
	 * @throws DuplicatedSensorException */
	public synchronized void insertSensor(Sensor sensor) throws SQLException,
	DuplicatedSensorException
	{
		if (getSensorDBid(sensor) != -1)
			throw new DuplicatedSensorException();
		PreparedStatement pre = conn.prepareStatement("INSERT into Sensors"
				+ " (name, pinid, type, refresh) values (?,?,?,?);");
		pre.setString(1, sensor.getName());
		pre.setString(2, sensor.getPinId());
		pre.setString(3, Character.toString(sensor.getType().getIdentifier()));
		pre.setDouble(4, sensor.getRefreshRate());
		pre.executeUpdate();
		pre.close();
	}
	
	/** Deletes the given sensor from the db.
	 * @param sensor - to delete
	 * @throws SQLException
	 */
	public synchronized void deleteSensor(Sensor sensor) throws SQLException {
		deleteSensor(sensor.getPinId(), Character.toString(
				sensor.getType().getIdentifier()));
	}
	
	public synchronized void deleteSensor(String pinId, String type)
	throws SQLException
	{
		PreparedStatement pre = conn.prepareStatement("DELETE FROM Sensors "
				+ "WHERE id = ?;");
		int id = getSensorBDid(pinId, type);
		if (id != -1) {
			pre.setInt(1, id);
			pre.executeUpdate();
			pre.close();
		}
	}
	
	/** Updates the sensor.
	 * @param sensor - to update
	 * @throws SQLException
	 */
	public synchronized void updateSensor(Sensor sensor) throws SQLException {
		System.out.println(" $$ db sensor:" + sensor.toString());
		PreparedStatement pre = conn.prepareStatement("UPDATE Sensors "
				+ "SET name = ?, refresh = ? WHERE id = ?;");
		int id = getSensorDBid(sensor);
		System.out.println(" $$ SENSOR DB ID: " + id);
		if(id != -1) {
			pre.setString(1, sensor.getName());
			pre.setDouble(2, sensor.getRefreshRate());
			pre.setInt(3, id);
			int ret = pre.executeUpdate();
			System.out.println(" $$ UPDATED ROWS: " + ret);
			pre.close();
		}
	}
	
	/** Gets the db id of a sensor */
	private int getSensorDBid(Sensor sensor) throws SQLException {
		return getSensorBDid(sensor.getPinId(),
				Character.toString(sensor.getType().getIdentifier()));
	}
	
	/** Gets the db id of a sensor */
	private int getSensorBDid(String pinId, String type) throws SQLException {
		PreparedStatement pre = conn.prepareStatement("SELECT id FROM Sensors"
				+ " WHERE pinid = ? AND type = ?");
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
	
	/** Inserts a new reading from a sensor into the db
	 * @param sensor - sensor that made the reading
	 * @param value - reading
	 * @throws SQLException 	 */
	public synchronized void insertReading(Sensor sensor, double value)
	throws SQLException
	{
		int idSensor = getSensorDBid(sensor);
		int maxId = getMaxId(READINGS_TABLE_NAME);
		if(idSensor != -1 && maxId != -1) {
			PreparedStatement pre = conn.prepareStatement("INSERT into Readings"
					+ " values (?, ?, ?, ?, ?);");
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
	 * @throws GreenhouseDatabaseException - when there aren't readings
	 *  from a sensor
	 */
	public double getLastReading(Sensor sensor) throws SQLException,
	NoDataException
	{
		int idSensor = getSensorDBid(sensor);
		PreparedStatement pre = conn.prepareStatement("SELECT value FROM"
				+ " Readings WHERE idsensor = ? and id = "
				+ "(SELECT max(id) FROM Readings WHERE idsensor = ?);");
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
			throw new NoDataException();
	}
	
	/** Returns the last reading of a sensor
	 * @param pinId - sensor's pin id
	 * @param type - sensor's type
	 * @return last reading
	 * @throws SQLException
	 * @throws GreenhouseDatabaseException - when there aren't readings
	 *  from a sensor
	 */
	public double getLastReading(String pinId, String type) throws SQLException,
	NoDataException
	{
		int id = getSensorBDid(pinId, type);
		PreparedStatement pre = conn.prepareStatement("SELECT value FROM"
				+ "Readings WHERE idsensor = ? and id = "
				+ "(SELECT max(id) FROM Readings WHERE idsensor = ?);");
		pre.setInt(1, id);
		pre.setInt(2, id);
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
			throw new NoDataException();
	}
	
	/** Returns all the sensors from the database.
	 * @return list of sensors
	 * @throws SQLException
	 */
	public List<Sensor> getSensors() throws SQLException {
		List<Sensor> ret = new ArrayList<Sensor>();
		Statement sta = conn.createStatement();
		ResultSet result = sta.executeQuery("SELECT * FROM Sensors;");
		while (result.next()){
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
	public List<Map<String, Double>> getLastXFromSensor(int lastX, String pinId,
			String type) throws SQLException
	{
		List<Map<String, Double>> ret = new ArrayList<Map<String, Double>>();
		int id = getSensorBDid(pinId, type);
		PreparedStatement pre = conn.prepareStatement("SELECT time, date, value"
				+ " FROM Readings WHERE idsensor = ?"
				+ " ORDER BY id DESC LIMIT ?;");
		pre.setInt(1, id);
		pre.setInt(2, lastX);
		ResultSet result = pre.executeQuery();
		while(result.next()) {
			Map<String, Double> map = new HashMap<String, Double>();
			map.put(result.getString(1) + " - " + result.getString(2),
					result.getDouble(3));
			ret.add(map);
		}
		result.close();
		pre.close();
		return ret;
	}
	
	// -- Actuators --
	
	/** Checks if an actuator is created ir not.
	 * @param pinid - pin from the actuator
	 * @return
	 * @throws SQLException
	 */
	private boolean isActuatorCreated(String pinid) throws SQLException {
		PreparedStatement pre = conn.prepareStatement("SELECT COUNT(*) FROM"
				+ " Actuators WHERE pinid = ?;");
		pre.setString(1, pinid);
		ResultSet result = pre.executeQuery();
		int r = result.getInt(1);
		result.close();
		pre.close();
		return r == 1;
	}
	
	/** Retrieves all the actuators from the database
	 * @return
	 * @throws SQLException 
	 * @throws NoSuchSensorException 
	 */
	public List<Actuator> getActuators() throws SQLException,
	NoSuchSensorException
	{
		List<Actuator> ret = new ArrayList<Actuator>();
		Statement sta = conn.createStatement();
		ResultSet result = sta.executeQuery("SELECT * FROM Actuators;");
		while (result.next()) {
			Actuator act = new Actuator();
			act.setPinId(result.getString(1));
			act.setName(result.getString(2));
			act.setType(ActuatorType.valueOf(result.getString(3)));
			if (result.getString(5) != null) {
				Sensor sensor;
				try {
					sensor = getSensor(result.getInt(4));
					if (sensor != null)
						act.setControlSensor(sensor);
					act.setCompareType(CompareType.valueOf(result.getString(5)));
					act.setCompareValue(result.getDouble(6));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			ret.add(act);
		}
		result.close();
		sta.close();
		return ret;
	}
	
	/** Inserts an actuator in the DB.
	 * @param actuator - actuator to insert
	 * @throws SQLException  - if something bad happens
	 * @throws DuplicatedActuatorException - when there is already an Actuator
	 * with the same pinid
	 * @throws NoSuchSensorException 
	 */
	public synchronized void insertActuator(Actuator actuator)
	throws DuplicatedActuatorException, SQLException, NoSuchSensorException
	{
		if (isActuatorCreated(actuator.getPinId()))
			throw new DuplicatedActuatorException();
		else {
			int idSensor = -1;
			PreparedStatement pre = null;
			if (actuator.hasControlSensor()) {
				idSensor = getSensorBDid(actuator.getControlSensor().getPinId(),
							Character.toString(actuator
								.getControlSensor().getType()
								.getIdentifier()));
				if (idSensor == -1)
					throw new NoSuchSensorException();
				pre = conn.prepareStatement("INSERT into "
						+ "Actuators (pinid, name, type, idSensor, "
						+ "compareType, compareValue) "
						+ "values (?, ?, ?, ?, ?, ?);");
				
			} else
				pre = conn.prepareStatement("INSERT into "
						+ "Actuators (pinid, name, type) values (?, ?, ?);");
			pre.setString(1, actuator.getPinId());
			pre.setString(2, actuator.getName());
			pre.setString(3, actuator.getType().toString());
			if (actuator.hasControlSensor()) {
				pre.setInt(4, idSensor);
				pre.setString(5, actuator.getCompareType().toString());
				pre.setDouble(6, actuator.getCompareValue());
			}
			pre.executeUpdate();
			pre.close();
		}
	}
	
	/** Deletes an actuator from the db.
	 * @param actuator - actuator to delete
	 * @throws SQLException
	 * @throws NoSuchActuatorException 
	 */
	public synchronized void deleteActuator(Actuator actuator)
	throws SQLException, NoSuchActuatorException
	{
		deleteActuator(actuator.getPinId());
	}
	
	/** Deletes an actuator given its pin id
	 * @param pinId
	 * @throws SQLException
	 * @throws NoSuchActuatorException 
	 */
	public synchronized void deleteActuator(String pinid) throws SQLException,
	NoSuchActuatorException
	{
		if (isActuatorCreated(pinid)) {
			PreparedStatement pre = conn.prepareStatement("DELETE FROM Actuators"
				+ " WHERE pinid = ?;");
			pre.setString(1, pinid);
			pre.executeUpdate();
			pre.close();
		} else throw new NoSuchActuatorException();
	}
	
	/** Updates an actuator from the db.
	 * @param actuator - actuator to update
	 * @throws SQLException 
	 * @throws NoSuchSensorException 
	 */
	public synchronized void updateActuator(Actuator actuator)
	throws SQLException, NoSuchSensorException
	{
		if (isActuatorCreated(actuator.getPinId())) {
			PreparedStatement pre = null;
			int idPos = -1, sensorId = -1;
			if (actuator.hasControlSensor()) {
				sensorId = getSensorBDid(actuator.getControlSensor()
						.getPinId(), Character.toString(actuator
								.getControlSensor().getType()
								.getIdentifier()));
				if (sensorId == -1)
					throw new NoSuchSensorException();
				pre = conn.prepareStatement("UPDATE Actuators SET type = ?,"
						+ "name = ?, idSensor = ?, compareType = ?,"
						+ "compareValue = ? WHERE pinid = ?;");
				idPos = 6;
			} else {
				pre = conn.prepareStatement("UPDATE Actuators SET type = ?,"
						+ "name = ? WHERE pinid = ?;");
				idPos = 3;
			}
			pre.setString(1, actuator.getType().toString());
			pre.setString(2, actuator.getName());
			if (actuator.hasControlSensor()) {
				pre.setInt(3, sensorId);
				pre.setString(4, actuator.getCompareType().toString());
				pre.setDouble(5, actuator.getCompareValue());
			}
			pre.setString(idPos, actuator.getPinId());
			pre.executeUpdate();
			pre.close();
		}
	}
}
