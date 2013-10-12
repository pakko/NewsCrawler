package com.ml.task;

import java.io.FileInputStream;
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
import com.ml.qevent.InsertListener;
import com.ml.qevent.ParserListener;
import com.ml.qevent.QueueListenerManager;
import com.ml.util.Constants;
import com.ml.util.QueueBucket;

public class Main {
	
	public static void main(String[] args) throws Exception  {
		String confFile = Constants.defaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);

		//1, get crawl list
		List<CrawlPattern> crawlList = mongodb.findAll(CrawlPattern.class, 
				Constants.crawlPatternCollectionName);

		//2, initial queues
		QueueBucket queues = new QueueBucket();
		for(CrawlPattern cp: crawlList) {
			String crawlUrl = cp.getCrawlUrl();
			queues.add(crawlUrl, ConcurrentLinkedQueue.class);
        }
        queues.add(Constants.parserQueueName, ConcurrentLinkedQueue.class);
        
        //3, add listener
        Set<String> visitedUrl = new HashSet<String>();
        int fixedThreadPoolNum = Integer.valueOf(props.getProperty("fixed.thread_pool_num"));
        ExecutorService service = Executors.newFixedThreadPool(fixedThreadPoolNum);
        
        QueueListenerManager manager = new QueueListenerManager();
        manager.addQueueListener(new ParserListener(crawlList, queues, visitedUrl, manager, service));
        manager.addQueueListener(new InsertListener(mongodb, queues, service));

        //4, schedule crawler to run  
        CrawlerTask ct = new CrawlerTask(crawlList, queues, visitedUrl, manager);
        
		long initialDelay = Long.valueOf(props.getProperty("schedule.initial.delay"));
		long delay = Long.valueOf(props.getProperty("schedule.delay"));
		int threadPoolNum = Integer.valueOf(props.getProperty("schedule.thread_pool_num"));

        // 从现在开始1分钟之后，每隔1小时执行一次job
		ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(threadPoolNum);
		scheduledService.scheduleWithFixedDelay(ct, initialDelay, delay, TimeUnit.MINUTES);

	}

}
