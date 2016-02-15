package com.dramav3.script;

/*    */
/*    */import com.agiserver.helper.DBHelper;
/*    */
import com.agiserver.helper.DatabaseException;
/*    */
import com.agiserver.helper.common.AbstractBaseAgiScript;
/*    */
import org.apache.log4j.Logger;
/*    */
import org.asteriskjava.fastagi.AgiChannel;
/*    */
import org.asteriskjava.fastagi.AgiException;
/*    */
import org.asteriskjava.fastagi.AgiRequest;

/*    */
/*    */public class SaveActivity extends AbstractBaseAgiScript
/*    */{
	/* 19 */private static final Logger logger = Logger
			.getLogger(SaveActivity.class);

	/*    */
	/*    */public SaveActivity() throws DatabaseException {
		/* 22 */super(true);
		/*    */}

	/*    */
	/*    */public void service(AgiRequest request, AgiChannel channel)
	/*    */throws AgiException
	/*    */{
		/*    */try
		/*    */{
			/* 31 */Integer start_time = Integer.valueOf(channel
					.getVariable("START_TIME"));
			/* 32 */Integer end_time = Integer.valueOf(channel
					.getVariable("END_TIME"));
			/* 33 */Integer diff = Integer.valueOf(channel.getVariable("DIFF"));
			/* 34 */String activityType = channel.getVariable("ACTIVITY_TYPE");
			/* 35 */String dtmf = channel.getVariable("DTMF");
			/* 36 */String cellno = "0" + request.getParameter("CALLER_NUM");
			/*    */
			/* 38 */logger.debug("Start_Time = " + start_time);
			/* 39 */logger.debug("End_Time = " + end_time);
			/* 40 */logger.debug("Diff = " + diff);
			/* 41 */logger.debug("Activity_Type = " + activityType);
			/*    */
			/* 43 */StringBuilder dml = new StringBuilder(300);
			/* 44 */dml
					.append("insert into activity(cellno,dt,activity_type,duration,dtmf)");
			/* 45 */dml.append(" values(?, now(), ?, ?,?)");
			/* 46 */DBHelper.getInstance().executeDml(dml.toString(),
					super.getConnection(),
					new Object[] { cellno, activityType, diff, dtmf });
			/* 47 */channel.setVariable("__SAVE_ACTIVITY", "YES");
			/* 48 */logger.info("Save successfully -  activity typye = "
					+ activityType + ": diff = " + diff + " , cellno = "
					+ cellno);
			/* 49 */return;
			/*    */}
		/*    */catch (DatabaseException databaseException)
		/*    */{
			/* 54 */logger.error(databaseException.getMessage(),
					databaseException);
			/*    */}
		/*    */catch (Exception ex)
		/*    */{
			/* 58 */logger.error(ex.getMessage(), ex);
			/*    */}
		/*    */
		/* 61 */channel.setVariable("__SAVE_ACTIVITY", "NO");
		/*    */}
	/*    */
}

/*
 * Location:
 * /home/abdul/Desktop/IVR/segment_tracking_agi_server/segment_tracking.jar
 * Qualified Name: com.segment.tracking.script.SaveActivity JD-Core Version:
 * 0.6.2
 */