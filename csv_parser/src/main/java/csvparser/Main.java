package csvparser;

import java.util.Map;
import java.util.List;
public class Main {
    public static void main(String[] args) {

        String inputFile;
        String outputFile;

        if(args.length ==2){
            inputFile = args[0];
            outputFile = args[1];
        }
        else{
            inputFile="src/test/resources/text_example/test.txt";
            outputFile="result.csv";

        }

        String text = FileReaderUtil.readFile( inputFile);
        //System.out.println(text);

        //Мапа: Слово - частота
        Map<String,Integer> freq = WordCounter.countWords(text);
        //System.out.println(freq);

        //Отсортированный по убыванию список пар: (слова,частота)
        List<Map.Entry<String,Integer>> list = Sort.sortMap(freq);
        System.out.println(list);

        CSVWriter.writeCSV(outputFile, list);

    }

}