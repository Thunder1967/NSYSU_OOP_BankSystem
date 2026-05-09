package nsysu.bank.account;

import nsysu.util.sqlaccess.AccountData;

public interface CanWithdraw {
    boolean withdraw(double amount);
    default void handleWithdraw(String id,double amount){
        AccountData.incBalance(id,-amount);
        AccountData.addOneHistory(id,amount,"","withdraw money");
    }
}
