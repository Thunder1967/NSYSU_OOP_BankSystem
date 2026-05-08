package nsysu.Util.exception;

public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(String message) {
        super(message);
    }
    public NegativeBalanceException() {
        super("Can Not Allow Negative Balance");
    }
}
