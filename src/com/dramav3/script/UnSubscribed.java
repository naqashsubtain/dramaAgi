package com.dramav3.script;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;

public class UnSubscribed extends AbstractBaseAgiScript {
	private static final Logger logger = Logger.getLogger(UnSubscribed.class);

	public void service(AgiRequest request, AgiChannel channel) throws AgiException {

		String cellno = "0" + channel.getVariable("DB_CLI");

		String msisdn = "92" + channel.getVariable("DB_CLI");
		logger.info("Customer: " + cellno + " checking UnSubscribed");
		unsub(cellno);

		/*
		 * String response = null; try { response =
		 * Util.makeWebServiceRequest("unsubscribe?msisdn=" +
		 * msisdn+"&unsub_type=unsub_user"); } catch (IOException e) { // TODO
		 * Auto-generated catch block logger.debug("{error} "+e.getMessage());
		 * e.printStackTrace(); } logger.debug("unsubscribe response: " +
		 * response); if (response != null) { if (response.equals("1")) {
		 * channel.setVariable("UNSUB", "YES"); channel.verbose("Customer: " +
		 * cellno + ", has unsubscribed ", 0); unsub(cellno); } else if
		 * (response.equals("2")) { channel.setVariable("UNSUB", "YES");
		 * channel.verbose("Customer: " + cellno + ", is already unsubscribed ",
		 * 0); } else if (response.equals("-2")) { channel.setVariable("UNSUB",
		 * "NO"); } else if (response.equals("-1")) {
		 * channel.setVariable("UNSUB", "NO"); } } //String code=
		 * crmhelper.Disconnect(channel.getVariable("DB_CLI"),
		 * crmhelper.init());
		 */

	}

	public void unsub(String cellno) {

		System.out.println("unsub:" + cellno);
		try {
			// if(code.contains("Code=0")){
			String query = "";
			if (ConfigurationLoader.getProperty("Operator").equals("Telenor")) {
				query = "Insert into subscriber_unsub SELECT null,cellno, sub_dt,lang,last_call_dt,now(),sub_from,'IVR' FROM subscriber where cellno=? and is_active=?";
				Integer rs = DBHelper.getInstance().executeDml(query, super.getConnection(), new Object[] { cellno ,true});
				if (rs.intValue() > 0) {
					DBHelper.getInstance().executeDml("Delete from subscriber where cellno=?", super.getConnection(), new Object[] { cellno });
					
					String smsText = "You have been successfully un-subscribed from Telenor Mobile Drama.";
					String smsquery = "INSERT INTO send_sms (dt,msgdata,receiver,status) VALUES (now(),?,?,-100)";
					DBHelper.getInstance().executeDml(smsquery, super.getConnection(), smsText, super.formatCellNumber(cellno));
					logger.info(cellno + " unsub successfully ");
				}
			} else {
				query = "Insert into subscriber_unsub (cellno,sub_dt,unsub_dt,last_call_dt,lang,sub_from) SELECT cellno, sub_dt,now(),last_call_dt,lang,sub_from FROM subscriber where cellno=?";
				Integer rs = DBHelper.getInstance().executeDml(query, super.getConnection(), new Object[] { cellno });
				if (rs.intValue() > 0) {
					DBHelper.getInstance().executeDml("Delete from subscriber where cellno=?", super.getConnection(), new Object[] { cellno });
					logger.info(cellno + " unsub successfully ");
				}
				
				SendSMSUtil.sendSMS(ConfigurationLoader.getProperty("SENT_SMS_URL"), super.formatCellNumber(cellno), ConfigurationLoader.getProperty("SHORT_CODE"), ConfigurationLoader.getProperty("SMSC"), ConfigurationLoader.getProperty("SMS_TEXT_UNSUB"));
			}

			/*
*/
			// } else {
			// logger.error(cellno + " unsub failed ");
			// }
			//
			// }else{
			// logger.error(cellno + " unsub failed reason : "+code);
			//

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}
}
