package nsysu.util.exception;

public class ClosedUserException extends RuntimeException {
  public ClosedUserException(String message) {
    super(message);
  }
  public ClosedUserException() {
    super("This user has been closed from this bank system");
  }
}
