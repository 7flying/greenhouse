package com.sevenflying.server.net;

/** Defines project's constants
 * @author 7flying
 */
public class Constants {

	/* ********************
	 *  Expected commands * 
	 * ********************/
	public static final String GETSENSORS = "GETSENSORS";
	public static final String HISTORY = "HISTORY";
	public static final String CHECK = "CHECK";
	public static final String NEW = "NEW";
	public static final String DELETE = "DELETE";
	public static final String UPDATE = "UPDATE";
	public static final String POWSAV = "POWSAV";
	

	/* ***************
	 * Control codes *
	 * ***************/
	public static final String OK = "OK\n";
	
	/* *************
	 * Error codes *
	 * *************/
	 public static final String INTERNAL_SERVER_ERROR = "500";
	 public static final String DUPLICATED_SENSOR = "501";
	 public static final String INCORRECT_NUMBER_OF_PARAMS = "400";
	
}
