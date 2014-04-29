import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Paging;
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
	
	//print out the numberOfTweets that are downloaded
	public void print(){
		for(Status s : statusList)
		{
			System.out.println();
            System.out.println(s.getUser().getName() + ":" +
                               s.getText());
		}
	}
}
