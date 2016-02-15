package it.mancin.rpi.serial;

import java.util.TimerTask;

public class SerialPoller extends TimerTask {
    public void run() {
       System.out.println("--> SEND DATA");
       Serial.send(Command.getNewData); 
    }
 }
