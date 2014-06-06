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
	    /**
	     * The farmTweets method is used to download Tweets. First it connects
	     * to Twitter's servers and performs a search using the first parameter.
	     * Next we take all the Tweets and put them in a List<Status>. Finally
	     * we serialize or save the List<Status> into a file so we can later
	     * load what we downloaded for easy use.
	     * 
	     * The second parameter dictates the name to give the file and the
	     * third parameter specifies the number of Tweets to download.
	     * 
	     * The maximum number of Tweets allowed in a single go is about
	     * 17985 in either 15 minutes to 1 hour (Twitter wasn't too specific).
	     * 
	     * Also, if you run into an exception while downloading Tweets, the
	     * program will automatically save them up getting said exception, so
	     * nothing is truly lost.
	     * 
	     * Also some topics simply won't yield high Tweet counts, such as
	     * #calpolypomona or #gabrieliglesias for example. If they stay at the
	     * same count for a while, terminate the program to maintain preserve
	     * your rate limit.
	     */
	    //farmTweets("#food", "FoodTweets.sav", 15000);
	    
	    
	    /**
	     * The printTweets method is simply used to print out all the Tweets
	     * in a given list. This includes not only the poster, but the message
	     * of the Tweet itself.
	     * 
	     * We pass it the name and extension of the file to load the
	     * List<Status> object from. The .sav files are located inside the
	     * data subdirectory.
	     */
	    //printTweets("FoodTweets.sav");
	    
	    
	    /**
	     * The extractKeyPhrases method, as the name implies, extracts the key.
	     * Simply give it a list of saved Tweets in the form of a .sav file and
	     * the number of key phrases you want to extract in the second
	     * parameter.
	     * 
	     * Due to the wide variety of topics and different aspects on the topic
	     * 20 or 30 is considered a good amount to help print not only the
	     * noise but the more relevant key phrases as well
	     */
	    //extractKeyPhrases("FoodTweets.sav", 30);
	}
	
	/**
	 * Prints all the tweets saved in a given file
	 * 
	 * @param fileName - Name of the file that contains the Tweets
	 */
	public static void printTweets(String fileName) {
	    ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_KEY)
                .setOAuthAccessTokenSecret(ACCESS_SECRET);
        
	    DataIO dio = new DataIO(cb);
        
        dio.loadSavedTweets(fileName);
        dio.print();
	}
	
	/**
	 * Farms Twitters for Tweets using a given search term. The limit of
	 * transfer is somewhere at 17980 Tweets, so we download them in 15000
	 * Tweet batches.
	 * 
	 * Note: Due to Twitter rate limits, we can only download about 17980
	 * Tweets every 15 minutes, so either wait 15 minutes, or be wary of your
	 * download rate
	 * 
	 * For more information on search strings and phrases check this page:
     *     https://dev.twitter.com/docs/using-search
	 * 
	 * @param searchTerm - Term to search for
	 * @param fileName - Name and extension of the file to save to
	 */
	public static void farmTweets(String searchTerm, String fileName,
	                              int count) {
	    ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_KEY)
                .setOAuthAccessTokenSecret(ACCESS_SECRET);
        
	    DataIO dio = new DataIO(cb);
        
        dio.getTweetsBySearch(searchTerm, count);
        dio.saveCurrentTweets(fileName);
	}
	
	/**
	 * Reads the Tweets file saved by the DataIO class and analyzes the data
	 * to extract the key phrases from them.
	 * 
	 * @param fileName - Name of the file that contains the Tweets
	 * @param count - Number of Tweets to fetch from Twitter
	 */
	public static DataProcessor extractKeyPhrases(String fileName, int count) {
	    DataProcessor dp = new DataProcessor(fileName);
        
        dp.buildUnigramAndIndexMap();
        //dp.buildBigram();
        
        List<String> keyPhrases = dp.getKeyPhrases(count);
        
        System.out.println("");
        
        if (keyPhrases != null)
            for (String s : keyPhrases)
                System.out.println(s);
        else
            System.out.println("ERROR: Unable to produce list of keyphrases");
        
        return dp;
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
