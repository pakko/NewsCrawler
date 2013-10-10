package com.ml.qevent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.ml.model.CrawlPattern;
import com.ml.task.ParserTask;
import com.ml.util.QueueBucket;

public class ParserListener implements QueueListener {

	
	private List<CrawlPattern> crawlList;   //待爬url
    private QueueBucket queues;
    private Set<String> visitedUrl;
    private QueueListenerManager manager;
    private ExecutorService service;
    
    public ParserListener(List<CrawlPattern> crawlList, QueueBucket queues, 
    		Set<String> visitedUrl, QueueListenerManager manager, ExecutorService service) {
        this.crawlList = crawlList;
        this.queues = queues;
        this.visitedUrl = visitedUrl;
        this.manager = manager;
        this.service = service;
    }
    

	@Override
	public void queueEvent(QueueEvent event) {
		if (event.getQueueState() != null && event.getQueueState().equals("take_crawler")) {
			for(CrawlPattern cp: crawlList) {
				ParserTask pt = new ParserTask(cp, queues, visitedUrl, manager);
				service.execute(pt);
			}
		}
	}

}
