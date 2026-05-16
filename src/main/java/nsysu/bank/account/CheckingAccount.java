package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.sqlaccess.AccountData;

/**
 * 支票帳戶：繼承 BasicAccount（無利息），實作 CanWithdraw 和 CanTransferOut
 * 特點：沒有利息，但可以提款和轉出
 */
public class CheckingAccount extends BasicAccount implements CanWithdraw, CanTransferOut {

    public CheckingAccount(String accountId) {
        super(accountId, AccountType.CheckingAccount.getStr());
    }

    /** 轉出：委託父類別的 transfer 方法執行轉帳操作 */
    @Override
    public boolean transferOut(String toId, double amount, String description) {
        return transfer(toId, amount, description);
    }

    /** 提款：檢查帳戶是否為 Active 狀態，若是則執行提款並記錄歷史 */
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        this.refresh(); // 同步最新狀態，防堵時間差
        if (AccountData.transferable(this.getId())) {
            handleWithdraw(this.getId(), amount);
            return true;
        }
        return false;
    }
}
