package csvparser;

import java.util.Map;
import java.util.List;

public class TextProcessor {
    public static void process(String InputFile, String OutputFile){
        String text = FileReaderUtil.readFile( InputFile);
        //Мапа: Слово - частота
        Map<String,Integer> freq = WordCounter.countWords(text);

        //Отсортированный по убыванию список пар: (слова,частота)
        List<Map.Entry<String,Integer>> list = Sort.sortMap(freq);
        System.out.println(list);

        CSVWriter.writeCSV(outputFile, list);

    }

}
