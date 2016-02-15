package it.mancin.rpi.serial;

public class Command {
//	* recv 'G' = get new data, quindi genera la stringa json e   
//		     manda i dati alla Raspberry (compreso un crc) 
//	* recv 'R' = resend data
//	* recv 'S,id' dove indico il numero di sensore di cui voglio 
//		     sapere il valore
//	* recv 'A,id,value' attuatore
		   
	public static String getNewData = "G";
	
	public String resendData = "R";
	
	public static String received = "O";
	
	public String dataOfSensor(int id){
		return "S,"+String.valueOf(id);
	}
	
	public String setActuator(int id, int value){
		return "S,"+String.valueOf(id)+","+String.valueOf(value);
	}

}
