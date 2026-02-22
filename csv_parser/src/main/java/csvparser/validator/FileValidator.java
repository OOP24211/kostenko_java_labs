package csvparser.validator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileValidator {

    public ValidationResult validate(String inputFile, String outputFile) {
        ValidationResult result = new ValidationResult();

        checkInput(inputFile, result);

        if (!result.hasErrors()) {
            checkOutput(outputFile, result);
        }

        return result;
    }

    private void checkInput(String inputFile, ValidationResult result) {
        Path path = Paths.get(inputFile);

        if (!Files.exists(path)) {
            result.addError("Input file not found: " + inputFile);
            return;
        }

        if (!Files.isReadable(path)) {
            result.addError("Cannot read input file: " + inputFile);
        }

        try {
            if (Files.size(path) == 0) {
                result.addWarning("Input file is empty: " + inputFile);
            }
        } catch (Exception e) {
            result.addError("Error reading input file: " + e.getMessage());
        }
    }

    private void checkOutput(String outputFile, ValidationResult result) {
        File file = new File(outputFile);
        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            result.addError("Output directory does not exist: " + parent);
            return;
        }

        if (file.exists()) {
            if (!file.canWrite()) {
                result.addError("Cannot write to output file: " + outputFile);
            } else {
                result.addWarning("Output file will be overwritten: " + outputFile);
            }
        }
    }
}