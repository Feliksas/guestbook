package exceptions;

public class EmailExistsException extends AccountExistsException {
    public EmailExistsException(String errorMessage) {
        super(errorMessage);
    }
}
