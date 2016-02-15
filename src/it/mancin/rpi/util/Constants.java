package it.mancin.rpi.util;

public class Constants {
	
	public static final String SERVER_ADDRESS = "http://localhost";	
	public static final String SERVER_ADDRESS_NEWLOG = SERVER_ADDRESS + "/api/m2m.php?q=ins";
	
	public static boolean IS_DEFINE_SERVER = true;
	public static String SERVER_REMOTE_ADDRESS = "http://<REMOTE ADDRESS>;	
	public static String SERVER_REMOTE_ADDRESS_NEWLOG = SERVER_REMOTE_ADDRESS + "/api/m2m.php?q=ins";
	
	
	public static final String DB_NAME = "jdbc:sqlite:rpiDB.db";
	public static final String MYSQLDB = "jdbc:mysql://localhost:3306/rpidb";;
	
	
	public static final int LOCALHOST_PORT = 3456;
	
	
	public static final String LOCAL = "local";
	public static final String SENT = "sent";
	public static final String NOT_SENT = "notSent";
	
	public static final String MAC_ADDRESS = "outdoor_1";
	
}
