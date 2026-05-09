package nsysu.bank.role;

import nsysu.bank.account.BasicAccount;
import nsysu.util.enumtype.StatusType;
import nsysu.util.exception.ClosedUserException;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.sqlaccess.UserData;

import java.util.ArrayList;
import java.util.List;

public abstract class Person {
    private final String userId;
    private final String name;
    private final String role;
    private String status;
    private String password;
    protected ArrayList<BasicAccount> account;

    public Person(String userId,String role) throws IdNotFindException {
        this.userId = userId;
        this.name = UserData.getUserName(userId);
        this.role = role;
        refresh();
    }

    public String getId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        UserData.setPassword(userId,password);
        this.password = password;
    }

    public final void refresh(){
        this.status = UserData.getStatue(userId);
        if(status.equals(StatusType.Closed.getStr())){
            throw new ClosedUserException();
        }
        this.account = new ArrayList<BasicAccount>();
        for(String id:UserData.getAccount(userId)){
            this.account.add(BasicAccount.loadAccount(id));
        }
        this.password = UserData.getPassword(userId);
    }
}
