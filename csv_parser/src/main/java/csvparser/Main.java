package csvparser;

import csvparser.validator.FileValidator;
import csvparser.validator.ValidationResult;

public class Main {
    public static void main(String[] args) {

        String inputFile;
        String outputFile;

        if (args.length == 2) {
            inputFile = args[0];
            outputFile = args[1];
        } else {
            inputFile = "src/test/resources/text_example/test.txt";
            outputFile = "result.csv";
        }

        FileValidator validator = new FileValidator();
        ValidationResult check = validator.validate(inputFile, outputFile);
        check.printReport();
        if (!check.hasError()) {
            TextProcessor.process(inputFile, outputFile);
        }
    }
}
