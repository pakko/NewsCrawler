package com.ml.task;

import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.ml.model.CrawlPattern;
import com.ml.nlp.crawler.Crawler;
import com.ml.queue.QueueBucket;

public class CrawlerTask extends Thread {
    private List<CrawlPattern> crawlList;   //待爬url
    private QueueBucket queues;  //通过QueueBucket得到links队列
    private Crawler crawler;
    
    public CrawlerTask(List<CrawlPattern> crawlList, QueueBucket queues) {
        this.crawlList = crawlList;
        this.queues = queues;
        this.crawler = new Crawler();
    }

    public void run() {
        for(CrawlPattern cp: crawlList) {
			String crawlUrl = cp.getCrawlUrl();
			String crawPattern = cp.getPatternUrl();
    		Set<String> links = crawler.crawlingNews(crawlUrl, crawPattern);
    			
    		for(String link: links) {
    			Queue<String> q = queues.get(crawlUrl);
    			q.offer(link);
    		}
        }
    }
}