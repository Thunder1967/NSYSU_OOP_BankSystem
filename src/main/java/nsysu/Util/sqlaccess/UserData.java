package nsysu.Util.sqlaccess;

import nsysu.Util.enumtype.AccountType;
import nsysu.Util.enumtype.RoleType;
import nsysu.Util.enumtype.StatusType;
import nsysu.Util.enumtype.UserTarget;
import nsysu.Util.exception.IdNotFindException;
import nsysu.Util.mongodb.MongoDBUtil;

import java.util.ArrayList;
import java.util.List;

public final class UserData {
    private UserData(){}
    public static String getUserName(String id) throws IdNotFindException{
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS,id, UserTarget.UserName, String.class);
    }
    public static String getRole(String id) throws IdNotFindException{
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS,id, UserTarget.Role, String.class);
    }
    public static List<String> getAccount(String id) throws IdNotFindException{
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS,id, UserTarget.Accounts, List.class);
    }
    public static String getPassword(String id) throws IdNotFindException{
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS,id, UserTarget.Password, String.class);
    }
    public static String getStatue(String id) throws IdNotFindException{
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS,id, UserTarget.Status, String.class);
    }

    public static String addAccount(String id, AccountType type) throws IdNotFindException{
        return MongoDBUtil.addNewAccount(id,type);
    }
    public static void setPassword(String id,String newPassword) throws IdNotFindException{
        MongoDBUtil.setData(MongoDBUtil.CollectionType.USERS,id, UserTarget.Password, newPassword);
    }

    public static void deactiveUser(String id) throws IdNotFindException{
        MongoDBUtil.setData(MongoDBUtil.CollectionType.USERS,id, UserTarget.Status, StatusType.Closed.getStr());
        List<String> accounts = getAccount(id);
        for(String accountId : accounts){
            AccountData.setStatus(accountId,StatusType.Closed);
        }
    }

    public static void deleteUser(String id) throws IdNotFindException{
        List<String> accounts = getAccount(id);
        for(String accountId : accounts){
            MongoDBUtil.deleteData(MongoDBUtil.CollectionType.ACCOUNTS,accountId);
        }
        MongoDBUtil.deleteData(MongoDBUtil.CollectionType.USERS,id);
    }

    public static String addNewUser(String userName, String password, RoleType type){
        return MongoDBUtil.addNewUser(userName,password,type);
    }
}
