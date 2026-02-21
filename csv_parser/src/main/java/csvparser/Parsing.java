package csvparser;

public class Parsing {
    public static String[] parse(String text){
        return text.split("[^A-Za-zА-Яа-я]+");
    }
}
