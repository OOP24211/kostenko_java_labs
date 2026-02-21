package csvparser.validator;

import java.util.List;
import java.util.ArrayList;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public void addError(String message){
        errors.add("ERROR" + message);
    }
    public void addWarning(String message){
        warnings.add("WARNING" + message);
    }

    public boolean hasError(){
        return !errors.isEmpty();
    }

    public boolean hasWarning(){
        return !warnings.isEmpty();
    }

    public void printReport(){
        if(hasError()){
            System.out.println("ERROR: ");
            for (String error : errors) {
                System.out.println(" " + error);
            }
        }
        if (hasWarning()){
            System.out.println("WARNING: ");
            for (String warning : warnings) {
                System.out.println("" + warning);
            }
        }


    }
}
