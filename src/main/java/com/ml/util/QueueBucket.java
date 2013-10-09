package com.ml.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * This class contains a set of Queues. It provides methods to serialize its
 * state to/from disk, as well as creating, removing and listing what queues it
 * manages.
 * 
 */
public class QueueBucket {
	private final HashMap<String, Queue> queues = new HashMap<String, Queue>();

	/**
	 * Fetch a queue.
	 * 
	 * @param name
	 *            String containing the name of the queue
	 * @return Queue if found, null if not
	 */
	public Queue get(String name) {
		synchronized (queues) {
			return queues.get(name);
		}
	}

	/**
	 * Add a queue.
	 * 
	 * @param name
	 *            String containing the name of the queue
	 * @param c
	 *            Class containing the type of the queue
	 * @return Queue if successfully created
	 * @throws java.lang.IllegalArgumentException
	 *             on unknown type or duped name
	 * @throws java.lang.InstantiationException
	 *             from Class.newInstance()
	 * @throws java.lang.IllegalAccessException
	 *             from Class.newInstance()
	 */
	public Queue add(String name, Class c) throws InstantiationException, IllegalAccessException {
		
		Queue queue = (Queue) c.newInstance();

		synchronized (queues) {
			if (queues.get(name) == null) {
				queues.put(name, queue);
			}
		}

		return queue;
	}
	
	/**
	 * Remove a queue.
	 * 
	 * @param path
	 *            String containing the name of the queue
	 * @return Queue if found
	 * @throws java.lang.IllegalArgumentException
	 *             if queue doesn't exist
	 */
	public Queue remove(String path) throws IllegalArgumentException {
		Queue queue;

		synchronized (queues) {
			queue = queues.remove(path);
			if (queue == null) {
				throw new IllegalArgumentException("Queue doesn't exist");
			}
		}

		return queue;
	}

	/**
	 * Get list of queue names.
	 * 
	 * @return String[] containing all the queue names (including SYSQ)
	 */
	public String[] list() {
		synchronized (queues) {
			return queues.keySet().toArray(new String[0]); // Yuck!
		}
	}

}