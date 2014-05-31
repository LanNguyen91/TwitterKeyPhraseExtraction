/**
 * This class is responsible for taking a list of Tweets and processing it to
 * extract key phrases. This class can also be saved so we can load processed
 * data and perform more work on them in needed in the future.
 * 
 * @author Gordon Cheng
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;

public class DataProcessor implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<String, Integer> unigram, indexMap;
    private List<Status> tweetsList;
    private List<String> backgroundWordList;
    
    private int[][] bigram;
    
    public static final String backWordFile = "backgroundwords.txt";
    public static final String wordRegex = "[#@]?[\\w\\d':/.]{3,}";
    //public static final String wordRegex = "[#@]?[\\w\\d']{3,}";
    
    /**
     * Simply creates a blank Data Processor object
     */
    public DataProcessor() {
    	loadBackgroundWords();
    }
    
    /**
     * Initializes this DataProcessing object using a saved list of Tweets
     * 
     * @param tweetsFile - Name of the file to fetch the tweets from
     */
    public DataProcessor(String tweetsFile) {
    	loadBackgroundWords();
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
        return (match.charAt(0) != '@' && match.charAt(0) != '#' &&
        		!isBackgroundWord(match) && !isLink(match));
    }
    
    /**
     * Returns whether or not a certain word is background word and therefore
     * should be ignored
     * 
     * @param word - String of the word to verify
     * @return TRUE if the word is a background word; FALSE otherwise
     */
    public boolean isBackgroundWord(String word) {
    	if (backgroundWordList == null) {
    		System.err.println("ERROR: Background word list has not been" +
    	        "initialized yet");
    		return true;
    	}
    	
    	return backgroundWordList.contains(word);
    }
    
    /**
     * Returns whether or not a word is an internet link
     * 
     * @param word - String of the word to verify
     * @return TRUE if the given word is an internet link; FALSE otherwise
     */
    public boolean isLink(String word) {
        return (word.length() < 4) ? false :
            word.substring(0, 4).equalsIgnoreCase("http");
    }
    
    /**
     * Since the HashMap does not support bidirectional movement, Key to Value
     * and Value to Key, this method simply returns the key using a given
     * value from the indexMap
     * 
     * @param index - The index assigned to a word
     * @return The word that's been assigned to the given index
     */
    public String getWordFromIndex(int index) {
        if (indexMap == null) {
            System.err.println("ERROR: indexMap is currently null");
            return "";
        }
        
        Entry pair;
        Iterator it = indexMap.entrySet().iterator();
        
        while (it.hasNext()) {
            pair = (Entry)it.next();
            
            if ((int)pair.getValue() == index)
                return (String)pair.getKey();
        }

        return "";
    }
    
    /**
     * Returns the most common word used in all the tweets based off the
     * unigram counts
     * 
     * @param count - number of most used words
     * @return List of the most commonly used words
     */
    public List<String> getMostCommonWord(int count) {
        if (unigram == null) {
            System.err.println("ERROR: Unigram is currently null");
            return null;
        }
        
        System.out.println("Fetching most commonly used words...");
        
        int frequency = 0;
        int upperLimit = Integer.MAX_VALUE;
        Entry pair;
        String mostCommon = "";
        
        List<String> mostCommonWords = new ArrayList<String>();
        
        for (int i=0;i<count;i++) {
        	frequency = 0;
        	mostCommon = "";
	        Iterator it = unigram.entrySet().iterator();
	        
	        while (it.hasNext()) {
	            pair = (Entry)it.next();
	            
	            if ((int)pair.getValue() > frequency &&
	            	(int)pair.getValue() < upperLimit) {
	                frequency = (int)pair.getValue();
	                mostCommon = (String)pair.getKey();
	            }
	        }
	        
	        mostCommonWords.add(mostCommon);
	        upperLimit = frequency;
	        
	        //System.out.println("Common word: " + mostCommon);
	        //System.out.println("Frequency: " + frequency);
	        //System.out.println();
        }
        
        System.out.println("Fetched most commonly used words");
        
        return mostCommonWords;
    }
    
    /**
     * Returns the key phrases most commonly used
     * 
     * @param count - the number of key phrases to extract from the Tweets
     * @return List of keyphrases
     */
    public List<String> getKeyPhrases(int count) {
        if (indexMap == null) {
            System.err.println("ERROR: Index Map has not yet been " +
                "initialized");
            return null;
        }
        
    	if (bigram == null) {
    		System.err.println("ERROR: Bigram has not yet been initialized");
    		return null;
    	}
    	
    	System.out.println("Extracting key phrases...");
    	
    	int baseWordIndex;
    	int prevHighest, nextHighest;
    	int prevIndex, nextIndex;
    	String keyPhrase = "";
    	
    	List<String> mostCommonlyUsedWords = getMostCommonWord(count);
    	List<String> keyPhrases = new ArrayList<String>();
    	
    	for (String s : mostCommonlyUsedWords) {
    	    baseWordIndex = indexMap.get(s);
    	    keyPhrase = "";
    	    
    	    prevHighest = 0;
    	    prevIndex = -1;
    	    
        	for (int i=0;i<bigram[0].length;i++) {
        		if (bigram[i][baseWordIndex] > prevHighest) {
        		    prevHighest = bigram[i][baseWordIndex];
        		    prevIndex = i;
        		}
        	}
        	
        	nextHighest = 0;
        	nextIndex = -1;
    
        	for (int i=0;i<bigram[0].length;i++) {
        	    if (bigram[baseWordIndex][i] > nextHighest) {
                    prevHighest = bigram[baseWordIndex][i];
                    nextIndex = i;
        	    }
        	}
        	
        	if (prevIndex > -1)
        	    keyPhrase = keyPhrase + getWordFromIndex(prevIndex) + " ";
        	
        	keyPhrase = keyPhrase + s;
        	
        	if (nextIndex > -1)
        	    keyPhrase = keyPhrase + " " + getWordFromIndex(nextIndex);
        	
        	keyPhrases.add(keyPhrase);
    	}
    	
    	System.out.println("Key phrase extraction complete");
    	
    	return keyPhrases;
    }
    
    /**
     * Loads a list of background words from a file. These words will be
     * ignored when building the unigram and bigram
     */
    public void loadBackgroundWords() {
    	Scanner sc;
    	File file = new File(DataIO.DATADIR + backWordFile);
    	
    	System.out.println("Loading background words...");
    	
    	try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: " + backWordFile + " not found.");
			return;
		}
    	
    	if (sc != null) {
    	    backgroundWordList = new ArrayList<String>();
    	    
    	    while (sc.hasNext())
    	    	backgroundWordList.add(sc.nextLine());
    	}
    	
    	sc.close();
    	
    	System.out.println("Finished loading background words");
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
        
        System.out.println("Building Unigram and Index Map...");
        
        String tweet, word;
        
        unigram = new HashMap<String, Integer>();
        indexMap = new HashMap<String, Integer>();
        
        for (Status s : tweetsList) {
            tweet = s.getText().toLowerCase();
            Matcher matcher = Pattern.compile(wordRegex).matcher(tweet);
            
            while (matcher.find()) {
                word = matcher.group();
                
                if (isValidWord(word)) {
                    word = word.replaceAll("\\.", "");
                    
                    if (!indexMap.containsKey(word))
                        indexMap.put(word, indexMap.size());

                    if (unigram.containsKey(word))
                        unigram.put(word, unigram.get(word)+1);
                    else
                        unigram.put(word, 1);
                }
            }
        }
        
        System.out.println("Unigram and Index map build complete");
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
        
        System.out.println("Building Bigram...");
        
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
                    nextWord = word.replaceAll("\\.", "");
    
                    if (indexMap.containsKey(prevWord) && indexMap.containsKey(nextWord))
                        bigram[indexMap.get(prevWord)][indexMap.get(nextWord)]++;
                    
                    prevWord = word;
                }
            }
        }
        
        System.out.println("Bigram build Complete");
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
