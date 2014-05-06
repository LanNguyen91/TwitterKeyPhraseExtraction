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
		int tweetCount;
		String searchTerm;
		Scanner sc = new Scanner(System.in);
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	            .setOAuthConsumerKey(CONSUMER_KEY)
	            .setOAuthConsumerSecret(CONSUMER_SECRET)
	            .setOAuthAccessToken(ACCESS_KEY)
	            .setOAuthAccessTokenSecret(ACCESS_SECRET);

	    DataIO io = new DataIO(cb);
	    
	    System.out.print("Enter Search Term: ");
	    searchTerm = sc.nextLine();
	    
	    System.out.print("Enter the number of Tweets retrieved: ");
	    tweetCount = sc.nextInt();
	    
	    //io.getTweets(searchTerm, tweetCount);
	    io.getTweetsBySearch(searchTerm, tweetCount);
	    io.printTweetCount();
	    
	    // get latest 5 tweets 
	    //io.getTweets(5);
	    System.out.println("==================Sample Output=====================");
	    io.print();
	    
	    sc.close();
	}
}
