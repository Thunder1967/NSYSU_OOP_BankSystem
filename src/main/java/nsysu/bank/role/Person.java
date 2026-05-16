package nsysu.bank.role;

import nsysu.bank.account.BasicAccount;
import nsysu.util.enumtype.StatusType;
import nsysu.util.exception.ClosedUserException;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.sqlaccess.UserData;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三層：人員抽象基礎類別
 * User 和 Administrator 的父類別
 * 建構時自動從資料庫載入使用者資訊和其名下所有帳戶
 */
public abstract class Person {
    private final String userId;
    private final String name;
    private final String role;
    private String status;
    private String password;
    protected ArrayList<BasicAccount> account; // 該使用者擁有的所有帳戶

    public Person(String userId, String role) throws IdNotFindException {
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
        UserData.setPassword(userId, password);
        this.password = password;
    }

    /** 取得該使用者名下的所有帳戶列表（供 GUI 層使用） */
    public List<BasicAccount> getAccounts() {
        return account;
    }

    /**
     * 重新載入使用者資料（充當「工作階段守衛」）
     * 1. 檢查使用者是否已被停用，若是則拋出 ClosedUserException
     * 2. 重新載入其名下所有帳戶物件（透過工廠方法 loadAccount）
     * 3. 重新載入密碼
     */
    public final void refresh() {
        this.status = UserData.getStatus(userId);
        if (status.equals(StatusType.Closed.getStr())) {
            throw new ClosedUserException();
        }
        this.account = new ArrayList<BasicAccount>();
        for (String id : UserData.getAccount(userId)) {
            this.account.add(BasicAccount.loadAccount(id)); // 工廠方法自動判斷帳戶類型
        }
        this.password = UserData.getPassword(userId);
    }
}
