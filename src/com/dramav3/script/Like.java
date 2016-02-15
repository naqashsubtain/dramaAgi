package com.dramav3.script;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.DatabaseException;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;
 
 
 

public class Like extends AbstractBaseAgiScript {
	private static final Logger	logger	= Logger.getLogger(Like.class);
	 
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		 
			
			String cellno = "0" + channel.getVariable("DB_CLI");
			String tablename =  channel.getVariable("TABLE_NAME");
			String file = channel.getVariable("file"+ channel.getVariable("i"));
			logger.debug(tablename+ "file name like:"+file);
			//logger.debug("From caller Context:   "+request.getContext());
		//	String  code=	crmhelper.Install(channel.getVariable("DB_CLI"), crmhelper.init());
			
//if(code.contains("Code=0")){

			try {
				DBHelper.getInstance().executeDml("UPDATE "+tablename+" SET likes_count = likes_count + 1 WHERE(file=?)", super.getConnection(), file);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			                   
			               
		                   
		         
	}
}
