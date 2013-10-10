package com.ml.util;

public class Constants {
	
	public static final String separator = "/";
	public static String currentDir = Constants.class.getResource("/").getPath();
	
	public static final String defaultConfigFile = currentDir + separator + "default.properties";

	public static final String newsCollectionName = "news";
	public static final String crawlPatternCollectionName = "crawlPattern";

	public static final String parserQueueName = "parserQueue";



}
