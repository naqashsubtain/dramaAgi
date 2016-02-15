package com.dramav3.script;

import java.util.List;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;

public class SetLiveSessionVars extends AbstractBaseAgiScript {

	@Override
	public void service(AgiRequest req, AgiChannel channel) throws AgiException {

		channel.setVariable("IS_AGI_UP", "Y");

		String cellNumber = formatCellNumber(channel.getVariable("DB_CLI"));
		cellNumber = "0" + cellNumber.substring(2);
		//String cellno = "0" + cellNumber.substring(2);
		String curChannel = channel.getVariable("CHANNEL");
		String ismod = channel.getVariable("IS_MOD");
		String event = req.getParameter("event");
		String user = req.getParameter("user");
		String unique_id = channel.getVariable("DB_UNIQUE_ID");
		StringBuilder query = new StringBuilder(100);
		boolean db_isModrator = false;

		logger.debug("cellNumber : " + cellNumber + ", user : " + user + ", event : "
				+ event + ", current channel : "+ curChannel + ", unique_id : " + unique_id + ", is_mod : " + ismod);
		try {
			db_isModrator = (Boolean) DBHelper.getInstance().singleResult(
					"Select is_mod from subscriber where cellno = ?", super.getConnection(),cellNumber);
			
			channel.setVariable("IS_MOD", 				   db_isModrator? "TRUE" : "FALSE");
			ismod = channel.getVariable("IS_MOD");
			
			
			logger.debug("cellNumber : " + cellNumber + ", user : " + user + ", event : "
					+ event + ", current channel : "+ curChannel + ", unique_id : " + unique_id + ", is_mod : " + ismod);
			
			if (user.equals("new")) {
				List<Object> rs = DBHelper
						.getInstance()
						.insert("insert into live_session (user_cellno, user_cur_channel, user_cur_unique_id, is_online, join_date,last_entry_date,unmute_count,is_unmute)"
								+ "values (?,?,?,true,now(),now(),?,?)",
								super.getConnection(), cellNumber, curChannel,
								unique_id, ismod.equals("TRUE") ? 1 : 0,
								ismod.equals("TRUE") ? true : false);
				long autokey = Long.valueOf(rs.get(0).toString());
				logger.info("LIVE_SESSION_ID " + autokey);
				//channel.setVariable("LIVE_SESSION_ID", "" + rs);
				channel.setVariable("LIVE_SESSION_ID", "" + autokey);
			} else if (user.equals("old")) {
				long session_id = Long.valueOf(channel
						.getVariable("LIVE_SESSION_ID").replaceAll("\\[", "")
						.replaceAll("\\]", ""));
				query.setLength(0);
				query.append("Update live_session set ");

				if (event.equals("mute")) {
					query.append(" is_unmute = false, ");
					query.append(" talk_seconds = IFNULL(talk_seconds,0) + (TIME_TO_SEC(TIMEDIFF(now(),unmute_date))) ");
				} else if (event.equals("unmute")) {
					query.append(" is_unmute = true, ");
					query.append("unmute_count = (unmute_count + 1), ");
					query.append(" unmute_date = now() ");
				} else if (event.equals("hangup")) {
					query.append(" talk_seconds = IF(is_unmute=TRUE, IFNULL(talk_seconds,0) + (TIME_TO_SEC(TIMEDIFF(NOW(),unmute_date))),talk_seconds), ");
					query.append(" is_unmute = false, ");
					query.append(" left_date = now(), ");
					query.append(" is_online = FALSE ");
				}
				query.append(" where session_id = ?");
				logger.debug("Live Session Update query: " + query.toString()
						+ " " + session_id);
				DBHelper.getInstance().executeDml(query.toString(),
						super.getConnection(), session_id);

			}

		} catch (Exception e) {
			channel.setVariable("ERROR", e.getMessage());
			logger.debug("ERROR", e);

		}
	}

}
