package com.dramav3.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.asteriskjava.fastagi.AgiServerThread;
import org.asteriskjava.fastagi.DefaultAgiServer;
 

public class MainClass {
	private final static org.apache.log4j.Logger	logger	= org.apache.log4j.Logger.getLogger(MainClass.class);
	  
	  public static void main(String[] args) throws FileNotFoundException, IOException {
  		
	  	 
	  			
	  			
	  			AgiServerThread agiServerThread = new AgiServerThread();
                agiServerThread.setAgiServer(new DefaultAgiServer());
                agiServerThread.setDaemon(false);
                agiServerThread.startup();      		
	  			
	  			
	  			
	  	 
	  			 
	                      
	                           
	                      
	                      
	                      
	                      
	                      
	        
	  		 
	  	}
}
