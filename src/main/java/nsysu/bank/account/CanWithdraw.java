package nsysu.bank.account;

import nsysu.util.sqlaccess.AccountData;

/**
 * 提款能力介面：實作此介面的帳戶可以提款
 * 使用 default method 提供共用的提款邏輯，子類別不需重複實作
 */
public interface CanWithdraw {
    boolean withdraw(double amount);

    /**
     * 預設提款處理方法（Java 8 default method）
     * 從餘額中扣除金額，並記錄一筆提款歷史
     */
    default void handleWithdraw(String id,double amount){
        AccountData.incBalance(id,-amount);
        AccountData.addOneHistory(id,-amount,"","withdraw money");
    }
}
