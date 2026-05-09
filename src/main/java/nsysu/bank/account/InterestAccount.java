package nsysu.bank.account;

import nsysu.util.sqlaccess.AccountData;

import java.util.Date;
/**
 * @startuml
 * class InterestAccount
 * @enduml
 */
public abstract class InterestAccount extends BasicAccount {
    protected Date date;
    protected double rate;

    protected InterestAccount(String accountId, String type, double rate) {
        super(accountId,type);
        this.date = AccountData.getLastView(accountId);
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    protected abstract void updateBalanceWithInterest();

    @Override
    public void refresh() {
        updateBalanceWithInterest();
        this.history = AccountData.getHistory(this.getId());
        this.status = AccountData.getStatus(this.getId());
    }
}
