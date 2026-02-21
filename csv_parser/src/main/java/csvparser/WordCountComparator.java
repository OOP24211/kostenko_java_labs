package csvparser;

import java.util.Comparator;
import java.util.Map;

public class WordCountComparator
    implements Comparator<Map.Entry<String,Integer>>{
    @Override
    public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
        return Integer.compare(b.getValue(), a.getValue());
    }
}
