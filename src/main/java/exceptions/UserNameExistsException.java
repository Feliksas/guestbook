package exceptions;

public class UserNameExistsException extends AccountExistsException {
    public UserNameExistsException(String errorMessage) {
        super(errorMessage);
    }
}
