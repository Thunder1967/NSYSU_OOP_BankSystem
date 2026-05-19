package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.StatusType;
import nsysu.util.sqlaccess.AccountData;

import java.time.Duration;
import java.util.Date;
import java.util.Random;

public class ForeignAccount extends InterestAccount implements Transactable {
    private static final Random RANDOM = new Random();
    public ForeignAccount(String accountId, double rate) {
        super(accountId, AccountType.USDAccount.getStr(), rate);
        updateBalanceWithInterest();
    }

    public ForeignAccount(String accountId){
        this(accountId,0.0001);
    }

    static public double USDtoNTD(){
        RANDOM.setSeed(System.currentTimeMillis()/3600_000);
        return 30.0 + (RANDOM.nextDouble() - 0.5) * 10.0;
    }

    @Override
    public double getBalanceInNTD() {
        return super.getBalanceInNTD()*USDtoNTD();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s (%.3f USD)",this.getId(),this.getType(),status,balance);
    }

    @Override
    protected final void updateBalanceWithInterest(){
        Duration duration = Duration.between(this.date.toInstant(), new Date().toInstant()).abs();
        this.updateBalance(this.balance * this.rate * duration.toMinutes());
        super.updateBalanceWithInterest();
    }

    @Override
    public boolean withdraw(double amount) {
        if(!isValidAmount(amount) || !checkStatusMatch(StatusType.Active)){
            return false;
        }
        updateBalance(-amount);
        AccountData.addOneHistory(this.getId(), -amount, "", "withdraw money from foreign account");
        return true;
    }

    private boolean isValidAmount(double amount) {
        return amount > 0;
    }

    @Override
    public boolean deposit(double amount) {
        if(!isValidAmount(amount) || !checkStatusMatch(StatusType.Active,StatusType.Frozen)){
            return false;
        }
        updateBalance(amount);
        addNewHistory(amount, "", "deposit money to foreign account");
        return true;
    }
}
