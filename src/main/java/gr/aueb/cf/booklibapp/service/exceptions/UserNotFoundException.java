package gr.aueb.cf.booklibapp.service.exceptions;

public class UserNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 123456789L;

    public UserNotFoundException(String message) {
        super(message);
    }
}
