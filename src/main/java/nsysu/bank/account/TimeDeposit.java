package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;

import java.time.Duration;
import java.util.Date;

public class TimeDeposit extends InterestAccount {
    public TimeDeposit(String accountId) {
        this(accountId,0.001);
    }
    public TimeDeposit(String accountId, double rate) {
        super(accountId, AccountType.TimeDeposit.getStr(), rate);
        updateBalanceWithInterest();
    }

    @Override
    protected final void updateBalanceWithInterest(){
        Duration duration = Duration.between(new Date().toInstant(),this.date.toInstant());
        this.updateBalance(this.balance*this.rate*(duration.toHours()));
    }
}
