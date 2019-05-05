package at.ac.tuwien.inso.sqm.exception;

public class BusinessObjectNotFoundException extends RuntimeException {

    public BusinessObjectNotFoundException() {
        super();
    }

    public BusinessObjectNotFoundException(String s) {
        super(s);
    }
}
