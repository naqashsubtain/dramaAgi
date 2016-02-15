package com.dramav3.script;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

public class SendSMSUtil {

	private static final Logger logger = Logger.getLogger(SendSMSUtil.class);
	private static String SEND_SMS_URL = "/cgi-bin/sendsms?user=tester&pass=foobar";

	public static void sendSMS(String smsURL, String to, String from,
			String smsc, String text) {
		try {

			// http://203.215.160.182:13023/cgi-bin/sendsms?username=mlusr4tmptest&password=mlusr4tmptest&from=2929&to=923065890500&text=Test%20Message

			smsURL = smsURL + SEND_SMS_URL;

			logger.debug("Sending SMS Cellno =" + to + "\nSMS=" + text);

			if (invokeUrl(createSmsUrl(from, to, smsc, text, smsURL)) == false) {
				logger.error("SMS sending FAILED to=" + to);
			} else {
				logger.info("SMS sending Successful to=" + to);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static boolean invokeUrl(String url) throws Exception {
		boolean rtnValue = false;
		URL ourl = new URL(url);

		logger.info(url);
		HttpURLConnection c = (HttpURLConnection) ourl.openConnection();
		// Set timeout to 5sec
		c.setConnectTimeout(5000);
		c.setRequestMethod("GET");
		c.connect();
		if ((c.getResponseCode() == HttpURLConnection.HTTP_OK)
				|| (c.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED)) {
			rtnValue = true;
		} else {
			rtnValue = false;
		}

		return rtnValue;
	}

	private static String createSmsUrl(String from, String to, String smsc,
			String msg, String SEND_SMS_URL)
			throws java.io.UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(SEND_SMS_URL);
		sb.append("&smsc=" + smsc);
		sb.append("&from=" + URLEncoder.encode(from, "UTF-8"));
		sb.append("&to=" + URLEncoder.encode(to, "UTF-8"));
		sb.append("&text=" + URLEncoder.encode(msg, "UTF-8"));
		return sb.toString();
	}

}
