package com.ml.task;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ml.db.MongoDB;
import com.ml.model.CrawlPattern;
import com.ml.util.Constants;
import com.ml.util.QueueBucket;

public class Main {
	
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
        
        //3, add listener
        Set<String> visitedUrl = new HashSet<String>();
        ExecutorService service = Executors.newCachedThreadPool();
        
        QueueListenerManager manager = new QueueListenerManager();
        manager.addQueueListener(new ParserListener(crawlList, queues, visitedUrl, manager, service));
        manager.addQueueListener(new InsertListener(mongodb, queues, service));

        //4, schedule crawler to run  
        CrawlerTask ct = new CrawlerTask(crawlList, queues, visitedUrl, manager);
        
		long initialDelay = 1;
		long delay = 10;
		int threadPoolNum = 2;
        // 从现在开始1秒钟之后，每隔10秒钟执行一次job
		ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(threadPoolNum);
		scheduledService.scheduleWithFixedDelay(ct, initialDelay, delay, TimeUnit.SECONDS);

	}
	
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

}
