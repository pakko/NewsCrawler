package com.ml.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import com.ml.db.MongoDB;
import com.ml.model.News;

public class TimeUtils {
	private static DateFormat sdf;
	static {
		String pattern = "yyyy年MM月dd日HH:mm";
		TimeZone timeZone = TimeZone.getTimeZone("GMT+8:00");
		sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(timeZone);
	}
	
	public static Date extractDate(String content) {
		Date defaultDate = new Date();
		content = content.replaceAll("&nbsp;", "");
		try {
			Date date = sdf.parse(content);
			//System.out.println(date);
			return date;
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return defaultDate;
	}
	
	public static Date formatDate(Date date) {
		Calendar cal = Calendar.getInstance();    
		cal.setTime(date);    
		cal.add(Calendar.HOUR, -8);
		return cal.getTime();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String str = "2013年09月12日19:07";
		Date date = extractDate(str);
		//date = formatDate(date);
		System.out.println(date.toString());
		
		String confFile = Constants.defaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		News news = new News();
		news.setUrl("xxxx");
		news.setDate(date);
		mongodb.save(news, "news3");
		List<News> list = mongodb.findAll(News.class, "news3");
		System.out.println(list.toString());
	}

}
