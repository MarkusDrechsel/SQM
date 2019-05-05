package at.ac.tuwien.inso.sqm.exception;

public class ValidationException extends RuntimeException {

    public ValidationException() {
        super();
    }

    public ValidationException(String s) {
        super(s);
    }
}
