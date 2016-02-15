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

public class CheckSessionStatus extends AbstractBaseAgiScript
{
	private final static Logger log = Logger.getLogger(CheckSessionStatus.class);

	//=============================================================================

	@Override
	public void service(AgiRequest request, AgiChannel channel) throws AgiException 
	{
		String callerNumber = channel.getVariable("DB_CLI");
		callerNumber = formatCellNumber(callerNumber);
		callerNumber = "0" + callerNumber.substring(2);
		log.debug("cell # of Caller : " + callerNumber);

		channel.setVariable("IS_CONF_ACTIVE",         "NO" );
		channel.setVariable("IS_MOD_AVAILABLE",       "NO" );
		channel.setVariable("IS_ANNOUNCEMENT_ACTIVE", "NO" );
		channel.setVariable("IS_NOCELEB_ACTIVE",      "NO" );
		channel.setVariable("IS_CELEBBUSY_ACTIVE",    "NO" ); 
		channel.setVariable("IS_WAIT_ACTIVE",         "NO" );

		/*	-----	checking show time and setting channel variables accordingly	*/
		Map<String, Object> confRoom = checkShowTime();
		if(confRoom != null){
			channel.setVariable("IS_CONF_ACTIVE",         "YES" );
			channel.setVariable("CONF_ROOM_TYPE",    confRoom.get("conf_room_type").toString() );
			channel.setVariable("CONF_WELCOME_FILE", confRoom.get("welcome_file_name").toString() );
			channel.setVariable("CONF_CONNECT_FILE", confRoom.get("connect_file_name").toString() );
			channel.setVariable("CONF_WAIT_FILE",    confRoom.get("wait_file_name").toString() );
			channel.setVariable("CONF_ROOM_NAME",    confRoom.get("conf_room_name").toString() );

			channel.setVariable("CELEB_BUSY_FILE_NAME",    confRoom.get("celeb_busy_file_name").toString() );
			channel.setVariable("ANNOUNCEMENT_FILE_NAME",    confRoom.get("announcement_file_name").toString() );
			channel.setVariable("NO_CELEB_FILE_NAME",    confRoom.get("no_celeb_file_name").toString() );

			log.debug("Active Live show id : " + confRoom.get("conf_room_id") + ",  show name : " + confRoom.get("conf_room_name") );
		} else {
			return;
		}

		/*	-----	checking if user is a moderator	*/
		boolean isModerator =  checkUserAsModerator(callerNumber);
		if(isModerator == true) {
			channel.setVariable("IS_MOD", "YES");
			return;
		}
		else{
			channel.setVariable("IS_MOD", "NO");
			/*	-----	checking if user is not a moderator	
 			then checking if moderator is online*/

			boolean isModeratorOnline = checkIsModeratorOnline();
			if(isModeratorOnline == true){
				channel.setVariable("IS_MOD_AVAILABLE",       "YES" );
			}
			else{
				return;
			}
		}

	}

	//=============================================================================

	public Map<String, Object> checkShowTime()
	{
		Map<String, Object> confMaps = null;
		String query = "Select * from conf_room" +
				" where now() between conf_room_start_time and conf_room_end_time" +
				" order by conf_room_id desc limit 1";
		try {
			confMaps = DBHelper.getInstance().firstRow(query, super.getConnection());
		} catch (DatabaseException e) {
			log.error("checkShowTime() : ", e);
		}
		return confMaps;
	}

	//=============================================================================

	private boolean checkUserAsModerator(String callerNumber) 
	{
		boolean isModerator = false;
		String query = "select is_mod FROM subscriber where cellno = ?";
		try {
			isModerator = (Boolean) DBHelper.getInstance().singleResult(query, super.getConnection(), callerNumber);
		} catch (DatabaseException e) {
			log.error("checkUserAsModerator() : ", e);
		}
		return isModerator;
	}

	//=============================================================================

	private boolean checkIsModeratorOnline()
	{
		boolean isModeratorOnline = false;
		List<Map<String, Object>> modratorList = null;
		String query = "select * from subscriber s  join live_session ls on ls.user_cellno=s.cellno where ls.is_online=1 and s.is_mod = true";
		try {
			modratorList = DBHelper.getInstance().query(query, super.getConnection());

			logger.info("check Mod Map" + modratorList + " size:"+modratorList.size());
			if ( modratorList != null && modratorList.size() >0) {
				isModeratorOnline = true;
				for(Map<String, Object> modratorRecord : modratorList){
					log.debug(modratorRecord.get("cellno").toString() + " is an online modrator");
				}
				return isModeratorOnline;
			}else{
				log.debug("NO MODRATOR FOUND");
				return isModeratorOnline;
				
			}
		} catch (DatabaseException e) 
		{
			log.error("checkIsModeratorOnline() : ", e);
		}
		return isModeratorOnline;
	}
}