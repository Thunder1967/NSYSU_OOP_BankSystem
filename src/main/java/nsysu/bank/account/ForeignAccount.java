package nsysu.bank.account;

import nsysu.util.enumtype.AccountType;
import nsysu.util.sqlaccess.AccountData;

import java.time.Duration;
import java.util.Date;
import java.util.Random;

/**
 * 外幣帳戶（USD）：繼承 InterestAccount，實作 CanWithdraw
 * 特點：
 * - 可計息（以分鐘為單位）
 * - 可提款，但提款時需要換匯（USD → TWD）
 * - 不能轉出（未實作 CanTransferOut，因為外幣帳戶不支援直接轉帳）
 * - 匯率以當前時間為種子碼隨機產生
 */
public class ForeignAccount extends InterestAccount implements CanWithdraw {

    /** 當前匯率（USD → TWD），每次建構或刷新時更新 */
    private double exchangeRate;

    public ForeignAccount(String accountId, double rate) {
        super(accountId, AccountType.USDAccount.getStr(), rate);
        this.exchangeRate = generateExchangeRate();
        updateBalanceWithInterest();
    }

    public ForeignAccount(String accountId) {
        this(accountId, 0.0001);
    }

    /**
     * 產生匯率：以當前時間毫秒數為種子碼，產生隨機匯率
     * 模擬 USD/TWD 匯率，範圍約在 29.0 ~ 33.0 之間
     */
    private double generateExchangeRate() {
        Random random = new Random(System.currentTimeMillis());
        // 基準匯率 30.0，上下浮動 ±2.0（範圍 28.0 ~ 32.0）
        double base = 30.0;
        double fluctuation = (random.nextDouble() * 4.0) - 2.0;
        // 四捨五入到小數點後兩位
        return Math.round((base + fluctuation) * 100.0) / 100.0;
    }

    /** 取得當前匯率 */
    public double getExchangeRate() {
        return exchangeRate;
    }

    /** 取得換算後的台幣等值餘額 */
    public double getTwdEquivalentBalance() {
        return Math.round((this.balance * exchangeRate) * 100.0) / 100.0;
    }

    /**
     * 利息計算公式：餘額 × 利率 × 經過分鐘數
     * 計算完成後更新 lastview，避免重複計息
     */
    @Override
    protected final void updateBalanceWithInterest() {
        Duration duration = Duration.between(this.date.toInstant(), new Date().toInstant());
        double interest = this.balance * this.rate * (duration.toMillis() / 60000.0);
        if (interest >= 0.01) {
            this.updateBalance(interest);
            AccountData.addOneHistory(this.getId(), interest, "", "interest earned");
            AccountData.setLastView(this.getId());
            this.date = new Date();
        }
    }

    /**
     * 存款（含換匯）：
     * 使用者輸入的 amount 為台幣金額（TWD）
     * 系統會根據當前匯率換算成等值的 USD 存入帳戶中
     */
    @Override
    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        this.refresh(); // 計算利息防堵時間差漏洞
        if (!AccountData.transferable(this.getId())) return false;
        
        // 將台幣金額換算成美元金額
        double usdAmount = Math.round((amount / exchangeRate) * 100.0) / 100.0;
        
        // 增加帳戶的美元餘額
        updateBalance(usdAmount);
        
        // 記錄歷史：描述中標明匯率和換算金額
        String description = String.format("deposit %.2f TWD (rate: %.2f, added: %.2f USD)",
                amount, exchangeRate, usdAmount);
        AccountData.addOneHistory(this.getId(), usdAmount, "", description);
        return true;
    }

    /**
     * 提款（含換匯）：
     * 使用者輸入的 amount 為希望提領的台幣金額（TWD）
     * 系統會根據當前匯率換算成等值的 USD 從帳戶中扣除
     *
     * 例如：匯率 30.5，提領 3050 TWD → 扣除 100 USD
     *
     * @param amount 希望提領的台幣金額（TWD）
     * @return 是否提款成功
     */
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        this.refresh(); // 計算利息防堵時間差漏洞
        if (!AccountData.transferable(this.getId())) {
            return false;
        }
        // 將台幣金額換算成美元金額
        double usdAmount = Math.round((amount / exchangeRate) * 100.0) / 100.0;
        // 檢查餘額是否足夠
        if (this.balance < usdAmount) {
            return false;
        }
        // 從帳戶扣除美元金額
        AccountData.incBalance(this.getId(), -usdAmount);
        // 記錄歷史：描述中標明匯率和換算金額
        String description = String.format("withdraw %.2f TWD (rate: %.2f, deducted: %.2f USD)",
                amount, exchangeRate, usdAmount);
        AccountData.addOneHistory(this.getId(), -usdAmount, "", description);
        return true;
    }

    /**
     * 覆寫 refresh：重新產生匯率並更新利息
     */
    @Override
    public void refresh() {
        this.exchangeRate = generateExchangeRate();
        super.refresh();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s (%.3f USD, rate: %.2f)",
                getId(), getType(), getStatus(), balance, exchangeRate);
    }
}
