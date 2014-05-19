package com.sevenflying.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
					+ "dbid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "id INTEGER NOT NULL check (id >= 0),"
					+ "type char(1) NOT NULL,"
					+ "refresh INTEGER NOT NULL check (refresh > 0)"
					+ ");");
			// TODO other database tables
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
	
	
}
