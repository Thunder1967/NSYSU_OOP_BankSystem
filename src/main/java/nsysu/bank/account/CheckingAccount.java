package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.sqlaccess.AccountData;

import java.util.Date;

public class CheckingAccount extends BasicAccount implements CanWithdraw,CanTransferOut{
    protected CheckingAccount(String accountId) {
        super(accountId, AccountType.CheckingAccount.getStr());
    }


    @Override
    public boolean transferOut(String toId, double amount, String description) {
        return false;
    }

    @Override
    public boolean withdraw(double amount) {
        return false;
    }
}
