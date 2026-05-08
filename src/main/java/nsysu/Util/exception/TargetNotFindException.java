package nsysu.Util.exception;

public class TargetNotFindException extends RuntimeException {
    public TargetNotFindException(String message) {
        super(message);
    }
    public TargetNotFindException() {
        super("Target Not Find");
    }
}
