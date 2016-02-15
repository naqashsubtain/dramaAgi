package com.dramav3.script;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBConnectionManager;
import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;

public class SendCatSMS extends AbstractBaseAgiScript {
	private static final Logger logger = Logger.getLogger(SendCatSMS.class);

	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		try {
			String cellno = "0" + channel.getVariable("DB_CLI");
			String msisdn = "92" + channel.getVariable("DB_CLI");
			String cat = channel.getVariable("CAT");
			logger.info("Customer: " + cellno + " adding category sms");
			String smsText = "";

			if (ConfigurationLoader.getProperty("Operator").equals("Mobilink")) {
				if (cat.equals("urdu")) {
					smsText = "Muaziz Sarif, Aap Mobilink Drama Urdu Channel asani se sune k liye kisi bhi waqt 7676001 dial ker sakte hain";
				} else if (cat.equals("pashtu")) {
					smsText = "Muaziz Sarif, Taso Mobilink Drama Pukhto Channel asanai sara awredo dapara sa wakht hum  7676002 dial kawalo sara awrede shae";
				} else if (cat.equals("punjabi")) {
					smsText = "Muaziz Sarif, Tussi Mobilink Drama Punjabi Channel asani nal sunnan lai kisi vele v  7676003 dial ker k sun sakde o";
				}

				SendSMSUtil.sendSMS(ConfigurationLoader.getProperty("SENT_SMS_URL"), super.formatCellNumber(cellno),
						ConfigurationLoader.getProperty("SHORT_CODE"), ConfigurationLoader.getProperty("SMSC"), smsText);
			} else if (ConfigurationLoader.getProperty("Operator").equals("Telenor")) {
				if (cat.equals("urdu")) {
					smsText = "Muaziz Sarif, Aap Telenor Drama Urdu Channel asani se sune k liye kisi bhi waqt 530001 dial ker sakte hain";
				} else if (cat.equals("pashtu")) {
					smsText = "Muaziz Sarif, Taso Telenor Drama Pukhto Channel asanai sara awredo dapara sa wakht hum  530002 dial kawalo sara awrede shae";
				} else if (cat.equals("punjabi")) {
					smsText = "Muaziz Sarif, Tussi Telenor Drama Punjabi Channel asani nal sunnan lai kisi v vele 530003 dial ker k sun sakde o";
				}
				
				String url = ConfigurationLoader.getProperty("db.sms.url");
				String user= ConfigurationLoader.getProperty("db.sms.user");
				String pass= ConfigurationLoader.getProperty("db.sms.password");
				
				logger.info("Customer: " + cellno + " making DB Connection: " + url +" "+ user +" "+ pass);
//				Connection conn = DBConnectionManager.getInstance().getConnection(url,user,pass);
				logger.info("Customer: " + cellno + " insert into send_sms");
				String query = "INSERT INTO send_sms (dt,msgdata,receiver,status) VALUES (now(),?,?,-100)";
				DBHelper.getInstance().executeDml(query, super.getConnection(), smsText, super.formatCellNumber(cellno));

			}

		} catch (Exception e) {
			logger.error("Exception",e);
			logger.error(e.getMessage(), e);
		}
	}
}
