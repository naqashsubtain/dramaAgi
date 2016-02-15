package com.dramav3.script;

import java.util.Map;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.DatabaseException;
import com.agiserver.helper.common.AbstractBaseAgiScript;

/**
 * 
 * @author Yasir Ashfaq
 */

public class SaveBookMark extends AbstractBaseAgiScript
{
	public SaveBookMark() throws DatabaseException {
		super(true);
	}

	@Override
	public void service(AgiRequest request, AgiChannel channel) throws AgiException
	{
		try
		{

			String cellno = "0" + channel.getVariable("DB_CLI");
			Integer duration = Integer.parseInt(request.getParameter("DURATION"));
			Integer contentid = Integer.parseInt(request.getParameter("CONTENT_ID"));
			String cat = request.getParameter("CAT");
			logger.debug("cellno = " + cellno + " And duration = " + duration);

			String query = "INSERT INTO bookmarks(cellno,content_id,cat,dt,duration)  VALUES (?,?,?,now(),?)  ON DUPLICATE KEY UPDATE dt=now(),duration=?,content_id=?";
			DBHelper.getInstance().executeDml(query, super.getConnection(), cellno,  contentid,cat, duration,duration,contentid);

			// Get duration for history
			Integer start = Integer.valueOf(channel.getVariable("START_TIME"));
			Integer end = Integer.valueOf(channel.getVariable("END_TIME"));
			Integer diff = end - start;
			logger.info("DIFF = " + diff);

			query = "Select content_name from drama_content where id=?";
			Map<String, Object> result = DBHelper.getInstance().firstRow(query, super.getConnection(), new Object[] { contentid });
			
			String contentName="";
			if (result != null) {
				contentName=(String) result.get("content_name");
			}
			query = "INSERT INTO activity_hst(cellno,duration,dt,content_id,content_name,cat) values(?,?,now(),?,?,?)";
			DBHelper.getInstance().executeDml(query, super.getConnection(), cellno, diff,contentid,contentName,cat);
			channel.setVariable("SAVE_BOOKMARK", "YES");
		}
		catch (DatabaseException databaseException)
		{
			logger.error(databaseException.getMessage(), databaseException);
		}
		catch (Exception ex)
		{
			logger.error("Exception", ex);
		}

	}

}
