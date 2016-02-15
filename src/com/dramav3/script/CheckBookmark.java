package com.dramav3.script;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBConnectionManager;
import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;


public class CheckBookmark extends AbstractBaseAgiScript {
	private static final Logger	logger	= Logger.getLogger(CheckBookmark.class);

	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		try {
			String cellno = "0" + channel.getVariable("DB_CLI");
			  String cat = channel.getVariable("CAT");
			logger.info("Customer: " + cellno + " checking Subscription");
			String smsText ="";
			
			Map<String, Object> result = DBHelper.getInstance().firstRow("select * from bookmarks where cellno = ? AND cat = ? limit 1", super.getConnection(), new Object[] { cellno,cat });
						
			
			if (result != null) {
				channel.setVariable("BOOKMARK_EXISTS", "YES");
				return;
			}else{
				channel.setVariable("BOOKMARK_EXISTS", "NO");
				return;
			 
			}
		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
