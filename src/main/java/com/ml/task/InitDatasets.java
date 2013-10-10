package com.ml.task;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ml.db.MongoDB;
import com.ml.model.CrawlPattern;
import com.ml.util.Constants;

public class InitDatasets {
	private MongoDB mongodb;
	
	public InitDatasets(MongoDB mongodb) {
		this.mongodb = mongodb;
	}
	
	private void saveCrawlPattern() {
		Map<String, String> sohuUrlPatterns = new HashMap<String, String>();
		//sohuUrlPatterns.put("http://news.sohu.com", "http://news.sohu.com/[\\d]+/n[\\d]+.shtml");
		sohuUrlPatterns.put("http://it.sohu.com", "http://it.sohu.com/[\\d]+/n[\\d]+.shtml");
		sohuUrlPatterns.put("http://learning.sohu.com", "http://learning.sohu.com/[\\d]+/n[\\d]+.shtml");
		sohuUrlPatterns.put("http://travel.sohu.com", "http://travel.sohu.com/[\\d]+/n[\\d]+.shtml");
		sohuUrlPatterns.put("http://health.sohu.com", "http://health.sohu.com/[\\d]+/n[\\d]+.shtml");
		sohuUrlPatterns.put("http://mil.sohu.com", "http://mil.sohu.com/[\\d]+/n[\\d]+.shtml");
		sohuUrlPatterns.put("http://cul.sohu.com", "http://cul.sohu.com/[\\d]+/n[\\d]+.shtml");
		sohuUrlPatterns.put("http://sports.sohu.com", "http://sports.sohu.com/[\\d]+/n[\\d]+.shtml");
		sohuUrlPatterns.put("http://business.sohu.com", "http://business.sohu.com/[\\d]+/n[\\d]+.shtml");
		sohuUrlPatterns.put("http://auto.sohu.com", "http://auto.sohu.com/[\\d]+/n[\\d]+.shtml");

		Map<String, String> sinaUrlPatterns = new HashMap<String, String>();
		//sinaUrlPatterns.put("http://news.sina.com.cn", "http://news.sina.com.cn/([\\w]+/)+[\\d]+-[\\d]+-[\\d]+/[\\d]+.shtml");
		sinaUrlPatterns.put("http://finance.sina.com.cn", "http://finance.sina.com.cn/([\\w]+/)+[\\d]+/[\\d]+.shtml");
		sinaUrlPatterns.put("http://mil.news.sina.com.cn", "http://mil.news.sina.com.cn/[\\d]+-[\\d]+-[\\d]+/[\\d]+.html");
		sinaUrlPatterns.put("http://tech.sina.com.cn", "http://tech.sina.com.cn/([\\w]+/)+[\\d]+-[\\d]+-[\\d]+/[\\d]+.shtml");
		sinaUrlPatterns.put("http://edu.sina.com.cn", "http://edu.sina.com.cn/([\\w]+/)+[\\d]+-[\\d]+-[\\d]+/[\\d]+.shtml");
		sinaUrlPatterns.put("http://travel.sina.com.cn", "http://travel.sina.com.cn/([\\w]+/)+[\\d]+-[\\d]+-[\\d]+/[\\d]+.shtml");
		sinaUrlPatterns.put("http://auto.sina.com.cn", "http://auto.sina.com.cn/([\\w]+/)+[\\d]+-[\\d]+-[\\d]+/[\\d]+.shtml");
		sinaUrlPatterns.put("http://sports.sina.com.cn", "http://sports.sina.com.cn/([\\w]+/)+[\\d]+-[\\d]+-[\\d]+/[\\d]+.shtml");
		sinaUrlPatterns.put("http://health.sina.com.cn", "http://health.sina.com.cn/([\\w]+/)+[\\d]+-[\\d]+-[\\d]+/[\\d]+.shtml");

		for(String url: sohuUrlPatterns.keySet()) {
			CrawlPattern cp = new CrawlPattern(null, url, sohuUrlPatterns.get(url), "sohu");
			mongodb.save(cp, Constants.crawlPatternCollectionName);
		}
		for(String url: sinaUrlPatterns.keySet()) {
			CrawlPattern cp = new CrawlPattern(null, url, sinaUrlPatterns.get(url), "sina");
			mongodb.save(cp, Constants.crawlPatternCollectionName);
		}
	}
	
	public static void main(String[] args) throws Exception  {
		String confFile = Constants.defaultConfigFile;
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		InitDatasets mk = new InitDatasets(mongodb);
		mk.saveCrawlPattern();
	}
}
