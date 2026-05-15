package nsysu.bank.role;

import nsysu.bank.account.BasicAccount;
import nsysu.bank.account.CanTransferOut;
import nsysu.bank.account.Transactable;
import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.RoleType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.NegativeBalanceException;
import nsysu.util.sqlaccess.UserData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

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
        if(amount>0){
            String toId = this.account.get(to).getId();
            return this.account.get(from).transfer(toId,amount,description);
        }
        return false;
    }

    public boolean externalTransfer(int from,String toId,double amount,String description)
            throws IdNotFindException,NegativeBalanceException,IndexOutOfBoundsException{
        if(this.account.get(from) instanceof CanTransferOut fromAccount){
            if(amount<=0 || this.account.stream().anyMatch(i->i.getId().equals(toId))){
                return false;
            }
            else{
                return fromAccount.transferOut(toId,amount,description);
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
    public void closeAccount(int from)
            throws IndexOutOfBoundsException{
        this.account.get(from).closeAccount();
    }
}
