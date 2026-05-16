package nsysu.bank.account;

import nsysu.util.sqlaccess.AccountData;

public interface Transactable {
    boolean withdraw(double amount);
    boolean deposit(double amount);
}
