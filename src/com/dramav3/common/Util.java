 package com.dramav3.common;
 
 import com.agiserver.helper.common.ConfigurationLoader;
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import org.apache.log4j.Logger;
 import org.asteriskjava.fastagi.BaseAgiScript;
 
 public abstract class Util
   extends BaseAgiScript
 {
   private static final Logger logger = Logger.getLogger(Util.class);
   
   public static String makeWebServiceRequest(String uri)
     throws IOException
   {
     URL url = new URL(ConfigurationLoader.getProperty("WEBSERVICE_URL") + "/" + uri);
     logger.debug("requestUrl: " + url);
     
     HttpURLConnection http = (HttpURLConnection)url.openConnection();
     
     StringBuilder body = new StringBuilder(100);
     label147:
     try
     {
       BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
       try
       {
         String inputLine;
         while ((inputLine = in.readLine()) != null)
         {
            
           body.append(inputLine);
         }
       }
       catch (IOException e)
       {
         if (in == null) {
           break label147;
         }
       }
       in.close();
     }
     finally
     {
       if (http != null) {
         http.disconnect();
       }
     }
     return body.toString();
   }
   
   public static String formatCellNumber(String cellNumber)
   {
     if (cellNumber.startsWith("+92")) {
       cellNumber = cellNumber.substring(1);
     } else if (cellNumber.startsWith("0092")) {
       cellNumber = cellNumber.substring(2);
     } else if (cellNumber.startsWith("03")) {
       cellNumber = "92" + cellNumber.substring(1);
     } else if (cellNumber.startsWith("3")) {
       cellNumber = "92" + cellNumber;
     }
     return cellNumber;
   }
 }

