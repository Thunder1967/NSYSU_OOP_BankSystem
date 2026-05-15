package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.exception.NegativeBalanceException;
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
        try{
            return transfer(toId,amount,description);
        }
        catch (NegativeBalanceException e){
            return false;
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if(!isValidAmount(amount) || !transferable(this.getId())){
            return false;
        }
        try{
            updateBalance(-amount);
            AccountData.addOneHistory(this.getId(),-amount,"","withdraw money from checking account");
        }
        catch (NegativeBalanceException e){
            return false;
        }
        return true;
    }

    private boolean isValidAmount(double amount) {
        return amount > 0;
    }

    @Override
    public boolean deposit(double amount) {
        return false;
    }
}
