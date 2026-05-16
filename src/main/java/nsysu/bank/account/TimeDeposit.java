package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.sqlaccess.AccountData;

import java.time.Duration;
import java.util.Date;

/**
 * 定期存款帳戶：繼承 InterestAccount
 * 特點：利率較高（0.001），但不能提款、不能轉帳（資金鎖定）
 * 利息以「小時」為單位計算（比活存更高的計息粒度）
 * 不實作 CanWithdraw 和 CanTransferOut，因此無法提款或轉出
 */
public class TimeDeposit extends InterestAccount {

    public TimeDeposit(String accountId) {
        this(accountId, 0.001);
    }

    public TimeDeposit(String accountId, double rate) {
        super(accountId, AccountType.TimeDeposit.getStr(), rate);
        updateBalanceWithInterest();
    }

    /**
     * 利息計算公式：餘額 × 利率 × 經過小時數
     * 計算完成後更新 lastview，避免重複計息
     */
    @Override
    protected final void updateBalanceWithInterest() {
        Duration duration = Duration.between(this.date.toInstant(), new Date().toInstant());
        double interest = this.balance * this.rate * (duration.toMillis() / 3600000.0);
        if (interest >= 0.01) {
            this.updateBalance(interest);
            AccountData.addOneHistory(this.getId(), interest, "", "interest earned");
            AccountData.setLastView(this.getId());
            this.date = new Date();
        }
    }

    /** 定存帳戶不允許後續繼續存入資金 */
    @Override
    public boolean deposit(double amount) {
        return false;
    }
}
