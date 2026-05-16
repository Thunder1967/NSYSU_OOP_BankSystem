package nsysu.bank.account;

import nsysu.util.sqlaccess.AccountData;

import java.util.Date;
/**
 * @startuml
 * class InterestAccount
 * @enduml
 */
/**
 * 計息帳戶抽象類別：繼承 BasicAccount，新增利息計算功能
 * SavingAccount、TimeDeposit、ForeignAccount 都繼承此類別
 * 每種帳戶的利息計算方式不同，因此 updateBalanceWithInterest() 是抽象方法
 */
public abstract class InterestAccount extends BasicAccount {
    protected Date date;   // 上次查看時間（用於計算經過時間）
    protected double rate; // 利率

    protected InterestAccount(String accountId, String type, double rate) {
        super(accountId,type);
        this.date = AccountData.getLastView(accountId);
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    /** 抽象方法：子類別必須實作自己的利息計算公式 */
    protected abstract void updateBalanceWithInterest();

    /** 覆寫 refresh：先計算利息，再重新載入歷史和狀態 */
    @Override
    public void refresh() {
        updateBalanceWithInterest();
        this.history = AccountData.getHistory(this.getId());
        this.status = AccountData.getStatus(this.getId());
    }
}
