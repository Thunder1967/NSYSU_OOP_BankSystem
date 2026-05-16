package nsysu.bank.account;

public interface Transactable {
    boolean withdraw(double amount);
    boolean deposit(double amount);
}
