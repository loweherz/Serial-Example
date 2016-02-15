package it.mancin.rpi.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtility {
	
	public static String getMyMacAddress(){
		return "b8:27:eb:66:11:d5";
/*
		String str = new String();
		
		try {
			InetAddress ip = InetAddress.getLocalHost();
			
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
	 
			byte[] mac = network.getHardwareAddress();
	 		
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
	 
			str = sb.toString();
			
		} catch (UnknownHostException e) {	 
			e.printStackTrace();	 
		} catch (SocketException e){	 
			e.printStackTrace();	 
		}
		
		return str;
		*/
	}
	
	public static String getMyIp(){
		
		String str = new String();
		
		try {
			InetAddress ip = InetAddress.getLocalHost();
			
			str = ip.getHostAddress();
			
		} catch (UnknownHostException e) {	 
			e.printStackTrace();	 
		}
		
		return str;
	}

}
