package it.mancin.rpi.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import it.mancin.rpi.http.HttpURLRequest;
import it.mancin.rpi.util.Constants;
import it.mancin.rpi.util.NetworkUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Serial implements SerialPortEventListener {
	SerialPort serialPort;
    /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
	        "/dev/ttyACM0", // Raspberry Pi
	        "/dev/ttyAMA0", // Raspberry Pi	        
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
	};
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private BufferedReader input;
	/** The output stream to the port */
	private static OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	
	public void initialize() {
	            // the next line is for Raspberry Pi and 
	            // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
	            System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyAMA0");
	
		CommPortIdentifier portId = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
	
		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}
	
		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);
	
			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
	
			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();
	
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}
	
	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
	    try {
	        switch (oEvent.getEventType() ) {
	            case SerialPortEvent.DATA_AVAILABLE: 
	                if ( input == null ) {
	                    input = new BufferedReader(
	                        new InputStreamReader(
	                                serialPort.getInputStream()));
	                }

	                String inputLine = input.readLine();
	//                int c;
	                
	//                while( (c=input.read())>0 ){
	//                	System.out.print(String.valueOf(Character.toChars(c)));
	//                	inputLine += String.valueOf(Character.toChars(c));
	//                }
	                
	                inputLine = inputLine.substring(inputLine.indexOf('{'), inputLine.indexOf('}')+1);
	                System.out.println(inputLine);
	                parseStringBuffer(inputLine);
	                break;
	 
	            default:
	                break;
	        }
	    } 
	    catch (Exception e) {
	        System.err.println(e.toString());
	    }
	}
	
	  public void parseStringBuffer(String str){
		  JsonParser jsonParser = new JsonParser();
		  JsonObject obj = (JsonObject)jsonParser.parse(str);
		  
		  if(obj.get("ERROR")!=null){
			  //ERRORE
			  System.out.println(obj.get("ERROR"));
			  return;
		  }
		  
		  //se non ci sono errori prelevo i dati per inviarli in remoto e/o in locale
		  String mac = Constants.MAC_ADDRESS;//NetworkUtility.getMyMacAddress();
		  Date date= new Date();
		  String timestamp = (new Timestamp(date.getTime())).toString();
		  
		  //setto la variabile status local
		  @SuppressWarnings("unused")
		  String status = Constants.LOCAL;
		  
		  //controllo il campo remote se devo spedire o meno il dato
		  System.out.println("REMOTE: " + obj.get("REMOTE").getAsString());
		  if(obj.get("REMOTE").getAsString().equals("Y")){
			  
			  status = Constants.SENT;
			  //RICONTROLLARE
			  //{"timestamp":"2014-08-15 10:34:22","payload":{"mac":"12345","temp":"21.32"}}
			  JsonObject json = new JsonObject();
			  json.addProperty("mac",mac);
			  json.addProperty("timestamp",timestamp);
			  json.add("payload",obj);
			  
			  
			  //SEND TO SERVER REMOTE
			  HttpURLRequest request = new HttpURLRequest();
			  try {
				  if(!request.sendPost(Constants.SERVER_ADDRESS_NEWLOG, json.toString())){
					  status = Constants.NOT_SENT;
				  }
				  
				  if(Constants.IS_DEFINE_SERVER)
					  if(!request.sendPost(Constants.SERVER_REMOTE_ADDRESS_NEWLOG, json.toString())){
						  status = Constants.NOT_SENT;
					  }
				  
				  //MY SERVER
				  if(!request.sendPost(Constants.MY_SERVER_REMOTE_ADDRESS_NEWLOG, json.toString())){
					  status = Constants.NOT_SENT;
				  }
			  } catch (Exception e) {				  
				  e.printStackTrace();
			  }
		  }
		  
		  send(Command.received);
		  
		  //SAVE ON LOCAL DB
		  //RpiStorer_SQL storer = new RpiStorer_SQL();
		  //storer.insertSensedData(mac, timestamp, str, status);

	  }
	  
	  public static void send(String str){	 
	      try {	        
	        output.write( str.getBytes() );	        
	      } catch( IOException e ) {
	        e.printStackTrace();
	      }
	  }

}
