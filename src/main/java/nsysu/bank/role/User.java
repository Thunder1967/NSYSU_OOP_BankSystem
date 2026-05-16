package nsysu.bank.role;

import nsysu.bank.HistoryRecord;
import nsysu.bank.account.BasicAccount;
import nsysu.bank.account.ExternalTransferable;
import nsysu.bank.account.Transactable;
import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.RoleType;
import nsysu.util.enumtype.StatusType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.NegativeBalanceException;
import nsysu.util.sqlaccess.AccountData;
import nsysu.util.sqlaccess.UserData;

import java.util.ArrayList;
import java.util.List;

public class User extends Person{
    public User(String userId) throws IdNotFindException {
        super(userId, RoleType.User.getStr());
    }

    public String addAccount(AccountType type){
        String tmp = UserData.addAccount(this.getId(),type);
        refresh();
        return tmp;
    }

    public ArrayList<BasicAccount> getAccount(){
        return this.account;
    }

    public double queryTotalBalance(){
        double total = 0;
        for(BasicAccount account1:this.account){
            total+=account1.getBalanceInNTD();
        }
        return total;
    }

    public boolean internalTransfer(int from,int to,double amount,String description)
            throws IndexOutOfBoundsException, NegativeBalanceException {
        if(amount>0 && from!=to){
            String toId = this.account.get(to).getId();
            boolean result = this.account.get(from).transfer(toId,amount,description);
            this.account.get(to).refresh();
            return result;
        }
        return false;
    }

    public boolean externalTransfer(int from,String toId,double amount,String description)
            throws IdNotFindException,NegativeBalanceException,IndexOutOfBoundsException{
        if(this.account.get(from) instanceof ExternalTransferable fromAccount &&
                BasicAccount.loadAccount(toId) instanceof ExternalTransferable){
            if(amount<=0 || this.account.get(from).getId().equals(toId) ||
                    this.account.stream().anyMatch(i->i.getId().equals(toId))){
                return false;
            }
            else{
                if(fromAccount.externalTransfer(toId,amount,description)){
                    if(amount>30000){
                        AccountData.setStatus(toId, StatusType.Frozen);
                    }
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        return false;
    }

    public boolean withdraw(int from,double amount)
            throws NegativeBalanceException,IndexOutOfBoundsException{
        if(amount>0){
            if(this.account.get(from) instanceof Transactable fromAccount){
                return fromAccount.withdraw(amount);
            }
        }
        return false;
    }
    public boolean deposit(int from,double amount)
            throws IndexOutOfBoundsException{
        if(amount>0){
            if(this.account.get(from) instanceof Transactable fromAccount){
                return fromAccount.deposit(amount);
            }
        }
        return false;
    }
    public boolean closeAccount(int from)
            throws IndexOutOfBoundsException{
        return this.account.get(from).closeAccount();
    }
    public List<HistoryRecord> getAccountHistory(int from)
            throws IndexOutOfBoundsException{
        return this.account.get(from).getHistory();
    }
}
