package com.ml.task;

import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.model.CrawlPattern;
import com.ml.nlp.crawler.Crawler;
import com.ml.util.QueueBucket;

public class CrawlerTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CrawlerTask.class);

    private List<CrawlPattern> crawlList;   //待爬url
    private QueueBucket queues;  //通过QueueBucket得到links队列
    private Set<String> visitedUrl; //已访问过的url
    private QueueListenerManager manager;
    
    public CrawlerTask(List<CrawlPattern> crawlList, QueueBucket queues, 
    		Set<String> visitedUrl, QueueListenerManager manager) {
        this.crawlList = crawlList;
        this.queues = queues;
        this.visitedUrl = visitedUrl;
        this.manager = manager;
    }

    @Override
    public void run() {
    	Crawler crawler = new Crawler();
        for(CrawlPattern cp: crawlList) {
			String crawlUrl = cp.getCrawlUrl();
			String crawPattern = cp.getPatternUrl();
			
			//crawling news
    		Set<String> links = crawler.crawlingNews(crawlUrl, crawPattern);
    		Queue<String> q = queues.get(crawlUrl);
    		int qSize = q.size();
    		//add to queue
    		for(String link: links) {
    			if (link != null && !link.trim().equals("")
    					 && !visitedUrl.contains(link)
    					 && !q.contains(link)) {
    				q.offer(link);
    			}
    		}
    		if(q.size() > 0 && q.size() != qSize) {
    			//notify parser to run
    	        manager.fireWorkspaceCommand("take_crawler");
    		}
    		links.clear();
        }
        printInfo();
        
    }

	private void printInfo() {
		for(CrawlPattern cp: crawlList) {
			String crawlUrl = cp.getCrawlUrl();
			Queue<String> q = queues.get(crawlUrl);
			logger.info("Queue: " + crawlUrl + " --- Unvisited url size: " + q.size());
			logger.info("Visited url size: " + visitedUrl.size());
		}
		System.out.println();
	}
}