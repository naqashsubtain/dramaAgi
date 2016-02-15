package com.dramav3.script;

import java.util.Map;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;


public class checkingIsSub extends AbstractBaseAgiScript {
	private static final Logger	logger	= Logger.getLogger(checkingIsSub.class);

	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		try {
			String cellno = "0" + channel.getVariable("DB_CLI");
			  String msisdn = "92" + channel.getVariable("DB_CLI");
			logger.info("Customer: " + cellno + " checking Subscription");
			DBHelper.getInstance().executeDml("insert into subscriber (cellno,last_call_dt) values (?,now()) on duplicate key update last_call_dt=now()", super.getConnection(), cellno);
			
			Map<String, Object> result = DBHelper.getInstance().firstRow("select * from subscriber where cellno = ? AND is_active = 1", super.getConnection(), new Object[] { cellno });
						
			if (result != null) {
//				DBHelper.getInstance().executeDml("UPDATE subscriber SET lastcall_dt = NOW() WHERE cellno = ?", super.getConnection(), new Object[] { cellno });
				channel.verbose("Customer in local db: " + cellno + ", is a sunscriber", 0);
			    channel.setVariable("IS_SUB", "YES");
				channel.setVariable("LANG", result.get("lang").toString());
				logger.info("Cellno in colcal" + cellno + " is valid subsciber");
				return;
			}
			 channel.setVariable("IS_SUB", "YES");
			 
			//channel.verbose("Customer: " + cellno + ", is not a sunscriber", 0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
