package com.dramav3.script;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;

public class GetingStories extends AbstractBaseAgiScript {
	private final static Logger log = Logger.getLogger(GetingStories.class);

	@Override
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		// TODO Auto-generated method stub
		try {

			String cellno = "0" + channel.getVariable("DB_CLI");
			String cat = channel.getVariable("CAT");
			String contentType = channel.getVariable("GET_CONTENT_TYPE");
			String currentContentId = channel.getVariable("CURRENT_CONTENT_ID");
			

			String context = request.getContext();
			
			log.debug(cellno+" From caller Context:   " + request.getContext());
			log.debug(cellno+" From  contentType:   " + contentType);
			log.debug(cellno+" From cat selected   cat: " + cat);
			log.debug(cellno+" From cat selected   currentContentId: " + currentContentId);
			

			if(contentType.equals("BOOKMARKED")){
				
				Map<String, Object> bookmark = DBHelper.getInstance().firstRow("select content_id,duration from bookmarks where cat=? and cellno=?",	super.getConnection(), new Object[] { cat,cellno });
				
				if(bookmark==null){
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
					return;				
				}
				
				int bookmarkedContentid = (Integer) bookmark.get("content_id");
				Integer duration = (Integer) bookmark.get("duration");
				
				List<Map<String, Object>> result = DBHelper.getInstance().query("select * from drama_content where date(dt)=(select date(dt) from drama_content where id=?) and cat=? order by content_order",
						super.getConnection(), new Object[] { bookmarkedContentid,cat });
				
				if (result != null) {
					int i=1;
					for (Map<String, Object> map : result) {
						
						String filename = (String) map.get("file");
						Integer contentId = (Integer) map.get("id");
						
						channel.setVariable("file"+i, filename);
						channel.setVariable("File_Name"+i, filename);
						channel.setVariable("content_id"+i, ""+contentId);
						if(bookmarkedContentid==contentId){
							
							channel.setVariable("CONTENT_DUR"+i,""+duration);
							channel.setVariable("INDEX", ""+i);
						}else{
						channel.setVariable("CONTENT_DUR"+i,""+0);
						}
						i++;
					}
					int size=result.size();
					channel.setVariable("TOTAL_FILES", ""+size);
					channel.setVariable("HAVE_CAT", "YES");
				} else {
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
				}


			}else if(contentType.equals("FILE_START")){
				Map<String, Object> bookmark = DBHelper.getInstance().firstRow("select content_id,duration from bookmarks where cat=? and cellno=?",	super.getConnection(), new Object[] { cat,cellno });
				
				if(bookmark==null){
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
					return;				
				}
				
				int bookmarkedContentid = (Integer) bookmark.get("content_id");
				Integer duration = (Integer) bookmark.get("duration");
				
				List<Map<String, Object>> result = DBHelper.getInstance().query("select * from drama_content where date(dt)=(select date(dt) from drama_content where id=?) and cat=? order by content_order",
						super.getConnection(), new Object[] { bookmarkedContentid,cat });
				
				if (result != null) {
					int i=1;
					for (Map<String, Object> map : result) {
						
						String filename = (String) map.get("file");
						Integer contentId = (Integer) map.get("id");
						
						channel.setVariable("file"+i, filename);
						channel.setVariable("File_Name"+i, filename);
						channel.setVariable("content_id"+i, ""+contentId);
						channel.setVariable("CONTENT_DUR"+i,""+0);
						
						i++;
					}
					int size=result.size();
					channel.setVariable("TOTAL_FILES", ""+size);
					channel.setVariable("HAVE_CAT", "YES");
				} else {
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
				}
			}else if(contentType.equals("FROM_START")){
				
				Map<String,Object> datemap=DBHelper.getInstance().firstRow("select min(date(dt)) start_date from drama_content where cat=?", super.getConnection(),cat);
				if(datemap ==null	){
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
					return;
				}
				
				
				String mindate= new String( (byte[])datemap.get("start_date"));
				logger.info(cellno+"Start date is " + mindate);
				
				List<Map<String, Object>> result = DBHelper.getInstance().query("select * from drama_content where cat = ? and date(dt)=?  order by content_order",
						super.getConnection(), new Object[] { cat,mindate });
				
				
				if (result != null) {
					logger.info(cellno +" Total Files:" + result.size());
					int i=1;
					for (Map<String, Object> map : result) {
						
						String filename = (String) map.get("file");
						Integer contentId = (Integer) map.get("id");
						channel.setVariable("file"+i, filename);
						channel.setVariable("File_Name"+i, filename);
						channel.setVariable("content_id"+i, ""+contentId);
						channel.setVariable("CONTENT_DUR"+i,""+0);
						
						i++;
					}
					int size=result.size();
					channel.setVariable("TOTAL_FILES", ""+size);
					channel.setVariable("HAVE_CAT", "YES");
				} else {
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
				}
				

			}else if(contentType.equals("TODAY_START")){
				
			}else if(contentType.equals("YESTERDAY_START")){
				

				Map<String,Object> datemap=DBHelper.getInstance().firstRow("select date(dt) previous_dt from drama_content where date(dt)< (select date(dt) from drama_content where id=?) order by dt desc limit 1", super.getConnection(),currentContentId);
				if(datemap ==null	){
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
					return;
				}
				
				
				java.sql.Date prevoiusDay= (java.sql.Date)datemap.get("previous_dt");
				logger.info(cellno+"Start date is " + prevoiusDay);
				
				List<Map<String, Object>> result = DBHelper.getInstance().query("select * from drama_content where cat = ? and date(dt)=? order by content_order",
						super.getConnection(), new Object[] { cat,prevoiusDay });

				if (result != null) {
					int i=1;
					for (Map<String, Object> map : result) {
						
						String filename = (String) map.get("file");
						Integer contentId = (Integer) map.get("id");
						channel.setVariable("file"+i, filename);
						channel.setVariable("File_Name"+i, filename);
						channel.setVariable("content_id"+i, ""+contentId);
						channel.setVariable("CONTENT_DUR"+i,""+0);
						
						i++;
					}
					int size=result.size();
					channel.setVariable("TOTAL_FILES", ""+size);
					channel.setVariable("HAVE_CAT", "YES");
				} else {
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
				}

				
			}else if(contentType.equals("NEXT_DAY_START")){
				
				Map<String,Object> datemap=DBHelper.getInstance().firstRow("select date(dt) next_dt from drama_content where date(dt) > (select date(dt) from drama_content where id=?) order by dt limit 1", super.getConnection(),currentContentId);
				if(datemap ==null	){
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
					return;
				}
				
				
				java.sql.Date nextDay= (java.sql.Date)datemap.get("next_dt");
				logger.info(cellno+"Start date is " + nextDay);
				
				List<Map<String, Object>> result = DBHelper.getInstance().query("select * from drama_content where cat = ? and date(dt)=? and date(dt)<=curdate() order by content_order",
						super.getConnection(), new Object[] { cat,nextDay });

				if (result != null) {
					int i=1;
					for (Map<String, Object> map : result) {
						
						String filename = (String) map.get("file");
						Integer contentId = (Integer) map.get("id");
						channel.setVariable("file"+i, filename);
						channel.setVariable("File_Name"+i, filename);
						channel.setVariable("content_id"+i, ""+contentId);
						channel.setVariable("CONTENT_DUR"+i,""+0);
						
						i++;
					}
					int size=result.size();
					channel.setVariable("TOTAL_FILES", ""+size);
					channel.setVariable("HAVE_CAT", "YES");
				} else {
					channel.setVariable("HAVE_CAT", "NO");
					channel.setVariable("TOTAL_FILES", "0");
				}
			}
			
			
		} catch (Exception e) {
			channel.setVariable("HAVE_CAT", "NO");
			channel.setVariable("TOTAL_FILES", "0");
			channel.verbose("Exception: [" + e.getClass() + "] " + e.getMessage(), 0);
			log.error("Error",e);
		} finally {

		}

	}

}
