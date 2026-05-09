package nsysu.bank.role;

import nsysu.bank.account.BasicAccount;
import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.RoleType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.sqlaccess.UserData;

import java.util.ArrayList;

public class User extends Person{
    public User(String userId) throws IdNotFindException {
        super(userId, RoleType.User.getStr());
    }

    public String addAccount(AccountType type){
        String tmp = UserData.addAccount(this.getId(),type);
        refresh();
        return tmp;
    }
}
