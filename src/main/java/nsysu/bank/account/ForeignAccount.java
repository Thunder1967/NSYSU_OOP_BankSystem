package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;

import java.time.Duration;
import java.util.Date;

public class ForeignAccount extends InterestAccount implements CanWithdraw{

    public ForeignAccount(String accountId, double rate) {
        super(accountId, AccountType.SavingsAccount.getStr(), rate);
        updateBalanceWithInterest();
    }

    public ForeignAccount(String accountId){
        this(accountId,0.0001);
    }

    @Override
    protected final void updateBalanceWithInterest(){
        Duration duration = Duration.between(new Date().toInstant(),this.date.toInstant());
        this.updateBalance(this.balance*this.rate*(duration.toMinutes()));
    }

    @Override
    public boolean withdraw(double amount) {
        return false;
    }
}
