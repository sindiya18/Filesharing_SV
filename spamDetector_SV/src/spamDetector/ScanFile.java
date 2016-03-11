package spamDetector;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by venujan on 10/03/16.
 */
public class ScanFile {

    private Map<String, Integer> trainFreq;
    private String directoryName;

    public ScanFile(String directoryName, Map <String, Integer> trainFreq){
        this.directoryName=directoryName;
        this.trainFreq=trainFreq;
    }

    public Map <String, Integer> readFiles(File folder, Map<String, Integer> trainFreq){
        Map <String, Integer>freq=new TreeMap<>();
        try{
            String word;
            for (File fileEntry : folder.listFiles()){
                Scanner scan=new Scanner(fileEntry);
                while (scan.hasNext()){
                    word=scan.next();
                    if(isWord(word)){
                        if (!freq.containsKey(word)){
                            freq.put(word,1);
                        } else{
                            freq.put(word,freq.get(word)+1);
                        }
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return freq;
    }

    private boolean isWord(String str){
        String pattern = "^[a-zA-Z]*$";
        if (str.matches(pattern)){
            return true;
        }
        return false;
    }

    public Map <String, Double> getProbabilities(Map<String, Integer> map,int fileCount){
        Map <String, Double> probabilities = new TreeMap<>();
        Set<String> keys=map.keySet();
        Iterator<String> keyIterator=keys.iterator();
        String word;
        while (keyIterator.hasNext()){
            word=keyIterator.next();
            probabilities.put(word,map.get(word)/(double) fileCount);
        }
        return probabilities;
    }
}
