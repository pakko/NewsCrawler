package com.ml.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	private static String pattern = "yyyy年MM月dd日HH:mm";
	private static DateFormat sdf = new SimpleDateFormat(pattern);

	public static Date extractDate(String content) {
		if(content.equals("")) {
			return new Date();
		}
		
		content = content.replaceAll("&nbsp;", "");
		try {
			Date date = sdf.parse(content);
			//System.out.println(date);
			return date;
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public static void main(String[] args) {
		String str = "2013年10月16日&nbsp;10:19";
		extractDate(str);
	}

}
