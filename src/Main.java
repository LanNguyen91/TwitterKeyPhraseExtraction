import java.text.NumberFormat;
import java.util.*;

import twitter4j.conf.ConfigurationBuilder;

//////////////////////////////////////////////////////////////
//	Lan Nguyen, Gordon Cheng, Alex							//
//															//
//	Class : Computer Science								//
//	Course: CS 517											//
//	Subject: Natural Languague Processing					//
//															//
//	Topic : Keyphrase extraction for Twitter				//
//															//
//////////////////////////////////////////////////////////////

public class Main {
	static final String ACCESS_KEY = "2163998690-28tx0JdKCJynvlL20Tkmd9TopMOGd0HpJPwxaD4";
	static final String ACCESS_SECRET ="P2kChTQFUunhqCigQT0OjjW6AGSP1T46MYyuT6MWqWNg5";
	static final String CONSUMER_KEY = "gJsuxchYzgRFRcPGwDNaQcwmW";
	static final String CONSUMER_SECRET="xSAQNBycKbm8d9ZtWplZfvV6pB5QdXIR2j0dr8QDRewnCd4xJJ";
	
	public static void main(String [] agrs)	
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	            .setOAuthConsumerKey(CONSUMER_KEY)
	            .setOAuthConsumerSecret(CONSUMER_SECRET)
	            .setOAuthAccessToken(ACCESS_KEY)
	            .setOAuthAccessTokenSecret(ACCESS_SECRET);
	    
	    /**DataIO dio = new DataIO(cb);
	    
	    dio.getTweets("Google", 50);
	    dio.saveCurrentTweets("GoogleTweets.sav");
	    dio.print();*/
	    
	    DataProcessor dp = new DataProcessor("YOLOTweets.sav");
	    
	    dp.buildUnigramAndIndexMap();
	    //dp.printUnigram();
	    
	    System.out.println("Most common word: " + dp.getMostCommonWord());
	}
	
	public static void benchmark() {
	    Runtime runtime = Runtime.getRuntime();
	    NumberFormat format = NumberFormat.getInstance();
	    
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();

	    System.out.println("");
	    System.out.println("free memory: " + format.format(freeMemory / 1024) + " KB");
	    System.out.println("allocated memory: " + format.format(allocatedMemory / 1024) + " KB");
	    System.out.println("max memory: " + format.format(maxMemory / 1024) + " KB");
	    System.out.println("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + " KB");
	}
}
