package gr.aueb.cf.booklibapp.service.exceptions;

public class PasswordMismatchException extends RuntimeException {
    private static final long serialVersionUID = 1234567L;

    public PasswordMismatchException(String message) {
        super(message);
    }
}
