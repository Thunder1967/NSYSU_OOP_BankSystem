package nsysu.bank.account;

import nsysu.bank.HistoryRecord;
import nsysu.util.enumtype.AccountType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.NegativeBalanceException;
import nsysu.util.sqlaccess.AccountData;

import java.util.List;

/**
 * 第三層：帳戶基礎抽象類別
 * 所有帳戶類型的父類別，定義共同屬性和方法
 * 建構時自動從資料庫載入帳戶資料
 */
public abstract class BasicAccount {
    private final String accountId;
    private final String type;
    protected double balance;
    protected String status;
    protected List<HistoryRecord> history;

    /** 建構子：透過第二層 AccountData 從資料庫載入帳戶的所有資料 */
    protected BasicAccount(String accountId, String type) {
        this.accountId = accountId;
        this.balance = AccountData.getBalance(accountId);
        this.history = AccountData.getHistory(accountId);
        this.status = AccountData.getStatus(accountId);
        this.type = type;
    }

    /**
     * 工廠方法（Factory Method）：根據資料庫中的帳戶類型，自動建立對應的子類別實例
     * 這就是多型（Polymorphism）的應用：回傳型別是 BasicAccount，
     * 但實際物件可能是 SavingAccount、TimeDeposit、CheckingAccount 或 ForeignAccount
     */
    public static BasicAccount loadAccount(String accountId) throws IdNotFindException{
        String type = AccountData.getType(accountId);
        if(type.equals(AccountType.SavingsAccount.getStr())){
            return new SavingAccount(accountId);
        }
        else if(type.equals(AccountType.TimeDeposit.getStr())){
            return new TimeDeposit(accountId);
        }
        else if(type.equals(AccountType.CheckingAccount.getStr())){
            return new CheckingAccount(accountId);
        }
        else if(type.equals(AccountType.USDAccount.getStr())){
            return new ForeignAccount(accountId);
        }
        else{
            throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }

    public String getId() {
        return accountId;
    }

    public double getBalance() {
        return balance;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public List<HistoryRecord> getHistory() {
        return history;
    }

    /**
     * 更新餘額：寫入資料庫並同步本地欄位
     * protected final：子類別可以呼叫，但不能覆寫（確保行為一致性）
     */
    protected final void updateBalance(double increment) throws NegativeBalanceException {
        AccountData.incBalance(accountId,increment);
        this.balance = AccountData.getBalance(accountId);
    }

    /** 重新從資料庫載入最新資料（餘額、歷史、狀態） */
    public void refresh(){
        this.balance = AccountData.getBalance(accountId);
        this.history = AccountData.getHistory(this.getId());
        this.status = AccountData.getStatus(this.getId());
    }

    /**
     * 存款：所有帳戶都可以存款，因此放在基底類別
     * 1. 檢查帳戶是否為 Active 狀態
     * 2. 增加餘額並記錄交易歷史
     *
     * @param amount 存入金額（必須為正數）
     * @return 是否存款成功
     */
    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        this.refresh(); // 計算利息防堵時間差漏洞
        if (!AccountData.transferable(accountId)) return false;
        updateBalance(amount);
        AccountData.addOneHistory(accountId, amount, "", "deposit money");
        return true;
    }
    /**
     * 轉帳操作：從本帳戶轉帳到目標帳戶
     * 1. 檢查雙方帳戶是否都是 Active 狀態
     * 2. 從本帳戶扣款、向目標帳戶入款
     * 3. 雙方各新增一筆交易歷史紀錄
     */
    public boolean transfer(String toId,double amount,String description) throws IdNotFindException,NegativeBalanceException{
        if(amount <= 0) return false;
        if(!(this instanceof CanTransferOut)) return false; // 只有實作 CanTransferOut 的帳戶才能轉出
        
        this.refresh(); // 更新本帳戶利息，防止時間差漏洞
        
        // 讀取並更新目標帳戶利息，防止時間差生息漏洞
        BasicAccount toAccount = BasicAccount.loadAccount(toId);
        toAccount.refresh();
        
        if(!AccountData.transferable(toId) || !AccountData.transferable(accountId)) return false;
        
        double receiveAmount = amount; // 預設接收金額與轉出金額相同（台幣）
        if (toAccount instanceof ForeignAccount) {
            // 若目標帳戶是外幣帳戶，將台幣金額換算為美金
            receiveAmount = Math.round((amount / ((ForeignAccount)toAccount).getExchangeRate()) * 100.0) / 100.0;
        }

        updateBalance(-amount);                                    // 本帳戶扣款
        try {
            AccountData.incBalance(toId, receiveAmount);                       // 目標帳戶入款
        } catch (Exception e) {
            updateBalance(amount); // 發生錯誤時回滾（Rollback）把錢加回本帳戶
            throw e;
        }
        AccountData.addOneHistory(accountId,-amount,toId,description);  // 記錄本帳戶支出
        
        String receiveDesc = description;
        if (toAccount instanceof ForeignAccount) {
            receiveDesc += String.format(" (rate: %.2f)", ((ForeignAccount)toAccount).getExchangeRate());
        }
        AccountData.addOneHistory(toId, receiveAmount, accountId, receiveDesc);   // 記錄對方帳戶收入
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s (%.3f)",accountId,type,status,balance);
    }
}
