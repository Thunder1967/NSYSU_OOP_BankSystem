package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.StatusType;
import nsysu.util.sqlaccess.AccountData;

public class CheckingAccount extends BasicAccount implements Transactable, ExternalTransferable {
    protected CheckingAccount(String accountId) {
        super(accountId, AccountType.CheckingAccount.getStr());
    }

    @Override
    public boolean externalTransfer(String toId, double amount, String description) {
        if(!isValidAmount(amount)){
            return false;
        }
        return transfer(toId,amount,description);
    }

    @Override
    public boolean withdraw(double amount) {
        if(!isValidAmount(amount) || !checkStatusMatch(StatusType.Active)){
            return false;
        }
        updateBalance(-amount);
        AccountData.addOneHistory(this.getId(),-amount,"","withdraw money from checking account");
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
        addNewHistory(amount,"","deposit money to checking account");
        return true;
    }
}
