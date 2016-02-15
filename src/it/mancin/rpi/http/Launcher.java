package it.mancin.rpi.http;


public class Launcher {

	//private static Logger logger = LoggerFactory.getLogger(CloudServerLauncher.class);

	public static void main(String[] args) {

		int cloudPort = 3333;

		if(args.length == 1){
			cloudPort = Integer.parseInt(args[0]);
		}

		HTTPServer server = new HTTPServer(cloudPort);
		server.setHandler(new Handler());
		
		try{
			server.start();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
