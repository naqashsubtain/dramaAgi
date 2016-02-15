package com.dramav3.script;

import java.util.Map;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;
 
 
 

public class saveSubConfirmation extends AbstractBaseAgiScript {
	private static final Logger	logger	= Logger.getLogger(saveSubConfirmation.class);
 
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		try {
			
			String cellno = "0" + channel.getVariable("DB_CLI");
			String lang = channel.getVariable("LANG");
			  String msisdn = "92" + channel.getVariable("DB_CLI");
			String subFrom = channel.getVariable("SUB_FROM");
			if(subFrom==null){
				subFrom="IVR";
			}
			  
			/* String response = Util.makeWebServiceRequest("subscribe?msisdn=" + msisdn+"&type_of_subscription=new");
		       
		       logger.debug("subscribe response: " + response);
		       if (response != null) {
		         if (response.equalsIgnoreCase("1Success"))
		         {
		          
					 
		          
		         }
		         else if (response.equals("2"))
		         {
		           channel.setVariable("SUB", "YES");
		           channel.verbose("Customer: " + cellno + ", is already subscribed ", 0);
		         }
		         else if (response.equals("-2"))
		         {
		           channel.setVariable("SUB", "NO");
		         }
		         else if (response.equals("-1"))
		         {
		           channel.setVariable("SUB", "NO");
		         }
		       }*/
			
			  if(ConfigurationLoader.getProperty("Operator").equals("Telenor")){
				  Map<String, Object> result = DBHelper.getInstance().firstRow("select * from subscriber where cellno = ? AND is_active = 1", super.getConnection(), new Object[] { cellno });
					
					if (result == null) {
					 DBHelper.getInstance().executeDml("insert into subscriber (cellno,sub_dt,lang,last_call_dt,is_active,sub_from,charging_attempt,response,expiry_date) values (?,now(),?,now(),?,?,0,null,date_add(now(), interval 1 day)) ON DUPLICATE KEY UPDATE sub_dt=now(),lang=?,charging_attempt=0,expiry_date=date_add(now(), interval 1 day)", super.getConnection(), cellno ,lang, true,subFrom,lang );
					 channel.verbose("Customer: " + cellno + ", has subscribed ", 0);
						
					  channel.setVariable("SUB", "YES");
					}
				}else{
					DBHelper.getInstance().executeDml("insert into subscriber (cellno,sub_dt,lang,last_call_dt,is_active,sub_from) values (?,now(),?,now(),?,'IVR') ON DUPLICATE KEY UPDATE sub_dt=now(),lang=?,is_active=?", super.getConnection(), cellno ,lang, true,lang,true );

				channel.verbose("Customer: " + cellno + ", has subscribed ", 0);
					
				  channel.setVariable("SUB", "YES");
			  }
				 if (ConfigurationLoader.getProperty("Operator").equals("Mobilink")) {
				String smsText = "Ap Mobilink Drama ki haftawaar subscription Rs.2.50+Tax pe hasil kar chukay hain.Aap aglay 7 Mobilink Drama istemaal kar saktay hain, Mobilink Drama istimal kernay kay liye 7676 @ Rs 0.30+T/min.";
				SendSMSUtil.sendSMS(ConfigurationLoader.getProperty("SENT_SMS_URL"), super.formatCellNumber(cellno), ConfigurationLoader.getProperty("SHORT_CODE"),  ConfigurationLoader.getProperty("SMSC"),smsText);

			}
			
 
		
} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
