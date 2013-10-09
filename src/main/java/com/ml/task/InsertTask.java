package com.ml.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.db.MongoDB;
import com.ml.model.News;
import com.ml.util.Constants;
import com.ml.util.QueueBucket;

public class InsertTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(InsertTask.class);

	
	private MongoDB mongodb;
    private QueueBucket queues;
	List<News> list = new ArrayList<News>();

    public InsertTask(MongoDB mongodb, QueueBucket queues) {
    	this.mongodb = mongodb;
        this.queues = queues;
    }

    public void run() {
        Queue<News> parserQueue = queues.get("parserQueue");
        while (true) {
        	if(parserQueue.size() > 0) {
        		mongodb.save(parserQueue.poll(), Constants.newsCollectionName);
        		/*synchronized (list) {
        			list.add(parserQueue.poll());
        			if (list.size() == 100) {
        				logger.info("insert into db, size: " + list.size());
        				mongodb.insert(list, Constants.newsCollectionName);
        				list.clear();
        			}
        		}*/
        	}
        	else {
        		try {
	            	logger.info("sleep 10s...");
	                Thread.sleep(10 * 1000);   //等待爬虫一分钟
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
        	}
		}
    }
}