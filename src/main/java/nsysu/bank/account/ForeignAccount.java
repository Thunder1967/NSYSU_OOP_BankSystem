package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.exception.NegativeBalanceException;
import nsysu.util.sqlaccess.AccountData;

import java.time.Duration;
import java.util.Date;

public class ForeignAccount extends InterestAccount implements CanWithdraw{

    public ForeignAccount(String accountId, double rate) {
        super(accountId, AccountType.USDAccount.getStr(), rate);
        updateBalanceWithInterest();
    }

    public ForeignAccount(String accountId){
        this(accountId,0.0001);
    }

    @Override
    protected final void updateBalanceWithInterest(){
        Duration duration = Duration.between(this.date.toInstant(), new Date().toInstant());
        this.updateBalance(this.balance * this.rate * duration.toMinutes());
    }

    @Override
    public boolean withdraw(double amount) {
        if(!isValidAmount(amount) || !AccountData.transferable(this.getId())){
            return false;
        }
        try{
            updateBalance(-amount);
            AccountData.addOneHistory(this.getId(), -amount, "", "withdraw money from foreign account");
        }
        catch (NegativeBalanceException e){
            return false;
        }
        return true;
    }

    private boolean isValidAmount(double amount) {
        return amount > 0;
    }
}
