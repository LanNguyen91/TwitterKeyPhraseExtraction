import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.GeoLocation;
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
    
    public static final String DATADIR = "data/";
    public static final String TRAINDATADIR = "testdocs/en/";
    // Coordinates for Los Angeles
    public static final double LA_LAT = 34.052;
    public static final double LA_LON = -118.243;
    
    // Coordinates for Las Vegas
    public static final double LV_LAT = 36.169;
    public static final double LV_LON = -115.139;
    
    // Coordinates for Guangzhou, China
    public static final double GZ_LAT = 23.129;
    public static final double GZ_LON = 113.264;
    
    // Two forms of measuring distance
    public static final String MILES = "mi";
    public static final String KILOMETERS = "km";
    
    /**
     * Temporary default constructor used to test the tweet saving system
     */
    /**public DataIO() {
        // The constructor... IT DOES NOTHING!
    }*/
    
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
			System.err.println("Twitter could not download tweets for user: \""
		        + user + "\"");
			e.printStackTrace();
		}
		
		return statusList;
	}

	/**
	 * Given a specific search term, such as a hastag or phrase, return Tweets
	 * with the given phrase or patterns
	 * 
	 * Update 5/18/2014
	 *     Updated it so we can fetch more than 100 tweets at a time
	 * 
	 * For more information on search strings and phrases check this page:
	 *     https://dev.twitter.com/docs/using-search
	 * 
	 * @param search - Basically a string search term used to find tweets
	 * @param amount - Number of Tweets you want to fetch from the search
	 * @return List of Tweets
	 */
	public List<Status> getTweetsBySearch(String search, int amount) {
	    long lastID = Long.MAX_VALUE;
	    Query query = new Query(search);
	    statusList = new ArrayList<Status>();
	    
	    System.out.println("Fetching Tweets by search term: \"" +
	        search + "\"");
	    
	    while (statusList.size () < amount) {
	        if (amount - statusList.size() > 100)
	            query.setCount(100);
	        else
	            query.setCount(amount - statusList.size());

	        try {
	            QueryResult result = twitter.search(query);
	            statusList.addAll(result.getTweets());
	            
	            System.out.println("Currently fetched " + statusList.size() +
	                " Tweets");
	            
	            for (Status t: statusList)
	                if (t.getId() < lastID)
	                    lastID = t.getId();

	        }
	        catch (TwitterException e) {
	            System.err.println("Failed to download tweets for search: \"" +
	                search + "\"");
	            e.printStackTrace();
	            return statusList;
	        }

	        query.setMaxId(lastID - 1);
	    }
	    System.out.println("Finish Fetching");
		return statusList;
	}
	
	/**
	 * Returns the tweets made in a certain radius around a certain location
	 * 
	 * @param lat - Latitude in degrees of the location
	 * @param lon - Longitude in degrees of the location
	 * @param radius - Radius around that location to fetch the tweets
	 * @param units - Distance units that the radius is measured in
	 * @param amount - Number of tweets to fetch from this location
	 * @return List of Tweets
	 */
	public List<Status> getTweetsByLocation(double lat, double lon,
	                                        double radius, int amount,
	                                        String units) {
	    long lastID = Long.MAX_VALUE;
	    Query query = new Query();
	    statusList = new ArrayList<Status>();

	    GeoLocation geoLoc = new GeoLocation(lat, lon);
	    query.geoCode(geoLoc, radius, units);

	    while (statusList.size () < amount) {
            if (amount - statusList.size() > 100)
                query.setCount(100);
            else
                query.setCount(amount - statusList.size());

            try {
                QueryResult result = twitter.search(query);
                statusList.addAll(result.getTweets());
                
                for (Status t: statusList)
                    if (t.getId() < lastID)
                        lastID = t.getId();

            }
            catch (TwitterException e) {
                System.err.println("Failed to download tweets from this " +
                    "location");
                e.printStackTrace();
            }

            query.setMaxId(lastID - 1);
        }

        return statusList;
	}
	
	/**
	 * Saves the current list of Tweets (statusList) to a file
	 * 
	 * @param fileName - Name of the file to save the Tweets to
	 */
	public void saveCurrentTweetsToFile(String fileName) {
	    DataIO.saveTweetsToFile(statusList, fileName);
	}
	
	/**
	 * Saves the current list of Tweets (statusList) to a file
	 * 
	 * @param fileName - Name of the file to save the Tweets to
	 */
	public void saveCurrentTweets(String fileName) {
	    DataIO.saveTweets(statusList, fileName);
	}
	
	/**
	 * Loads tweets from a specified file and sets it to the statusList
	 * 
	 * @param fileName - Name of the file to load data from
	 */
	public void loadSavedTweets(String fileName) {
	    statusList = DataIO.loadTweets(fileName);
	}
	
	/**
	 * Saves the given list of Tweets to a file which can then be loaded and
	 * read by another object in the program
	 * 
	 * @param fileName - Name of the file to save the Tweets to
	 */
	public static void saveTweetsToFile(List<Status> tweetsList, String fileName) {
	    // Ensure we always have the data folder to write files to
	    new File(TRAINDATADIR).mkdir();
	    
	    try {
	    	File result = new File(TRAINDATADIR+fileName);
	    	FileWriter fw = new FileWriter(result);
	    	BufferedWriter bw = new BufferedWriter(fw);
            for(Status s : tweetsList)
            {
            	bw.write(s.getText());
            	bw.write("\n");
            }
        } 
        catch (IOException e) {
            System.err.println("ERROR: Problem occured while saving file");
            e.printStackTrace();
        }   
	}
	
	/**
	 * Saves the given list of Tweets to a file which can then be loaded and
	 * read by another object in the program
	 * 
	 * @param fileName - Name of the file to save the Tweets to
	 */
	public static void saveTweets(List<Status> tweetsList, String fileName) {
	    // Ensure we always have the data folder to write files to
	    new File(DATADIR).mkdir();
	    
	    try {
            FileOutputStream fout = new FileOutputStream(DATADIR + fileName);
	        ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(tweetsList);
            out.close();
        } 
        catch (IOException e) {
            System.err.println("ERROR: Problem occured while saving file");
            e.printStackTrace();
        }   
	}
	
	/**
	 * Loads a list of Tweets saved in a specified file
	 * 
	 * @param fileName - Name of the file to load the Tweets from
	 * @return List of tweets to load from a file
	 */
	public static List<Status> loadTweets(String fileName) {
	    try {
            FileInputStream in = new FileInputStream(DATADIR + fileName);
            ObjectInputStream objIn = new ObjectInputStream(in);
            List<Status> savedStatusList = (List<Status>)objIn.readObject();
            
            objIn.close();
            
            return savedStatusList;
        }
	    catch (FileNotFoundException e) {
	        System.err.println("ERROR: File not found");
	        return null;
	    }
        catch (ClassNotFoundException e) {
            System.err.println("ERROR: Failed to find the class of the " +
                "object serialized in the file file");
            return null;
        }
	    catch (IOException e) {
            System.err.println("ERROR: Problem occured while reading file");
            e.printStackTrace();
            return null;
        }
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
