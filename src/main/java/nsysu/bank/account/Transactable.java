package nsysu.bank.account;

public interface Transactable {
    public abstract boolean withdraw(double amount);
    public abstract boolean deposit(double amount);
}
