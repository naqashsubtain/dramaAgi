package com.dramav3.script;

import java.util.Map;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.DatabaseException;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;

/**
 * 
 * @author Yasir
 */

public class SaveSubConfirmationWeekly extends AbstractBaseAgiScript {

	

	public SaveSubConfirmationWeekly() throws DatabaseException {
		super(true);
	}

	@Override
	public void service(AgiRequest resuest, AgiChannel channel) throws AgiException {
		try {
			String cellno = "0" + channel.getVariable("DB_CLI");
			long requestid = Long.parseLong(channel.getVariable("REQUESTID"));
			Integer lang = Integer.parseInt(channel.getVariable("LANG"));
			
			logger.debug(" SaveSubConfirmationWeekly  cellno =" + cellno );
			
			String checkingSubQuery = "select *  from ucip_response where requestId = ?";

			Map<String, Object> result = DBHelper.getInstance().firstRow(
					checkingSubQuery, super.getConnection(), requestid);
			
			if (result != null && !result.isEmpty()) {
				String response = (String)result.get("response");
				if(response.equals(ConfigurationLoader.getProperty("UCIP_SUCCESS_RESPONSE"))){
					channel.setVariable("IS_ALREADY_SUB", "YES");
					channel.setVariable("RETRY", "NO");
					String updateQuery = "update subscriber set sub_dt= now(),is_active=true,lang=1, expiry_date=if(DATEDIFF(Date(expiry_date),curdate()) = 1, DATE_ADD(expiry_date, interval 6 day), DATE_ADD(now(), interval 6 day) ) where cellno = ?";
					DBHelper.getInstance().executeDml(updateQuery, super.getConnection(), cellno);
					
					checkingSubQuery = "select DATE_FORMAT(expiry_date,'%d-%M')  expiry_date from subscriber where cellno = ? ";

					Map<String, Object> result3 = DBHelper.getInstance().firstRow(
							checkingSubQuery, super.getConnection(), cellno);
					if (result3 != null && !result3.isEmpty()) {
						String expiryDate = (String) result3.get("expiry_date");
						String smsText = "Ap Mobilink Drama ki haftawaar subscription Rs.2.50+Tax pe hasil kar chukay hain.Aap "+expiryDate+" tak Mobilink Drama istemaal kar saktay hain, Mobilink Drama istimal kernay kay liye 7676 @ Rs 0.30+T/min.";
						SendSMSUtil.sendSMS(ConfigurationLoader.getProperty("SENT_SMS_URL"), super.formatCellNumber(cellno), ConfigurationLoader.getProperty("SHORT_CODE"),  ConfigurationLoader.getProperty("SMSC"),smsText);


					}
				}else{
					String updateQuery = "update subscriber set sub_dt= now(),is_active=true , lang=1, expiry_date=null,last_billed_date=null where cellno = ?";
					DBHelper.getInstance().executeDml(updateQuery, super.getConnection(), cellno);
					String smsText = "Aap ko Mobilink Drama par subscribe kar diya giya hai. Mobilink DRAMA istimal kernay kay liye 7676 milayein @ Rs 0.30+T/min."; 
					SendSMSUtil.sendSMS(ConfigurationLoader.getProperty("SENT_SMS_URL"), super.formatCellNumber(cellno), ConfigurationLoader.getProperty("SHORT_CODE"),  ConfigurationLoader.getProperty("SMSC"),smsText);
					channel.setVariable("IS_ALREADY_SUB", "YES");
					channel.setVariable("RETRY", "NO");
					
					// to Add into billing users
//					String smsText = "Aap kay account main balance nakafi honay ki waja say apki Mobilink Drama hafatawar pass ki darkhuwast nakam rahi. Balance recharge kar kay dobara koshish karin"; 
//					SendSMSUtil.sendSMS(ConfigurationLoader.getProperty("SENT_SMS_URL"), super.formatCellNumber(cellno), ConfigurationLoader.getProperty("SHORT_CODE"),  ConfigurationLoader.getProperty("SMSC"),smsText);

				}
			}else{
				String updateQuery = "update subscriber set sub_dt= now(),is_active=true ,lang=1 ,expiry_date=null,last_billed_date=null where cellno = ?";
				DBHelper.getInstance().executeDml(updateQuery, super.getConnection(), cellno);
				String smsText = "Aap ko Mobilink Drama par subscribe kar diya giya hai. Mobilink DRAMA istimal kernay kay liye 7676 milayein @ Rs 0.30+T/min."; 
				SendSMSUtil.sendSMS(ConfigurationLoader.getProperty("SENT_SMS_URL"), super.formatCellNumber(cellno), ConfigurationLoader.getProperty("SHORT_CODE"),  ConfigurationLoader.getProperty("SMSC"),smsText);
				
				channel.setVariable("IS_ALREADY_SUB", "YES");
				channel.setVariable("RETRY", "NO");
				
				// to Add into billing users
//				channel.setVariable("RETRY", "YES");
			}
			
			

		} catch (DatabaseException databaseException) {
			logger.error(databaseException.getMessage(), databaseException);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

}
