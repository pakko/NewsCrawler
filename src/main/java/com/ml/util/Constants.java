package com.ml.util;


public class Constants {
	
	public static final String separator = "/";
	public static String currentDir = Constants.class.getClassLoader().getResource("").getPath();
	
	public static final String defaultConfigFile = currentDir + separator + "default.properties";
	public static final String defaultLogConfigFile = currentDir + separator + "log4j.properties";

	
	public static final String newsCollectionName = "news";
	public static final String crawlPatternCollectionName = "crawl_pattern";
	public static final String categoryCollectionName = "category";
	public static final String clusterCollectionName = "cluster";

	public static final int CPU_NUMBER = Runtime.getRuntime().availableProcessors();
    // default work queue cache size
    public static int maxCacheWork = 300;
    // default add work wait time
    public static long addWorkWaitTime = Long.MAX_VALUE;
    // work thread pool size
    public static int workThreadNum = CPU_NUMBER / 2;
    // callback thread pool size
    public static int callbackThreadNum = CPU_NUMBER / 2;
    //close service wait time
    public static long closeServiceWaitTime = 5 * 60 * 1000;



}
