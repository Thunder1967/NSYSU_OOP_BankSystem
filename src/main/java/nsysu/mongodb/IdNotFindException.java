package nsysu.mongodb;

public class IdNotFindException extends RuntimeException {
  public IdNotFindException(String message) {
    super(message);
  }
  public IdNotFindException() {
    super("Account id not find");
  }
}
