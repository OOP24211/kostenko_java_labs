package csvparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class FileReaderUtil {
    public static String readFile(String path){
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;

            while((line = br.readLine())!=null){
                result.append(line).append("\n");
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
