package com.ml.task;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


import com.ml.model.CrawlPattern;
import com.ml.nlp.crawler.Crawler;
import com.ml.nlp.parser.IParser;
import com.ml.nlp.parser.SinaNewsParser;
import com.ml.nlp.parser.SohuNewsParser;
import com.ml.queue.QueueBucket;

public class ParserTask extends Thread {
	private List<CrawlPattern> crawlList;   //待爬url
    private QueueBucket queues;  //通过QueueBucket得到links队列
    
    public ParserTask(List<CrawlPattern> crawlList, QueueBucket queues) {
        this.crawlList = crawlList;
        this.queues = queues;
    }

    public void run() {
    	for(CrawlPattern cp: crawlList) {
			String queueName = cp.getCrawlUrl();
			String type = cp.getType();
			IParser<?> parser = getParser(type);
			
			doParser(parser, queueName);
		}
    }
    
    private void doParser(IParser<?> parser, String queueName) {
    	Queue<String> queue = queues.get(queueName);
    	int timer = 0; //超时计时器
/*
        while(timer < 30) { //如果连续30分钟bookQueue均为空,则超时,线程结束
            if(queue.size() != 0) { //如果队列不为空
                Book book;
                while((book = queue.poll()) != null) {
                    System.out.println(book.getBookName()); //把这步当成插入数据库吧
                }
                timer = 0;  //超时计时器清零
            } else {
                try {
                    sleep(60 * 1000);   //等待爬虫一分钟
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer++;    //timer时间+1
            }
        }
			News news = (News) parser.parse(visitUrl);
			
			// 该 url 放入到已访问的 URL 中
			LinkDB.addVisitedUrl(visitUrl);
						
			if(news == null || news.getDate() == null || 
					news.getTitle() == null || news.getContent() == null) {
				continue;
			}
			
			//http://it.sohu.com -> it
			news.setOriginalCategory(queueName.substring(queueName.indexOf("/") + 2, queueName.indexOf(".")));
*/
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