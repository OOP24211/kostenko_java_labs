package csvparser;

import java.util.Map;
import java.util.HashMap;

public class WordCounter {

    public static Map<String,Integer> countWords(String text){

        String[] words = Parsing.parse(text);

        Map<String,Integer> freq = new HashMap<>();

        for(String word: words){
            if(word.isEmpty()) continue;

            word = word.toLowerCase();
            if(freq.containsKey(word)){
                freq.put(word, freq.get(word)+1);
            }
            else{
                freq.put(word, 1);
            }
        }

        return freq;
    }
}
