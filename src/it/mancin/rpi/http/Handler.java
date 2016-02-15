package it.mancin.rpi.http;

//import it.mancin.rpi.storer.RpiStorer_SQLite;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler extends AbstractHandler {

	private static Logger logger = LoggerFactory.getLogger(Handler.class);

	private static String CONTENT_TYPE_JSON = "application/json";
	private static String CONTENT_TYPE_HTML = "html";
	
	private static int RESPONSE_CODE_OK = 200;
	private static int RESPONSE_CODE_MISSING = 404;
	
	private String content_type = "";
	private int response_code = 0;
	
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		String responseBody = "";
		
		if(request.getMethod().equals("GET")){
			String str = baseRequest.getUri().toString();
			logger.info("GET REQUEST: " + str);
			
			//chrome send a request for favicon			
			if(!str.contains("favicon.ico")){
				String[] split = str.split("/");
				
				//primo livello
				if( (split.length)>1 ){
					if(split[1].equals("data")){
						
						//secondo livello
						if( (split.length)>2 ){
							if(split[2].equals("last")){ // --- /data/last
//								responseBody = storer.getLastSensedData();
							}
							else if(split[2].startsWith("filter")){ // --- /data/filter
								//filter?limit=1
								if(split[2].indexOf('?')>0){
									String parameter = split[2].substring(split[2].indexOf('?')+1);
									if(parameter.contains("&")){
//										String[] params = parameter.split("&");
//										responseBody = storer.getSensedDataWithFilter(params);
									}
									else{
//										responseBody = storer.getSensedDataWithFilter(parameter);
									}
								}else{
									responseBody = "Missing parameter. ex. filter?limit=10&begin=2014-05-14";
									responseBody += "\n\nParameter list:\n";
									responseBody += "limit=\"number\"\n";
									responseBody += "begin=\"date(yyyy-mm-dd)\"\n";
									responseBody += "end=\"date(yyyy-mm-dd)\"\n";
									responseBody += "order=\"desc/asc\"\n";
									
									content_type = CONTENT_TYPE_HTML;
									response_code = RESPONSE_CODE_MISSING;
								}
							}							
							else{
								responseBody = "Bad request";
								content_type = CONTENT_TYPE_HTML;
								response_code = RESPONSE_CODE_MISSING;
							}
						}
						else // --- /data
//							responseBody = storer.getSensedData();
							content_type = CONTENT_TYPE_JSON;
							response_code = RESPONSE_CODE_OK;
					}
					else if(split[1].equals("device")){ // --- /device						
//						responseBody = storer.getInfoOfDevice();
						content_type = CONTENT_TYPE_JSON;
						response_code = RESPONSE_CODE_OK;
					}
					else if(split[1].startsWith("db")){ // --- /device						
						if(!split[1].contains("KEY")){
							responseBody = "missing KEY!! => db?KEY=<value>";
							content_type = CONTENT_TYPE_HTML;
							response_code = RESPONSE_CODE_MISSING;
						}
						else{
							String key = split[1].substring(split[1].indexOf("=")+1);
							if(key.equals("ciao")){
//								response = storer.getDBDump(response);
								baseRequest.setHandled(true);
								return;
							}
							else{
								responseBody = "WRONG KEY";
								content_type = CONTENT_TYPE_HTML;
								response_code = RESPONSE_CODE_MISSING;
							}
						}
					}
					else{
						responseBody = "Bad request";
						content_type = CONTENT_TYPE_HTML;
						response_code = RESPONSE_CODE_MISSING;
					}
				}
				else{
					responseBody = getMainMenu();
					content_type = CONTENT_TYPE_HTML;
					response_code = RESPONSE_CODE_OK;
				}
			}
			
		}
		else if(request.getMethod().equals("POST")){
			logger.info("POST REQUEST");
		}
		else if(request.getMethod().equals("PUT")){
			logger.info("PUT REQUEST");
		}
		else if(request.getMethod().equals("DELETE")){
			logger.info("DELETE REQUEST");
		}
		else{
			logger.info("ERROR!");
		}
		
		response.setStatus(response_code);
		response.setContentType(content_type);
		baseRequest.setHandled(true);
		response.getWriter().println(responseBody);
	}

	private String getMainMenu() {
		String rsp = "";
		
		rsp += "<a href=\"data\">ALL SENSED DATA</a><br>";
		rsp += "<a href=\"/data/last\">LAST SENSED DATA</a><br>";
		rsp += "<a href=\"/data/filter\">FILTER SENSED DATA</a><br>";
		rsp += "<br>";
		rsp += "<a href=\"device\">DEVICE INFO</a>";
		rsp += "<br>";
		rsp += "<a href=\"db\">GET DB DUMP</a>";
		
		return rsp;
	}

}