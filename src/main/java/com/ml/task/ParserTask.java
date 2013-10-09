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
import com.ml.util.QueueBucket;

public class ParserTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ParserTask.class);

	
	private CrawlPattern cp;   //待爬url
    private QueueBucket queues;  //通过QueueBucket得到links队列
    private Set<String> visitedUrl; //已访问过的url
    
    public ParserTask(CrawlPattern cp, QueueBucket queues, Set<String> visitedUrl) {
        this.cp = cp;
        this.queues = queues;
        this.visitedUrl = visitedUrl;
    }

    public void run() {
    	String queueName = cp.getCrawlUrl();
		String type = cp.getType();
		IParser<?> parser = getParser(type);
		
		while (true) {
			doParser(parser, queueName);
		}
    }
    
    private void doParser(IParser<?> parser, String queueName) {
    	Queue<String> queue = queues.get(queueName);
    	int analyzedNum = 0;
        if(queue.size() != 0) { //如果队列不为空
            String url;
            Queue<News> parserQueue = queues.get("parserQueue");
            while((url = queue.poll()) != null) {
            	News news = (News) parser.parse(url);
            	// 该 url 放入到已访问的 URL 中
            	visitedUrl.add(url);
            	if(news == null || news.getDate() == null || 
    					news.getTitle() == null || news.getContent() == null) {
    				continue;
    			}
            	//http://it.sohu.com -> it
    			news.setOriginalCategory(queueName.substring(queueName.indexOf("/") + 2, queueName.indexOf(".")));
    			// 放到待分析队列
    			parserQueue.add(news);
            }
            analyzedNum = parserQueue.size();
        } else {
            try {
            	logger.info("sleep 20s...");
                Thread.sleep(20 * 1000);   //等待爬虫一分钟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		logger.info("parser queue size: " + analyzedNum);

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