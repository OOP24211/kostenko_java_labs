package csvparser;

import java.util.Comparator;
import java.util.Map;

public class WordCountComparator
    implements Comparator<Map.Entry<String,Integer>>{
    @Override
    public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
        return b.getValue() - a.getValue();
    }
}
