package mk.ukim.finki.ib.documentvalidator.model.exceptions;

public class PasswordsDoNotMatchException extends RuntimeException {
    public PasswordsDoNotMatchException() {
        super("The two passwords do not match.");
    }
}
