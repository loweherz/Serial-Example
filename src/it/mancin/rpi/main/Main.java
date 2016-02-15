package it.mancin.rpi.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.mancin.rpi.serial.Serial;
import it.mancin.rpi.serial.SerialPoller;
import it.mancin.rpi.util.CheckCachedData;
import it.mancin.rpi.util.Constants;
 
public class Main {
	
	public static void main( String[] args ) {
		
		Serial serial = new Serial();  
		serial.initialize();
		
//		attendo che Arduino si inizializzi
		try {
			
			Thread.sleep(5000);
			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//CONTROLLARE!!!!
		
//		Thread t=new Thread() {
//			public void run() {
//				//the following line will keep this app alive for 1000 seconds,
//				//waiting for events to occur and responding to them (printing incoming messages to console).
//				try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
//			}
//		};
//		t.start();
		
		try{
			File jsonFile = new File("/home/pi/servlet.json");		
			JsonParser jp = new JsonParser();
			JsonObject j = jp.parse(new FileReader(jsonFile)).getAsJsonObject();
			
			Constants.SERVER_REMOTE_ADDRESS = j.get("remote_address").getAsString();
			Constants.SERVER_REMOTE_ADDRESS_NEWLOG = Constants.SERVER_REMOTE_ADDRESS + j.get("remote_address_newlog").getAsString();
			System.out.println("Assegno i valori della servlet remota: " + Constants.SERVER_REMOTE_ADDRESS_NEWLOG);
		
			boolean SEND_TO_REMOTE = Boolean.getBoolean(j.get("send_remote").getAsString());
			
			Constants.IS_DEFINE_SERVER = SEND_TO_REMOTE;
		}
		catch(Exception e){
			//valori di default
			e.printStackTrace();
			System.out.println("ERRORE, assegno i valori di defualt della servlet remota");
			Constants.SERVER_REMOTE_ADDRESS = "http://<ADDRESS>";
			Constants.SERVER_REMOTE_ADDRESS_NEWLOG = Constants.SERVER_REMOTE_ADDRESS + "/api/m2m.php?q=ins";
		
			Constants.IS_DEFINE_SERVER = false;
		}
		//Timer cacheTimer = new Timer();
		//every 5 minutes check if there is cached data
		//cacheTimer.schedule(new CheckCachedData(), 0, 500*60);
		
		Timer serialTimer = new Timer();    
		// Chiedo i dati ogni 30 secondi (DEVO ABBASSARE!!)
		serialTimer.schedule(new SerialPoller(), 0, 1000*60);
		
		
		System.out.println("Started");		
			
		
		//HTTPServer server = new HTTPServer(Constants.LOCALHOST_PORT);
		//server.setHandler(new Handler());
			
		
//		try{
//			server.start();
//		} catch (Exception e){
//			e.printStackTrace();
//		}	
		
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
}