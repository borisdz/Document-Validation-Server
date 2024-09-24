package mk.ukim.finki.ib.documentvalidator.model.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("User with the username "+username+" already exists.");
    }
}
