package gr.aueb.cf.booklibapp.service.exceptions;

public class AuthenticationFailedException extends RuntimeException {
    private static final long serialVersionUID = 2345678L;

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
