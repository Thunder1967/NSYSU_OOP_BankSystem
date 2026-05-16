package nsysu.util.sqlaccess;

import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.RoleType;
import nsysu.util.enumtype.StatusType;
import nsysu.util.enumtype.UserTarget;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.mongodb.MongoDBUtil;

import java.util.List;

/**
 * 第二層：使用者資料存取類別
 * 封裝 MongoDBUtil，提供使用者相關的高階查詢方法
 */
public final class UserData {
    private UserData() {
    }

    public static String getUserName(String id) throws IdNotFindException {
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS, id, UserTarget.UserName, String.class);
    }

    public static String getRole(String id) throws IdNotFindException {
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS, id, UserTarget.Role, String.class);
    }

    public static List<String> getAccount(String id) throws IdNotFindException {
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS, id, UserTarget.Accounts, List.class);
    }

    public static String getPassword(String id) throws IdNotFindException {
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS, id, UserTarget.Password, String.class);
    }

    public static String getStatus(String id) throws IdNotFindException {
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.USERS, id, UserTarget.Status, String.class);
    }

    public static String addAccount(String id, AccountType type) throws IdNotFindException {
        return MongoDBUtil.addNewAccount(id, type);
    }

    public static void setPassword(String id, String newPassword) throws IdNotFindException {
        MongoDBUtil.setData(MongoDBUtil.CollectionType.USERS, id, UserTarget.Password, newPassword);
    }

    /**
     * 停用使用者：將使用者狀態設為 Closed
     * 同時將該使用者名下所有帳戶也設為 Closed（級聯操作）
     */
    public static void deactiveUser(String id) throws IdNotFindException {
        MongoDBUtil.setData(MongoDBUtil.CollectionType.USERS, id, UserTarget.Status, StatusType.Closed.getStr());
        List<String> accounts = getAccount(id);
        for (String accountId : accounts) {
            AccountData.setStatus(accountId, StatusType.Closed);
        }
    }

    /**
     * 刪除使用者：先刪除名下所有帳戶，再刪除使用者本身（級聯刪除）
     */
    public static void deleteUser(String id) throws IdNotFindException {
        List<String> accounts = getAccount(id);
        for (String accountId : accounts) {
            MongoDBUtil.deleteData(MongoDBUtil.CollectionType.ACCOUNTS, accountId);
        }
        MongoDBUtil.deleteData(MongoDBUtil.CollectionType.USERS, id);
    }

    public static String addNewUser(String userName, String password, RoleType type) {
        return MongoDBUtil.addNewUser(userName, password, type);
    }
}
