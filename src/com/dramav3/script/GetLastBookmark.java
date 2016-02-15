package com.dramav3.script;

import java.util.Map;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.DatabaseException;
import com.agiserver.helper.common.AbstractBaseAgiScript;

/**
 * 
 * @author Yasir
 */

public class GetLastBookmark extends AbstractBaseAgiScript
{
	private static final Logger	logger	= Logger.getLogger(GetLastBookmark.class);

	public GetLastBookmark() throws DatabaseException {
		super(true);
	}

	@Override
	public void service(AgiRequest request, AgiChannel channel) throws AgiException
	{
		try
		{

			String cellno = "0" + channel.getVariable("DB_CLI");
			Integer dramaId = Integer.parseInt(channel.getVariable("DRAMA_ID"));
			// SELECT  b.drama_id, b.episode_id,e.episode_number, b.duration 	FROM bookmarks b,episode e,drama d WHERE b.cellno = ?  AND b.duration>0 AND b.drama_id = d.id AND d.status >= 0 AND b.episode_id = e.id AND e.status = 100 ORDER BY b.dt desc limit 1;
			String query = "SELECT  b.drama_id, b.episode_id,e.episode_number, b.duration 	FROM bookmarks b,episode e,drama d "
					+ " WHERE b.cellno = ?  AND b.duration>0 AND b.drama_id = d.id AND d.status >= 0 AND d.id = ? AND b.episode_id = e.id AND e.status = 100 ORDER BY b.dt desc limit 1";

			Map<String, Object> lastBookmarked = DBHelper.getInstance().firstRow(query, super.getConnection(), cellno,dramaId);

			if (lastBookmarked != null && lastBookmarked.size() != 0)
			{
				channel.setVariable("LAST_BOOLMARKED_EPISODE", "YES");
				channel.setVariable("DRAMA_ID", lastBookmarked.get("drama_id").toString());
				channel.setVariable("EPISODE_ID", lastBookmarked.get("episode_id").toString());
				channel.setVariable("EPISODE_NUMBER", lastBookmarked.get("episode_number").toString());
				channel.setVariable("EPISODE_DUR", lastBookmarked.get("duration").toString());

				return;
			}

		}
		catch (DatabaseException databaseException)
		{
			logger.error(databaseException.getMessage(), databaseException);
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}
		channel.setVariable("EPISODE_DUR", "0");
		channel.setVariable("LAST_BOOLMARKED_EPISODE", "NO");

	}
}
