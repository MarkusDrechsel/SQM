package at.ac.tuwien.inso.sqm.exception;

public class ActionNotAllowedException extends RuntimeException {

    public ActionNotAllowedException() {
        super();
    }

    public ActionNotAllowedException(String s) {
        super(s);
    }
}
