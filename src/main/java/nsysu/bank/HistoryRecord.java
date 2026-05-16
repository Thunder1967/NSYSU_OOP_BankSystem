package nsysu.bank;

import org.bson.Document;

import java.util.Date;

/**
 * 交易紀錄類別：封裝單筆交易的所有資訊
 * 實作 Comparable 介面，讓紀錄可以按日期排序（最新的在前）
 */
public class HistoryRecord implements Comparable<HistoryRecord>{
    private final Date date;
    private final double amount;    // 正數=收入，負數=支出
    private final String accountId; // 本帳戶 ID
    private final String anotherId; // 交易對象的帳戶 ID
    private final String description;

    public HistoryRecord(Document doc,String accountId) {
        this.date = doc.getDate("date");
        this.amount = doc.getDouble("amount");
        this.anotherId = doc.getString("anotherId");
        this.description = doc.getString("description");
        this.accountId = accountId;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getAnotherId() {
        return anotherId;
    }

    public String getDescription() {
        return description;
    }

    public String getAccountId() {
        return accountId;
    }

    /** 排序規則：按日期遞減排序（最新的排在前面） */
    @Override
    public int compareTo(HistoryRecord other) {
        return other.date.compareTo(this.date);
    }

    /** 根據金額正負判斷交易方向，顯示「從 A 到 B」的格式 */
    @Override
    public String toString() {
        double absAmount = Math.abs(amount);
        if (anotherId == null || anotherId.isEmpty()) {
            String actionType = amount > 0 ? "Deposit" : "Withdraw";
            return String.format("[%s] %.2f (%s) - %s", date, absAmount, actionType, description);
        } else {
            if (amount > 0) {
                return String.format("[%s] %.2f (from %s to %s) - %s", date, absAmount, anotherId, accountId, description);
            } else {
                return String.format("[%s] %.2f (from %s to %s) - %s", date, absAmount, accountId, anotherId, description);
            }
        }
    }
}
