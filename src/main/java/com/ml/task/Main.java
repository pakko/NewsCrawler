package com.ml.task;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.db.MongoDB;
import com.ml.model.CrawlPattern;
import com.ml.queue.QueueBucket;
import com.ml.util.Constants;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private MongoDB getDB() {
		String confFile = Constants.defaultConfigFile;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(confFile));
		} catch (IOException e) {
			System.out.println(e.toString());
			return null;
		}
		MongoDB mongodb = new MongoDB(props);
		return mongodb;
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException  {
		Main main = new Main();
		MongoDB mongodb = main.getDB();
		
		List<CrawlPattern> crawlList = mongodb.findAll(CrawlPattern.class, 
				Constants.crawlPatternCollectionName);

		QueueBucket queues = new QueueBucket();
		for(CrawlPattern cp: crawlList) {
			String crawlUrl = cp.getCrawlUrl();
			queues.add(crawlUrl, ConcurrentLinkedQueue.class);
        }
        queues.add("parserQueue", ConcurrentLinkedQueue.class);
        queues.add("analyzerQueue", ConcurrentLinkedQueue.class);
        
        Set<String> visitedUrl = new HashSet<String>();
        
		CrawlerTask ct = new CrawlerTask(crawlList, queues);
		
		for(int i = 0; i < crawlList.size(); i++) {
			ParserTask pt = new ParserTask(crawlList, queues);
		}
		
	}

}
