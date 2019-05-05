package at.ac.tuwien.inso.sqm.exception;


public class UserFacingException extends RuntimeException {

    public UserFacingException() {
        super();
    }

    public UserFacingException(String s) {
        super(s);
    }
}
