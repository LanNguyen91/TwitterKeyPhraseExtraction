import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;


/*
 * This class will be use to handling data from Twitter
 * What we do: We will deal mainly with input from twitter.
 * What we don't: We will not twitting or post anything on twitter
 * 
 */
public class DataIO {
	private TwitterFactory twitterFactory;
    private Twitter twitter;
    private List<Status> statusList;
    
    //constructor will create a DataIO with given configuration
    //configuration that passes in contained user name, password of twitter developer
	public DataIO(ConfigurationBuilder cb){
		try{
			twitterFactory = new TwitterFactory(cb.build());
			twitter = twitterFactory.getInstance();
			statusList = null;
		}catch (Exception te) {
            te.printStackTrace();
            System.out.println("Failed to get account settings: " + te.getMessage());
            System.exit(-1);
		}
	}
	
	//return number of tweet according to the current user requesting
	public List<Status> getTweets(int amount){
		try {
			statusList = twitter.getHomeTimeline(new Paging(1, amount));
		} catch (TwitterException e) {
			System.out.println("Twitter could not download tweets");
			e.printStackTrace();
		}
		return statusList;
	}
	
	/**
	 * Retrieves tweets from a specified user
	 * @param user - Username of the profile you want to retrieve from
	 * @param amount - Number of tweets that you want to fetch
	 * @return List of tweets
	 */
	public List<Status> getTweets(String user, int amount) {
		try {
		    statusList = twitter.getUserTimeline(user, new Paging(1, amount));
		}
		catch (TwitterException e) {
			System.err.println("Twitter could not download tweets for user: \"" + user + "\"");
			e.printStackTrace();
		}
		
		return statusList;
	}

	/**
	 * Given a specific search term, such as a hastag or phrase
	 * 
	 * For more information on search strings and phrases check this page:
	 *     https://dev.twitter.com/docs/using-search
	 * 
	 * @param search - Basically a string search term used to find tweets
	 * @param amount - Number of Tweets you want to fetch from the search
	 * @return
	 */
	public List<Status> getTweetsBySearch(String search, int amount) {
		try {
			Query query = new Query(search);
            QueryResult result;
            
            query.setCount(amount);
            
            result = twitter.search(query);
            statusList = result.getTweets();
		}
		catch (TwitterException e) {
			System.err.println("Twitter could not download tweets for search: \"" + search + "\"");
			e.printStackTrace();
		}
		return statusList;
	}
	
	//print out the numberOfTweets that are downloaded
	public void print(){
		for(Status s : statusList)
		{
			System.out.println();
            System.out.println("@" + s.getUser().getScreenName() + ": " +
                               s.getText());
		}
	}
	
	/**
	 * Simply prints the number of tweets that have currently been retrieved
	 */
	public void printTweetCount() {
		System.out.println("Tweet(s): " + statusList.size());
	}
}
