package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.exception.IdNotFindException;

import java.time.Duration;
import java.util.Date;

public class SavingAccount extends InterestAccount implements CanTransferOut, Transactable, TransactableTool {

    public SavingAccount(String accountId, double rate) {
        super(accountId, AccountType.SavingsAccount.getStr(), rate);
        updateBalanceWithInterest();
    }

    public SavingAccount(String accountId){
        this(accountId,0.0001);
    }

    @Override
    protected final void updateBalanceWithInterest(){
        Duration duration = Duration.between(this.date.toInstant(),new Date().toInstant()).abs();
        this.updateBalance(this.balance*this.rate*(duration.toMinutes()));
        super.updateBalanceWithInterest();
    }

    @Override
    public boolean transferOut(String toId, double amount, String description) throws IdNotFindException,NegativeArraySizeException {
        if(amount>0){
            return transfer(toId,amount,description);
        }
        return false;
    }

    @Override
    public boolean withdraw(double amount) throws NegativeArraySizeException{
        if(amount>0 && transferable(this.getId())){
            handleWithdraw(this.getId(),amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean deposit(double amount) {
        if(amount>0 && transferable(this.getId())){
            handleDeposit(this.getId(),amount);
            return true;
        }
        return false;
    }
}
