package exceptions;

public class AccountExistsException extends Exception {
    AccountExistsException(String errorMessage) {
        super(errorMessage);
    }
}
