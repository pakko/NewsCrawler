package com.ml.qevent;

import java.util.concurrent.ExecutorService;

import com.ml.db.MongoDB;
import com.ml.task.InsertTask;
import com.ml.util.QueueBucket;

public class InsertListener implements QueueListener {

	private MongoDB mongodb;
	private QueueBucket queues;
	private ExecutorService service;
	
	public InsertListener(MongoDB mongodb, QueueBucket queues,
			ExecutorService service) {
		this.mongodb = mongodb;
		this.queues = queues;
		this.service = service;
	}

	@Override
	public void queueEvent(QueueEvent event) {
		if (event.getQueueState() != null && event.getQueueState().equals("take_parser")) {
			InsertTask it = new InsertTask(mongodb, queues);
			service.execute(it);
        }
	}

}
