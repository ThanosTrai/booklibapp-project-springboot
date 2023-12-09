package gr.aueb.cf.booklibapp.service.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 987654321L;

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
