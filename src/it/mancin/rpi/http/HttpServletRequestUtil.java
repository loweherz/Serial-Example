package it.mancin.rpi.http;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServletRequestUtil {

	private static Logger logger = LoggerFactory.getLogger(HttpServletRequestUtil.class);

	public static String getBody(HttpServletRequest request) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try{
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null){
				jb.append(line);
			}
		} catch (Exception e){
			logger.error(e.getMessage());
		}
		String body = new String(jb);
		return body;
	}

}
