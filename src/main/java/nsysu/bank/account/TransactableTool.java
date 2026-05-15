package nsysu.bank.account;

import nsysu.util.sqlaccess.AccountData;

public interface TransactableTool {
    default void handleWithdraw(String id,double amount) throws NegativeArraySizeException{
        AccountData.incBalance(id,-amount);
        AccountData.addOneHistory(id,amount,"","withdraw money");
    }
    default void handleDeposit(String id,double amount){
        AccountData.incBalance(id,amount);
        AccountData.addOneHistory(id,amount,"","deposit money");
    }
}
