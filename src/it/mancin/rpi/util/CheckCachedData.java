package it.mancin.rpi.util;

import it.mancin.rpi.http.HttpURLRequest;
import it.mancin.rpi.storer.RpiStorer_local;

import java.util.TimerTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CheckCachedData extends TimerTask {
	RpiStorer_local storer = new RpiStorer_local();
	
    public void run() {    	
    	JsonArray jarray = storer.getCachedData();
		HttpURLRequest request = new HttpURLRequest();
		for(int i=0; i<jarray.size(); i++){
			JsonObject json = (JsonObject) jarray.get(i);
			System.out.println("CACHED: " + json.get("id").toString());
			try {
				if(request.sendPost(Constants.SERVER_ADDRESS_NEWLOG, json.toString())){
					storer.updateSensedData(json.get("id").getAsInt());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
     }
}
