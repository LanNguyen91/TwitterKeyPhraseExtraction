/**
 * This class is responsible for taking a list of Tweets and processing it to
 * extract key phrases. This class can also be saved so we can load processed
 * data and perform more work on them in needed in the future.
 * 
 * @author Gordon Cheng
 */
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;

public class DataProcessor implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<String, Integer> unigram, indexMap;
    private List<Status> tweetsList;
    
    private int[][] bigram;
    
    public static final String wordRegex = "[#@]?[\\w\\d']{3,}";
    
    /**
     * Simply creates a blank Data Processor object
     */
    public DataProcessor() {
        String match;
        String testString = "RT @TaylorBalfour: I'm just not gonna sleep for two days #yolo";
        Matcher matcher = Pattern.compile(wordRegex).matcher(testString);
        
        while (matcher.find()) {
            match = matcher.group();
            
            if (match.charAt(0) == '@' || match.charAt(0) == '#')
                continue;
            else
                System.out.println("Found: " + match);
        }
    }
    
    /**
     * Initializes this DataProcessing object using a saved list of Tweets
     * 
     * @param tweetsFile - Name of the file to fetch the tweets from
     */
    public DataProcessor(String tweetsFile) {
        tweetsList = DataIO.loadTweets(tweetsFile);
    }
    
    /**
     * Returns whether or not a word is a valid match. I'm mostly using this
     * function to remove hashtagged words and @ replies that I haven't weeded
     * out using a proper regular expression. That also includes RT retweet
     * lables, we won't need those when extracting key phrases
     * 
     * @param match - String of the word to verify
     * @return TRUE if the word is a valid key phrase word; FALSE otherwise
     */
    public boolean isValidWord(String match) {
        return (match.charAt(0) != '@' && match.charAt(0) != '#') &&
                !match.equalsIgnoreCase("rt") &&
                !match.equalsIgnoreCase("http");
    }
    
    /**
     * Returns the most common word used in all the tweets based off the
     * unigram counts
     * 
     * @return Word most commonly used in the tweet list
     */
    public String getMostCommonWord() {
        if (unigram == null) {
            System.err.println("ERROR: Unigram is currently null");
            return "";
        }
        
        int frequency = 0;
        Entry pair;
        String mostCommon = "";
        
        Iterator it = unigram.entrySet().iterator();
        
        while (it.hasNext()) {
            pair = (Entry)it.next();
            
            if ((int)pair.getValue() > frequency) {
                frequency = (int)pair.getValue();
                mostCommon = (String)pair.getKey();
                System.out.println("");
                System.out.println("New Most Common Frequency: " + frequency);
                System.out.println("New Most Common: " + mostCommon);
            }
        }
        
        return mostCommon;
    }
    
    /**
     * Builds a unigram and an index map, used for bigram access and building
     * with our current list of tweets loaded
     */
    public void buildUnigramAndIndexMap() {
        if (tweetsList == null) {
            System.err.println("ERROR: Unable to build unigram using a null" +
                "list of Tweets");
            return;
        }
        
        String tweet, word;
        
        unigram = new HashMap<String, Integer>();
        indexMap = new HashMap<String, Integer>();
        
        for (Status s : tweetsList) {
            tweet = s.getText().toLowerCase();
            Matcher matcher = Pattern.compile(wordRegex).matcher(tweet);
            
            while (matcher.find()) {
                word = matcher.group();
                
                if (isValidWord(word)) {
                    if (!indexMap.containsKey(word))
                        indexMap.put(word, indexMap.size());

                    if (unigram.containsKey(word))
                        unigram.put(word, unigram.get(word)+1);
                    else
                        unigram.put(word, 1);
                }
            }
        }
    }
    
    /**
     * Builds a bigram based on the unigram size and all the tweets
     */
    public void buildBigram() {
        if (unigram == null) {
            System.err.println("ERROR: Unable to build bigram using a null" +
                "unigram");
            return;
        }
        
        if (tweetsList == null) {
            System.err.println("ERROR: Unable to build bigram using a null" +
                "list of Tweets");
            return;
        }
        
        String tweet, word;
        String prevWord = "";
        String nextWord = "";
        
        bigram = new int[unigram.size()][unigram.size()];
        
        for (Status s : tweetsList) {
            tweet = s.getText().toLowerCase();
            Matcher matcher = Pattern.compile(wordRegex).matcher(tweet);
            
            while (matcher.find()) {
                word = matcher.group();
                
                if (isValidWord(word)) {
                    nextWord = word;
    
                    if (prevWord.length() > 0 && nextWord.length() > 0)
                        bigram[indexMap.get(prevWord)][indexMap.get(nextWord)]++;
                    
                    prevWord = word;
                }
            }
        }
    }
    
    /**
     * Prints the current unigram, mostly used for debugging
     */
    public void printUnigram() {
        if (unigram == null) {
            System.err.println("ERROR: Unigram is currently null");
            return;
        }
        
        Iterator it = unigram.entrySet().iterator();
        
        while (it.hasNext())
            System.out.println(it.next().toString());
        
        System.out.println("Unigram Size: " + unigram.size());
    }
    
    /**
     * Prints the current index map for our vocabulary, mostly for debugging
     */
    public void printIndexMap() {
        if (indexMap == null) {
            System.err.println("ERROR: Index Map is currently null");
            return;
        }
        
        Iterator it = indexMap.entrySet().iterator();
        
        while (it.hasNext())
            System.out.println(it.next().toString());
        
        System.out.println("Index Map Size:" + indexMap.size());
    }
}
