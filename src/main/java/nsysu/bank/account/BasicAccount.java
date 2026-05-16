package nsysu.bank.account;

import nsysu.bank.HistoryRecord;
import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.StatusType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.NegativeBalanceException;
import nsysu.util.sqlaccess.AccountData;

import java.util.List;

public abstract class BasicAccount {
    private final String accountId;
    private final String type;
    protected double balance;
    protected String status;
    protected List<HistoryRecord> history;

    protected BasicAccount(String accountId, String type) {
        this.accountId = accountId;
        this.balance = AccountData.getBalance(accountId);
        this.history = AccountData.getHistory(accountId);
        this.status = AccountData.getStatus(accountId);
        this.type = type;
    }

    public static BasicAccount loadAccount(String accountId) throws IdNotFindException{
        String type = AccountData.getType(accountId);
        if(type.equals(AccountType.SavingsAccount.getStr())){
            return new SavingAccount(accountId);
        }
        else if(type.equals(AccountType.TimeDeposit.getStr())){
            return new TimeDeposit(accountId);
        }
        else if(type.equals(AccountType.CheckingAccount.getStr())){
            return new CheckingAccount(accountId);
        }
        else{
            return new ForeignAccount(accountId);
        }
    }

    public String getId() {
        return accountId;
    }

    public double getBalance() {
        return balance;
    }

    public double getBalanceInNTD() {
        return balance;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public List<HistoryRecord> getHistory() {
        return history;
    }

    public void addNewHistory(double amount,String anotherId,String description){
        AccountData.addOneHistory(this.accountId,amount,anotherId,description);
        this.history = AccountData.getHistory(this.getId());
    }

    protected final void updateBalance(double increment) throws NegativeBalanceException {
        AccountData.incBalance(accountId,increment);
        this.balance = AccountData.getBalance(accountId);
    }

    public void refresh(){
        this.balance = AccountData.getBalance(accountId);
        this.history = AccountData.getHistory(this.getId());
        this.status = AccountData.getStatus(this.getId());
    }

    public boolean transfer(String toId,double amount,String description) throws IdNotFindException,NegativeBalanceException{
        if(checkStatusMatch(StatusType.Active) &&
                StatusType.checkMatch(AccountData.getStatus(toId),StatusType.Active,StatusType.Frozen)){
            updateBalance(-amount);
            AccountData.incBalance(toId,amount);
            addNewHistory(-amount,toId,description);
            AccountData.addOneHistory(toId,amount,accountId,description);
            return true;
        }
        else {
            return false;
        }
    }

    public void closeAccount(){
        AccountData.setStatus(this.accountId,StatusType.Closed);
        refresh();
    }

    public boolean checkStatusMatch(StatusType... types){
        return StatusType.checkMatch(this.type,types);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s (%.3f)",accountId,type,status,balance);
    }
}
