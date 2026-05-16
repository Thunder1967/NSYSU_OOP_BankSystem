package nsysu.bank.role;

import nsysu.bank.account.BasicAccount;
import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.RoleType;
import nsysu.util.enumtype.StatusType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.NegativeBalanceException;
import nsysu.util.sqlaccess.AccountData;
import nsysu.util.sqlaccess.UserData;

import java.util.ArrayList;

public class User extends Person {
    public User(String userId) throws IdNotFindException {
        super(userId, RoleType.User.getStr());
    }

    public String addAccount(AccountType type) {
        String tmp = UserData.addAccount(this.getId(), type);
        refresh();
        return tmp;
    }

    /**
     * 發起轉帳操作（包含外部大額轉帳的凍結檢查）
     * @param fromAccountId 發起轉帳的本帳戶 ID
     * @param toId 目標帳戶 ID
     * @param amount 轉帳金額
     * @param description 備註
     * @return 是否成功轉帳
     */
    public boolean transferMoney(String fromAccountId, String toId, double amount, String description) throws IdNotFindException, NegativeBalanceException {
        // 1. 檢查發起帳戶是否屬於該 User
        BasicAccount fromAccount = null;
        for (BasicAccount acc : this.getAccounts()) {
            if (acc.getId().equals(fromAccountId)) {
                fromAccount = acc;
                break;
            }
        }
        if (fromAccount == null) return false;

        // 2. 判斷是否為內部轉帳（toId 是否也在該 User 名下）
        boolean isInternal = false;
        for (BasicAccount acc : this.getAccounts()) {
            if (acc.getId().equals(toId)) {
                isInternal = true;
                break;
            }
        }

        // 3. 如果是外部轉帳，且金額大於 30000，則凍結發起帳戶並阻擋轉帳
        if (!isInternal && amount > 30000) {
            AccountData.setStatus(fromAccountId, StatusType.Frozen);
            refresh(); // 更新本地資料
            return false;
        }

        // 4. 執行實際轉帳
        boolean success = fromAccount.transfer(toId, amount, description);
        if (success) refresh();
        return success;
    }

    /** 關閉名下的特定帳戶 */
    public void closeAccount(String accountId) {
        // 檢查該帳戶是否屬於此 User
        for (BasicAccount acc : this.getAccounts()) {
            if (acc.getId().equals(accountId)) {
                AccountData.setStatus(accountId, StatusType.Closed);
                refresh();
                break;
            }
        }
    }
}
