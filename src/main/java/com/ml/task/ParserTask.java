package com.ml.task;

import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.model.CrawlPattern;
import com.ml.model.News;
import com.ml.nlp.parser.IParser;
import com.ml.nlp.parser.SinaNewsParser;
import com.ml.nlp.parser.SohuNewsParser;
import com.ml.qevent.QueueListenerManager;
import com.ml.util.Constants;
import com.ml.util.QueueBucket;

public class ParserTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ParserTask.class);

	private CrawlPattern cp;   //待爬url
    private QueueBucket queues;
    private Set<String> visitedUrl;
    private QueueListenerManager manager;
    
    public ParserTask(CrawlPattern cp, QueueBucket queues, 
    		Set<String> visitedUrl, QueueListenerManager manager) {
        this.cp = cp;
        this.queues = queues;
        this.visitedUrl = visitedUrl;
        this.manager = manager;
    }

    public void run() {
    	String queueName = cp.getCrawlUrl();
		String type = cp.getType();
		IParser<?> parser = getParser(type);
		
		doParser(parser, queueName);
    }
    
    private void doParser(IParser<?> parser, String queueName) {
    	Queue<String> queue = queues.get(queueName);
        Queue<News> parserQueue = queues.get(Constants.parserQueueName);
    	String url;
    	
    	if(queue.size() != 0) {
    		while((url = queue.poll()) != null) {
            	//1) parse url
            	News news = (News) parser.parse(url);
            	
            	// 2) 将该 url 放入到已访问的 URL 中
            	visitedUrl.add(url);
            	
            	// 3) news为空则不放入分析队列
            	if(news == null || news.getDate() == null || 
    					news.getTitle() == null || news.getContent() == null) {
    				continue;
    			}
            	
            	// 4) 设置该新闻的原来类别, for test
            	//http://it.sohu.com -> it
    			news.setOriginalCategory(queueName.substring(queueName.indexOf("/") + 2, 
    					queueName.indexOf(".")));
    			
    			// 5) 放到待分析队列
    			parserQueue.offer(news);
            }
    		logger.info("parser queue size: " + parserQueue.size());
    		
    		//notify insert task
    		manager.fireWorkspaceCommand("take_parser");
    	}
        
	}
	
	private IParser<?> getParser(String name) {
		IParser<?> parser = null;
		
		if(name.equals("sohu")) {
			parser = new SohuNewsParser();
		}
		else if(name.equals("sina")) {
			parser = new SinaNewsParser();
		}
		
		return parser;
	}
}