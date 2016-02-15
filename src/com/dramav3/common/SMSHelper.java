package com.dramav3.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.log4j.Logger;

public class SMSHelper {
	private static Logger	logger	= Logger.getLogger(SMSHelper.class);

	public static boolean isValidCellNumber(String number) {
		number = formatCellNumber(number);

		if (number.length() != 12 || (number.startsWith("9230")==false)) {
			return false;
		}
		try {
			Long.parseLong(number);
			return true;
		} catch (Exception ex) {
		}

		return false;
	}

	public static String formatCellNumber(String cellNumber) {
		if (cellNumber.startsWith("+92")) {
			cellNumber = cellNumber.substring(1);
		} else if (cellNumber.startsWith("0092")) {
			cellNumber = cellNumber.substring(2);
		} else if (cellNumber.startsWith("03")) {
			cellNumber = "92" + cellNumber.substring(1);
		} else if (cellNumber.startsWith("3")) {
			cellNumber = "92" + cellNumber;
		}
		return cellNumber;
	}

	public static void sendSMS(String smsURL, String to, String from, String smsc, String text) {
		try {
			to = formatCellNumber(to);

			logger.debug("Sending SMS Cellno =" + to + "\nSMS=" + text);

			if (!invokeUrl(createSmsUrl(from, to, smsc, text, smsURL,false))) {
				logger.error("SMS sending FAILED to=" + to);
			} else {
				logger.info("SMS sending Successful to=" + to);
			}

		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static void sendEncodedSMS(String smsURL, String to, String from, String smsc, String text) {
		try {
			to = formatCellNumber(to);

			logger.debug("Sending SMS Cellno =" + to + "\nSMS=" + text);

			if (!invokeUrl(createSmsUrl(from, to, smsc, text, smsURL,true))) {
				logger.error("SMS sending FAILED to=" + to);
			} else {
				logger.info("SMS sending Successful to=" + to);
			}

		} catch (Exception e) {
			logger.error(e);
		}
	}

	private static boolean invokeUrl(String url) throws IOException {
		boolean rtnValue = false;
		URL ourl = new URL(url);

		logger.info(url);
		HttpURLConnection c = (HttpURLConnection) ourl.openConnection();

		c.setConnectTimeout(5000);
		c.setRequestMethod("GET");
		c.connect();
		if ((c.getResponseCode() == 200) || (c.getResponseCode() == 202)) {
			rtnValue = true;
		} else {
			rtnValue = false;
		}

		return rtnValue;
	}

	private static String createSmsUrl(String from, String to, String smsc, String msg, String SEND_SMS_URL,boolean encoded) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(SEND_SMS_URL);
		sb.append("&smsc=" + smsc);
		sb.append("&from=" + URLEncoder.encode(from, "UTF-8"));
		sb.append("&to=" + URLEncoder.encode(to, "UTF-8"));
		if(encoded){
			sb.append("&coding=" + URLEncoder.encode("2", "UTF-8"));
			 byte[] s = msg.getBytes("US-ASCII");
			 String temp ="";
		        for(byte b : s){
		            int a = (int)b;
		            temp +=intToHex4(a);
		        }
		        msg = temp;
		        sb.append("&text=" +msg);
		}else{
		sb.append("&text=" + URLEncoder.encode(msg, "UTF-8"));
		}
		return sb.toString();
	}
	
	static String intToHex4(int input) {
        String str = Integer.toHexString(input);
        String str2 = "";

        switch (4 - str.length()) {
            case 3:
                str2 = "000" + str;
                break;
            case 2:
                str2 = "00" + str;
                break;
            case 1:
                str2 = "0" + str;
                break;
            default:
                str2 = str;
                break;
        }

        String str3 = "%"+str2.substring(0, 2)+"%"+str2.substring(2);
        return str3;
    }
}
