package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.exception.IdNotFindException;

import java.time.Duration;
import java.util.Date;

public class SavingAccount extends InterestAccount {

    public SavingAccount(String accountId, double rate) {
        super(accountId, AccountType.SavingsAccount.getStr(), rate);
        updateBalanceWithInterest();
    }

    public SavingAccount(String accountId){
        this(accountId,0.0001);
    }

    @Override
    protected final void updateBalanceWithInterest(){
        Duration duration = Duration.between(new Date().toInstant(),this.date.toInstant());
        this.updateBalance(this.balance*this.rate*(duration.toMinutes()));
    }
}
