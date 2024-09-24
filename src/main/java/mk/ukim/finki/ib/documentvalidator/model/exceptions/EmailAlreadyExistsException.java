package mk.ukim.finki.ib.documentvalidator.model.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("User with the email "+email+" already exists.");
    }
}
