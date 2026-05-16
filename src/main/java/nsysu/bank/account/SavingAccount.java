package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.sqlaccess.AccountData;

import java.time.Duration;
import java.util.Date;

/**
 * 活期儲蓄帳戶：繼承 InterestAccount，實作 CanTransferOut 和 CanWithdraw 介面
 * 功能最完整的帳戶類型：可計息、可提款、可轉帳
 */
public class SavingAccount extends InterestAccount implements CanTransferOut,CanWithdraw{

    public SavingAccount(String accountId, double rate) {
        super(accountId, AccountType.SavingsAccount.getStr(), rate);
        updateBalanceWithInterest();
    }

    public SavingAccount(String accountId){
        this(accountId,0.0001);
    }

    /**
     * 利息計算公式：餘額 × 利率 × 經過分鐘數
     * 計算完成後更新 lastview，避免下次重複計息
     */
    @Override
    protected final void updateBalanceWithInterest(){
        Duration duration = Duration.between(this.date.toInstant(), new Date().toInstant());
        double interest = this.balance * this.rate * (duration.toMillis() / 60000.0);
        if (interest >= 0.01) {
            this.updateBalance(interest);
            AccountData.addOneHistory(this.getId(), interest, "", "interest earned");
            AccountData.setLastView(this.getId());
            this.date = new Date();
        }
    }

    @Override
    public boolean transferOut(String toId, double amount, String description) {
        return transfer(toId,amount,description);
    }

    /** 提款：檢查帳戶是否為 Active 狀態，若是則執行提款並記錄歷史 */
    @Override
    public boolean withdraw(double amount) {
        if(amount <= 0) return false;
        this.refresh(); // 計算利息防堵時間差漏洞
        if(AccountData.transferable(this.getId())){
            handleWithdraw(this.getId(),amount);
            return true;
        }
        return false;
    }
}
