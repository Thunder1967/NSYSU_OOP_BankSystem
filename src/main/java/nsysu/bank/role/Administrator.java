package nsysu.bank.role;

import nsysu.util.enumtype.RoleType;
import nsysu.util.enumtype.StatusType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.SameUserNameException;
import nsysu.util.sqlaccess.AccountData;
import nsysu.util.sqlaccess.UserData;

public class Administrator extends Person {
    public Administrator(String userId) throws IdNotFindException {
        super(userId, RoleType.Administrator.getStr());
    }

    /** 新增使用者 */
    public String addNewUser(String username, String password, RoleType role) throws SameUserNameException {
        return UserData.addNewUser(username, password, role);
    }

    /** 刪除(停用)使用者：資料庫留有紀錄，但無法登入與操作 */
    public void deactivateUser(String userId) throws IdNotFindException {
        UserData.deactiveUser(userId);
    }

    /** 解凍使用者的帳戶 */
    public void unfreezeAccount(String accountId) throws IdNotFindException {
        AccountData.setStatus(accountId, StatusType.Active);
    }
}
