package it.mancin.rpi.storer;

import it.mancin.rpi.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.util.VersionInfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class RpiStorer_SQLite {
	
	Connection c = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    String url = "jdbc:mysql://localhost:3306/rpidb";
    String user = "root";
    String password = "raspberry";
    
    public RpiStorer_SQLite() {
    	//creo il DB se non esiste
    	createDB();
	}
    
    private void openConnection(){
//		try {
//			Class.forName("org.sqlite.JDBC");
//		    c = DriverManager.getConnection(Constants.DB_NAME);
//		    c.setAutoCommit(false);
//	
//		    stmt = c.createStatement();
//		}
//		catch ( Exception e ){
//			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//		}
    	
    	try {
	            Class.forName("com.mysql.jdbc.Driver");
	        c = DriverManager.getConnection(url, user, password);
	        stmt = c.createStatement();
	        c.setAutoCommit(false);
//	        rs = stmt.executeQuery("SELECT VERSION()");
	
//	        if (rs.next()) {
//	            System.out.println(rs.getString(1));
//	        }
//	        
//	        rs.close();
	
	    } catch (SQLException | ClassNotFoundException ex) {
	        Logger lgr = Logger.getLogger(VersionInfo.class.getName());
	        lgr.log(Level.SEVERE, ex.getMessage(), ex);
	    }
    	
//	    finally {
//	        try {
//	            if (rs != null) {
//	                rs.close();
//	            }
//	            if (stmt != null) {
//	                stmt.close();
//	            }
//	            if (c != null) {
//	                c.close();
//	            }
//	
//	        } catch (SQLException ex) {
//	            Logger lgr = Logger.getLogger(VersionInfo.class.getName());
//	            lgr.log(Level.WARNING, ex.getMessage(), ex);
//	        }
	    
    	
	}
	
	private void closeConnection(){
		try {
			stmt.close();
	        c.commit();
	        c.close();
		}
		catch ( Exception e ){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public void createDB(){
		
		try {
	      openConnection();
	      
	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `configuration` ( "+
	    			"`id`	INT PRIMARY KEY AUTO_INCREMENT, "+
	    			"`station`	VARCHAR(255), "+
	    			"`sensortype`	VARCHAR(255) "+
	    		");");
	      
	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `location` ("+
					"`id`	INT PRIMARY KEY AUTO_INCREMENT,"+
					"`name`	VARCHAR(255),"+
					"`lat`	VARCHAR(255),"+
					"`lng`	VARCHAR(255),"+
					"`alt`	VARCHAR(255),"+
					"`ntoe`	VARCHAR(255)"+
				");");	
	      		
	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `networkinterface` ("+
					"`macaddress`	VARCHAR(255),"+
					"`station`	VARCHAR(255),"+
					"`type`	VARCHAR(255),"+
					"`note`	VARCHAR(255),"+
					"PRIMARY KEY(macaddress)"+
				");");
	      
	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `senseddata` ("+
					"`id`	INT PRIMARY KEY AUTO_INCREMENT,"+
					"`macaddress`	VARCHAR(255),"+
					"`timestamp`	VARCHAR(255),"+
					"`payload`	VARCHAR(255),"+
					"`status`	VARCHAR(255)"+
				");"
	      		);
	      
	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `sensortype` ("+
					"`id`	INT PRIMARY KEY AUTO_INCREMENT,"+
					"`name`	VARCHAR(255),"+
					"`datatype`	VARCHAR(255),"+
					"`rangemin`	VARCHAR(255),"+
					"`rangemax`	VARCHAR(255),"+
					"`units`	VARCHAR(255),"+
					"`type`	VARCHAR(255)"+
				");");
	      
	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `station` ("+
					"`id`	INT PRIMARY KEY AUTO_INCREMENT,"+
					"`location`	VARCHAR(255),"+
					"`type`	VARCHAR(255),"+					
					"`status`	VARCHAR(255),"+
					"`version`	VARCHAR(255),"+
					"`note`	VARCHAR(255)"+
				");");
	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `users` ("+
					"`username`	VARCHAR(255),"+
					"`password`	VARCHAR(255),"+
					"PRIMARY KEY(username)"+
				");");
	    	
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		finally{
			closeConnection();
		}
		
    }
	
	
	
	private int doInsert(String sql){
		int id = 0;
		
		System.out.println(sql);
		
		try {
		    openConnection();
		      
		    id = stmt.executeUpdate(sql);
		    
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		finally{
			closeConnection();
		}
	    
	    return id;	    
	}
	
	
	public String getSensedData() {
		
		JsonArray jarray = new JsonArray();
		
		String sql = "SELECT * FROM senseddata";		
		
		try {
			
			openConnection();
			
			ResultSet tmp = stmt.executeQuery(sql);		
	    
		    while(tmp.next()) {
		    	JsonObject json = new JsonObject();
		    	json.addProperty("id",tmp.getInt("id"));
		    	json.addProperty("mac",tmp.getString("macaddress"));
		    	json.addProperty("timestamp",tmp.getString("timestamp"));
		    	json.addProperty("payload",tmp.getString("payload"));
		    	json.addProperty("status",tmp.getString("status"));
		    	jarray.add(json);
		    }
			
			tmp.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			closeConnection();
		}
		
		return jarray.toString();
		
	}

	public void insertSensedData(String mac, String timestamp, String payload, String status) {
		int id = doInsert("INSERT INTO senseddata (id, macaddress, timestamp, payload, status) VALUES(NULL," +
				"'"+mac+"',"+
				"'"+timestamp+"',"+
				"'"+payload+"',"+
				"'"+status+"'"+
				")");
		
		if(id>0)
			System.out.println("Insert complete!");
		else
			System.out.println("Insert ERROR!");		
	}
	
	public JsonArray getCachedData() {
		JsonArray jarray = new JsonArray();
		
		String sql = "SELECT * FROM senseddata WHERE status = '"+Constants.NOT_SENT+"' ORDER BY timestamp DESC";		
		
		try {
			
			openConnection();
			
			ResultSet tmp = stmt.executeQuery(sql);		
	    
		    while(tmp.next()) {
		    	JsonObject json = new JsonObject();
		    	json.addProperty("id",tmp.getInt("id"));
		    	json.addProperty("mac",tmp.getString("macaddress"));
		    	json.addProperty("timestamp",tmp.getString("timestamp"));
		    	json.addProperty("payload",tmp.getString("payload"));
		    	jarray.add(json);
		    }
			
			tmp.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			closeConnection();
		}
		
		return jarray;
	}
	
	public String getLastSensedData() {
		JsonArray jarray = new JsonArray();
		
		String sql = "SELECT * FROM senseddata ORDER BY timestamp DESC LIMIT 0,1";		
		
		try {
			
			openConnection();
			
			ResultSet tmp = stmt.executeQuery(sql);		
	    
		    while(tmp.next()) {
		    	JsonObject json = new JsonObject();
		    	json.addProperty("id",tmp.getInt("id"));
		    	json.addProperty("mac",tmp.getString("macaddress"));
		    	json.addProperty("timestamp",tmp.getString("timestamp"));
		    	json.addProperty("payload",tmp.getString("payload"));
		    	jarray.add(json);
		    }
			
			tmp.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			closeConnection();
		}
		
		return jarray.toString();
	}
	
	
	public String getSensedDataWithFilter(String[] params) {
		JsonArray jarray = new JsonArray();
		
		String sql = "SELECT * FROM senseddata ";
		boolean where = false, begin = false, end = false;
		for(String s : params){
			if(s.contains("begin") || s.contains("end")){
				if(!where){
					sql += "WHERE ";
					where = true;
				}
				if(s.contains("begin")){
					String b = s.substring(s.indexOf("=")+1);
					if(end)
						sql += "AND ";
					sql += "timestamp >= date('"+b+"') ";
					begin=true;
				}
				else{//CONTAINS END
					String e = s.substring(s.indexOf("=")+1);
					if(begin)
						sql += "AND ";
					sql += "timestamp <= date('"+e+"') ";
					end=true;
				}
			}			
		}
		
		for(String s : params){
			if(s.contains("order")){
				String order = s.substring(s.indexOf("=")+1);
				sql += "ORDER BY timestamp " + order.toUpperCase() + " ";
			}			
		}
		
		for(String s : params){
			if(s.contains("limit")){
				String limit = s.substring(s.indexOf("=")+1);
				sql += "LIMIT " + limit;
			}
		}
		
		System.out.println(sql);
		
		try {
			
			openConnection();
			
			ResultSet tmp = stmt.executeQuery(sql);		
	    
		    while(tmp.next()) {
		    	JsonObject json = new JsonObject();
		    	json.addProperty("id",tmp.getInt("id"));
		    	json.addProperty("mac",tmp.getString("macaddress"));
		    	json.addProperty("timestamp",tmp.getString("timestamp"));
		    	json.addProperty("payload",tmp.getString("payload"));
		    	jarray.add(json);
		    }
			
			tmp.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			closeConnection();
		}
		
		return jarray.toString();
	}

	public String getSensedDataWithFilter(String params) {
		JsonArray jarray = new JsonArray();
		
		String sql = "SELECT * FROM senseddata ";
		
		if(params.contains("begin") || params.contains("end")){
			sql += "WHERE ";
			if(params.contains("begin")){
				String begin = params.substring(params.indexOf("=")+1);
				sql += "timestamp >= date('"+begin+"')";
			}
			else{//CONTAINS END
				String end = params.substring(params.indexOf("=")+1);
				sql += "timestamp <= date('"+end+"')";
			}
		}
		if(params.contains("order")){
			String order = params.substring(params.indexOf("=")+1);
			sql += "ORDER BY timestamp " + order.toUpperCase() + " ";
		}
		if(params.contains("limit")){
			String limit = params.substring(params.indexOf("=")+1);
			sql += "LIMIT " + limit;
		}
		
		System.out.println(sql);
		
		try {
			
			openConnection();
			
			ResultSet tmp = stmt.executeQuery(sql);		
	    
		    while(tmp.next()) {
		    	JsonObject json = new JsonObject();
		    	json.addProperty("id",tmp.getInt("id"));
		    	json.addProperty("mac",tmp.getString("macaddress"));
		    	json.addProperty("timestamp",tmp.getString("timestamp"));
		    	json.addProperty("payload",tmp.getString("payload"));
		    	jarray.add(json);
		    }
			
			tmp.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			closeConnection();
		}
		
		return jarray.toString();
	}
	
	
	public String getInfoOfDevice() {
		JsonArray jarray = new JsonArray();
		String deviceInfo = "SELECT * FROM location, networkinterface, station WHERE location.id = station.id AND networkinterface.station = station.id";
		String sensorInfo = "SELECT * FROM sensortype";
		
		try {
			
			openConnection();
			
			ResultSet tmp = stmt.executeQuery(deviceInfo);		
			JsonArray deviceArray = new JsonArray();

//			ResultSetMetaData md = tmp.getMetaData();
//			  int columns = md.getColumnCount();
//			  for(int i=1; i<=columns; i++){
//				  System.out.println(md.getColumnName(i)+","+tmp.getObject(i).toString());
//			  }
			  
			
			while(tmp.next()) {				
		    	JsonObject json = new JsonObject();
		    	json.addProperty("name",tmp.getString("name"));
		    	json.addProperty("lat",tmp.getString("lat"));
		    	json.addProperty("lng",tmp.getString("lng"));
		    	json.addProperty("alt",tmp.getString("alt"));
		    	json.addProperty("macaddress",tmp.getString("macaddress"));
//		    	json.addProperty("type",tmp.getString("type"));
		    	json.addProperty("status",tmp.getString("status"));
		    	json.addProperty("version",tmp.getString("version"));
		    	deviceArray.add(json);
		    }
		    
		    tmp = stmt.executeQuery(sensorInfo);		
		    JsonArray sensorArray = new JsonArray();
		    while(tmp.next()) {
		    	JsonObject json = new JsonObject();
		    	json.addProperty("name",tmp.getString("name"));
		    	json.addProperty("datatype",tmp.getString("datatype"));
		    	json.addProperty("type",tmp.getString("type"));
		    	json.addProperty("rangemin",tmp.getString("rangemin"));
		    	json.addProperty("rangemax",tmp.getString("rangemax"));
		    	json.addProperty("units",tmp.getString("units"));		    			    	
		    	sensorArray.add(json);
		    }
			
		    tmp.close();
			JsonObject jdevice = new JsonObject();
			jdevice.add("info",deviceArray);			
			jarray.add(jdevice);
			JsonObject jsensor = new JsonObject();
			jsensor.add("sensors",sensorArray);
			jarray.add(jsensor);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			closeConnection();
		}
		
		return jarray.toString();
	}
	
	public void put() {
		
		int id = doInsert("INSERT INTO senseddata (macaddress, timestamp, payload) VALUES(" +
				"'AAA',"+
				"'oggi',"+
				"'{ciao}'"+
				")");
		
		if(id>0)
			System.out.println("Insert complete!");
		else
			System.out.println("Insert ERROR!");

	}

	public void update() {
		
//		String str = "UPDATE RESOURCE SET ";
//		
//		logger.info("PATH: {}", path);
//		
//		String location;
//		
//		if(path.contains("?")){
//			String[] str_split = path.split("\\?");
//			location = str_split[0];
//			
//			//System.out.println(str_split[i]);
//			if(str_split[1].contains("et=")){
//				//logger.info("ET: {}", str_split[i].toString());
//				int ind = str_split[1].indexOf("=");
//				String et = str_split[1].substring(ind+1, str_split[1].length());
//				logger.info("ET: {}", et);
//				str += "et = " + et;
//			}
//			else if(str_split[1].contains("lt=")){
//				//logger.info("LT: {}", str_split[i].toString());
//				int ind = str_split[1].indexOf("=");
//				String lt = str_split[1].substring(ind+1, str_split[1].length());
//				logger.info("LT: {}", lt);
//				str += "lt = " + lt;
//			}
//			else if(str_split[1].contains("con=")){
//				//logger.info("CON: {}", str_split[i].toString());
//				int ind = str_split[1].indexOf("=");
//				String con = str_split[1].substring(ind+1, str_split[1].length());
//				logger.info("CON: {}", con);
//				str += "con = " + con;
//			}				
//			
//		}
//		else{
//			location = path;
//		}
//		
//		String[] split = location.split("/");
//		
//		str += " WHERE ep="+ split[split.length-1] + ";";
//		
//		//System.out.println(str);
//		
//		
//		Connection c = null;
//	    Statement stmt = null;
//	    try {
//		      Class.forName("org.sqlite.JDBC");
//		      c = DriverManager.getConnection("jdbc:sqlite:rs.db");
//		      c.setAutoCommit(false);
//		      System.out.println("Opened database successfully");
//	
//		      stmt = c.createStatement();
//		      stmt.executeUpdate(str);
//		      
//		      c.commit();		      
//		      stmt.close();
//		      c.close();
//	    } catch ( Exception e ) {
//	    	  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//	    	  return false;
//	    }
//	    
//	    System.out.println("Operation done successfully");
//		return true;		
	}

	public void delete() {
		
//		String str = "DELETE FROM RESOURCE WHERE ep=";
//		String str2 = "DELETE FROM ENDPOINT WHERE id=";
//		
//		logger.info("PATH: {}", path);
//		
//		String[] split = path.split("/");
//		
//		str += split[split.length-1] + ";";
//		str2 += split[split.length-1] + ";";
//		
//		//System.out.println(str);
//		
//		
//		Connection c = null;
//	    Statement stmt = null;
//	    try {
//		      Class.forName("org.sqlite.JDBC");
//		      c = DriverManager.getConnection("jdbc:sqlite:rs.db");
//		      c.setAutoCommit(false);
//		      System.out.println("Opened database successfully");
//	
//		      stmt = c.createStatement();
//		      stmt.executeUpdate(str);
//		      stmt.executeUpdate(str2);
//		      
//		      c.commit();		      
//		      stmt.close();
//		      c.close();
//	    } catch ( Exception e ) {
//	    	  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//	    	  return false;
//	    }
//	    
//	    System.out.println("Operation done successfully");
//	    return true;
	}

	public void updateSensedData(int id) {
		String sql = "UPDATE senseddata SET status = 'send' WHERE id=" + id;
		try {
			openConnection();
			stmt.executeUpdate(sql);
		}
		catch ( Exception e ) {
			
		}
		finally{
			closeConnection();
		}
		
		
	}

	public HttpServletResponse getDBDump(HttpServletResponse response) {
		String filename = "rpiDB.db";

		File file = new File(filename);

		response.setContentType(new MimetypesFileTypeMap().getContentType(file));
		response.setContentLength((int)file.length());
		try {
			response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		InputStream is = null;
		OutputStream output = null;
		try {
			
		is = new FileInputStream(file);
//		//Files.copy(is, response.getOutputStream());
//		response.getOutputStream();
		output = response.getOutputStream();
		
		byte[] buffer = new byte[10240];

		
			int size = 0;
		    for (int length = 0; (length = is.read(buffer)) > 0;) {
		        output.write(buffer, 0, length);
		        size += length;
		    }
		    response.setContentLength(size);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
		    try { output.close(); } catch (IOException ignore) {}
		    try { is.close(); } catch (IOException ignore) {}
		}

		return null;
	}

		

}
