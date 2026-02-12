package csvparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sort {

    public static List<Map.Entry<String, Integer>> sortMap(Map<String, Integer>freq){

        Set<Map.Entry<String,Integer>> set_word = freq.entrySet();
        List<Map.Entry<String,Integer>> list = new ArrayList<>(set_word);
        list.sort(new WordCountComparator());
        return list;
    }

}
