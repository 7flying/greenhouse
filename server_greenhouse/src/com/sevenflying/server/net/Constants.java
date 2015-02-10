package com.sevenflying.server.net;

/** Defines project's constants
 * @author 7flying
 */
public class Constants {

	/* ********************
	 *  Expected commands * 
	 * ********************/
	public static final String GETSENSORS 		= "GETSENSORS";
	public static final String GETACTUATORS		= "GETACTUATORS";
	public static final String HISTORY 			= "HISTORY";
	public static final String CHECK 			= "CHECK";
	public static final String LAUNCH			= "LAUNCH";
	public static final String NEW_SENSOR 		= "NEW-SENSOR";
	public static final String NEW_ACTUATOR		= "NEW-ACTUATOR";
	public static final String DELETE_SENSOR 	= "DELETE-SENSOR";
	public static final String DELETE_ACTUATOR 	= "DELETE-ACTUATOR";
	public static final String UPDATE_SENSOR 	= "UPDATE-SENSOR";
	public static final String UPDATE_ACTUATOR	= "UPDATE-ACTUATOR";
	public static final String POWSAV 			= "POWSAV";
	

	/* ***************
	 * Control codes *
	 * ***************/
	public static final String OK = "OK\n";
	
	/* *************
	 * Error codes *
	 * *************/
	 public static final String INTERNAL_SERVER_ERROR 	   = "500";
	 public static final String DUPLICATED_SENSOR 		   = "501";
	 public static final String DUPLICATED_ACTUATOR		   = "502";
	 public static final String INCORRECT_NUMBER_OF_PARAMS = "400";
	
}
