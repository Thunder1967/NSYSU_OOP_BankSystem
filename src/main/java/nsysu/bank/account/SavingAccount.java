package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.sqlaccess.AccountData;

import java.time.Duration;
import java.util.Date;

public class SavingAccount extends InterestAccount implements CanTransferOut,CanWithdraw{

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
    public boolean transferOut(String toId, double amount, String description) {
        return transfer(toId,amount,description);
    }

    @Override
    public boolean withdraw(double amount) {
        if(transferable(this.getId())){
            handleWithdraw(this.getId(),amount);
            return true;
        }
        return false;
    }
}
