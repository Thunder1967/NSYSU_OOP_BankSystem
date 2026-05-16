package nsysu.bank.role;

import nsysu.bank.account.BasicAccount;
import nsysu.util.enumtype.RoleType;
import nsysu.util.enumtype.StatusType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.SameUserNameException;
import nsysu.util.exception.TargetNotFindException;
import nsysu.util.sqlaccess.AccountData;
import nsysu.util.sqlaccess.UserData;

import java.util.ArrayList;
import java.util.List;

public class Administrator extends Person{
    public Administrator(String userId) throws IdNotFindException {
        super(userId, RoleType.Administrator.getStr());
    }
    public String addNewUser(String name,String password) throws SameUserNameException {
        return UserData.addNewUser(name,password,RoleType.User);
    }
    public boolean deactivateUser(String name) throws TargetNotFindException{
        final String Id = UserData.getIdByName(name);
        if(UserData.getStatue(Id).equals(StatusType.Active.getStr())){
            UserData.deactiveUser(Id);
            return true;
        }
        else return false;
    }
    public boolean giveMoney(String id,double value) throws IdNotFindException {
        if(value<0) return false;
        AccountData.incBalance(id,value);
        AccountData.addOneHistory(id,value,"Bank","Money from bank");
        return true;
    }
    public boolean unfreezeAccount(String id) throws IdNotFindException{
        if(AccountData.getStatus(id).equals(StatusType.Frozen.getStr())){
            AccountData.setStatus(id,StatusType.Active);
            return true;
        }
        else {
            return false;
        }
    }
    public List<BasicAccount> getAccountByUserName(String name) throws TargetNotFindException{
        final String Id = UserData.getIdByName(name);
        List<BasicAccount> accountList = new ArrayList<>();
        for(String id:UserData.getAccount(Id)){
            accountList.add(BasicAccount.loadAccount(id));
        }
        return accountList;
    }
}
