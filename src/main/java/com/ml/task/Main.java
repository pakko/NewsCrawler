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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.db.MongoDB;
import com.ml.model.CrawlPattern;
import com.ml.util.Constants;
import com.ml.util.QueueBucket;

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
		
		//1, get crawl list
		MongoDB mongodb = main.getDB();
		List<CrawlPattern> crawlList = mongodb.findAll(CrawlPattern.class, 
				Constants.crawlPatternCollectionName);

		//2, initial queues
		QueueBucket queues = new QueueBucket();
		for(CrawlPattern cp: crawlList) {
			String crawlUrl = cp.getCrawlUrl();
			queues.add(crawlUrl, ConcurrentLinkedQueue.class);
        }
        queues.add("parserQueue", ConcurrentLinkedQueue.class);
        queues.add("analyzerQueue", ConcurrentLinkedQueue.class);
        
        //3, schedule crawler to run  
        Set<String> visitedUrl = new HashSet<String>();
        CrawlerTask ct = new CrawlerTask(crawlList, queues, visitedUrl);
        
		long initialDelay = 1;
		long delay = 10;
		int threadPoolNum = 2;
        // 从现在开始1秒钟之后，每隔10秒钟执行一次job
		ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(threadPoolNum);
		scheduledService.scheduleWithFixedDelay(ct, initialDelay, delay, TimeUnit.SECONDS);
		
		//4, add several thread to parse url
/*		ExecutorService service = Executors.newCachedThreadPool();
		for(CrawlPattern cp: crawlList) {
			ParserTask pt = new ParserTask(cp, queues, visitedUrl);
			service.execute(pt);
        }
		
		//5, insert news to db
		//ExecutorService insertService = Executors.newSingleThreadExecutor();
		InsertTask it = new InsertTask(mongodb, queues);
		//insertService.execute(it);
		service.execute(it);*/

	}

}
