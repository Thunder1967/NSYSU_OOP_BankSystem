package nsysu.util.exception;

public class SameUserNameException extends RuntimeException {
    public SameUserNameException(String message) {
        super(message);
    }
    public SameUserNameException() {
        super("Can Not Allow Same Name");
    }
}
