package it.mancin.rpi.storer;

import com.google.gson.JsonArray;

public class RpiStorer_local {
	    
    public RpiStorer_local() {
    
    }
    
    
    public JsonArray getCachedData() {
		JsonArray jarray = new JsonArray();
		
		//String sql = "SELECT * FROM senseddata WHERE status = '"+Constants.NOT_SENT+"' ORDER BY timestamp DESC";		
		
				
		return jarray;
	}


	public void updateSensedData(int asInt) {
		// TODO Auto-generated method stub
		
	}	

}
