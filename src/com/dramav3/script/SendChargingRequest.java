package com.dramav3.script;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DatabaseException;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.dramav3.common.Helper;

/**
 * 
 * @author Yasir
 */

public class SendChargingRequest extends AbstractBaseAgiScript {



	public SendChargingRequest() throws DatabaseException {
		super(true);
	}

	@Override
	public void service(AgiRequest resuest, AgiChannel channel) throws AgiException {
		try {
			String cellno = "0" + channel.getVariable("DB_CLI");

			logger.info("SendChargingRequest  cellno =" + cellno );
//			channel.setVariable("REQUESTID", "230");
			
				long result = Helper.sendUcip(cellno);
				if (result != -999) {
					channel.setVariable("REQUESTID", String.valueOf(result));
					return;
				}else{
					logger.info("SendChargingRequest  cellno =" + cellno  + "failed");

				}
			

		}
//		catch (DatabaseException databaseException) {
//			logger.error(databaseException.getMessage(), databaseException);
//		} 
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

}
