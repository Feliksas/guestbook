package exceptions;

public class AccountExistsException extends Exception {
    public AccountExistsException(String errorMessage) {
        super(errorMessage);
    }
}
